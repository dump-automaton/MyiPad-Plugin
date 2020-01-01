package com.dumpautomaton.myipadplugin;

import android.content.SharedPreferences;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.os.Bundle;

import android.app.Application;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.dumpautomaton.myipadplugin.ui.MainActivity;
import com.dumpautomaton.myipadplugin.ui.PluginPreferenceFragment;
import com.dumpautomaton.myipadplugin.utils.FileIOUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.security.cert.X509Certificate;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class HookMyiPad implements IXposedHookLoadPackage {
    enum CurrentAppType {
        UNKNOWN,
        MYIPAD,
        TEACHERPAD,
        MANAGER
    }
    private static final String TAG = "HookMyiPad";
    private static boolean safeMode = false;
    private static boolean runInVxp = false;

    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) throws Exception {
        if (lpparam.packageName.equals(BuildConfig.APPLICATION_ID)) {
            XposedHelpers.findAndHookMethod("com.dumpautomaton.myipadplugin.ui.MainActivity", lpparam.classLoader, "isActive", new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    return true;
                }
            });
        }
        if (!lpparam.packageName.contains("com.netspace")) {
            return;
        }
        safeMode = UtilsForHook.isSafeMode();
        if (System.getProperty("vxp") != null) {
            runInVxp = true;
        }
        final String packageName = lpparam.packageName;
        Method uncaughtException;
        try {
            uncaughtException = XposedHelpers.findMethodExact(Thread.class, "dispatchUncaughtException", Throwable.class);
        } catch (NoSuchMethodError e) {
            XposedBridge.log(e.toString());
            uncaughtException = XposedHelpers.findMethodExact("com.android.internal.os.RuntimeInit$UncaughtHandler", lpparam.classLoader, "uncaughtException", Thread.class, Throwable.class);
        }
        XposedBridge.hookMethod(uncaughtException, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                String stackTraceString = Log.getStackTraceString((Throwable) param.args[1]);
                File crashLogFile = UtilsForHook.getCrashLogTxtFile();
                String oldStacktrace = FileIOUtils.readFile2String(crashLogFile);
                if (oldStacktrace != null) {
                    stackTraceString += oldStacktrace;
                }
                FileIOUtils.writeFileFromString(crashLogFile, "------" + packageName + ":" + System.currentTimeMillis() + "\n" + stackTraceString);
                UtilsForHook.setSafeMode(true);
            }
        });
        XposedHelpers.findAndHookMethod("android.app.Instrumentation", lpparam.classLoader, "newApplication", ClassLoader.class, String.class, Context.class, new AppCreateHookCallback());
    }

    public class AppCreateHookCallback extends XC_MethodHook {
        @Override
        protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
            Log.e(TAG, "newApplication=" + param.getResult());
            Application app = (Application) param.getResult();
            String appName = app.getClass().getName();
            if (!appName.contains("com.netspace")) {
                return;
            }

            CurrentAppType currentAppType = CurrentAppType.UNKNOWN;
            switch (app.getPackageName()) {
                case "com.netspace.myipad":
                    currentAppType = CurrentAppType.MYIPAD;
                    break;
                case "com.netspace.teacherpad":
                    currentAppType = CurrentAppType.TEACHERPAD;
                    break;
                case "com.netspace.myimanager":
                    currentAppType = CurrentAppType.MANAGER;
                    break;
            }

            ClassLoader realClassLoader = app.getClassLoader();
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(app);

            // [essential] hook for preferences ui
            addPreferencesUi(realClassLoader, currentAppType);
            // [essential] hook for compatibility
            if (runInVxp || sharedPreferences.getBoolean("compatibility_with_vx", false)) {
                try {
                    hookForHighApi(realClassLoader);
                    hookBackgroundPatcher(realClassLoader);
                    sharedPreferences.edit().putBoolean("compatibility_with_vx", true).commit();
                } catch (Exception e) {
                    sharedPreferences.edit().putBoolean("compatibility_with_vx", false).commit();
                }
            }

            if (safeMode) {
                return;
            }

            if (currentAppType == CurrentAppType.MYIPAD) {
                if (sharedPreferences.getBoolean("disable_mdm", true)) {
                    hookELMActivation(realClassLoader);
                }
                if (sharedPreferences.getBoolean("disable_lock_screen", true)) {
                    hookLockScreen(realClassLoader);
                }
                if (sharedPreferences.getBoolean("disable_useless_service", true)) {
                    hookDisableUselessThread(realClassLoader);
                }
                if (sharedPreferences.getBoolean("in_private", true)) {
                    hookStatusReport(realClassLoader);
                }
                if (sharedPreferences.getBoolean("teacher_mode", false)) {
                    hookIsTeacher(realClassLoader);
                }
            }

            if (sharedPreferences.getBoolean("fake_hardware_info", true)) {
                String fakeHW = sharedPreferences.getString("fake_hardware_info_content", "");
                if (fakeHW.equals("")) {
                    fakeHW = UtilsForHook.getHardwareInfoWithoutHardware();
                }
                Log.e(TAG, "fakeHW: " + fakeHW);
                hookHardwareInfo(realClassLoader, fakeHW + '\n');
            }
            if (sharedPreferences.getBoolean("cancelable_dialog", true)) {
                hookAlertDialog(realClassLoader);
            }
            if (sharedPreferences.getBoolean("disable_auto_update", true)) {
                hookAutoUpdate(realClassLoader);
            }
            if (sharedPreferences.getBoolean("disable_ssl_pinning", false)) {
                hookSslPinning(realClassLoader);
            }
            if (sharedPreferences.getBoolean("use_external_pdf_viewer", false)) {
                hookLaunchPdf(realClassLoader, app);
            }
            if (sharedPreferences.getBoolean("allow_all_permissions", false)) {
                hookCheckPermission(realClassLoader);
            }
            if (sharedPreferences.getBoolean("fake_version", true)) {
                hookVersionName(realClassLoader, sharedPreferences.getString("fake_version_name", "5.2.3.52405"));
            }
            if (sharedPreferences.getBoolean("fake_wifi_info", true)) {
                hookFakeWifiInfo("null");
            }
            if (sharedPreferences.getBoolean("disable_im", true)) {
                hookDisableMessage(realClassLoader);
            }
        }
    }

    void hookAddPreferencesUiOnCreateActivity(String activityClzName, ClassLoader classLoader) throws ClassNotFoundException {
        Class<?> activityClz = Class.forName(activityClzName, true, classLoader);
        XposedHelpers.findAndHookMethod(activityClz, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Activity activity = (Activity) param.thisObject;
                if (safeMode) {
                    Toast.makeText(activity, "Currently in safe mode. No hooks will be applied.", Toast.LENGTH_LONG).show();
                }
                activity.getFragmentManager().beginTransaction().add(new PluginPreferenceFragment(), "pref").commit();
            }
        });
    }

    void addPreferencesUi(ClassLoader classLoader, CurrentAppType currentAppType) throws ClassNotFoundException {
        if (currentAppType == CurrentAppType.UNKNOWN) {
            return;
        }
        if (currentAppType == CurrentAppType.MANAGER) {
            hookAddPreferencesUiOnCreateActivity("com.netspace.myimanager.LoginActivity", classLoader);
        } else {
            PluginPreferenceFragment.dumpLogcatMethod = XposedHelpers.findMethodExact("com.netspace.library.utilities.Utilities", classLoader, "dumpLogcatToFile", String.class);
            hookAddPreferencesUiOnCreateActivity("com.netspace.library.activity.WifiConfigActivity", classLoader);
            if (currentAppType == CurrentAppType.MYIPAD) {
                hookAddPreferencesUiOnCreateActivity("com.netspace.myipad.SettingsActivity", classLoader);
            } else if (currentAppType == CurrentAppType.TEACHERPAD) {
                hookAddPreferencesUiOnCreateActivity("com.netspace.teacherpad.SettingsActivity", classLoader);
            }
        }
    }

    void hookHardwareInfo(final ClassLoader realClassLoader, String fakeHardwareInfo) throws ClassNotFoundException {
        final Class<?> clazz = realClassLoader.loadClass("com.netspace.library.utilities.HardwareInfo");
        Method m = XposedHelpers.findMethodExact(clazz, "getHardwareInfo", Context.class);
        XposedBridge.hookMethod(m, XC_MethodReplacement.returnConstant(fakeHardwareInfo));
    }

    void hookAlertDialog(ClassLoader realClassLoader) {
        XposedHelpers.findAndHookMethod("android.app.Dialog", realClassLoader, "setCancelable", boolean.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                return param.thisObject;
            }
        });
    }

    void hookELMActivation(ClassLoader classLoader) {
        XposedHelpers.findAndHookMethod("com.netspace.myipad.MyiPadApplication", classLoader, "onCreate", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                XposedHelpers.setBooleanField(param.thisObject, "mbNeedMDM", false);
            }
        });
    }

    void hookAutoUpdate(ClassLoader realClassLoader) {
        XposedHelpers.findAndHookMethod("com.netspace.library.utilities.MyiUpdate2", realClassLoader, "run", XC_MethodReplacement.returnConstant(null));
    }

    void hookStatusReport(ClassLoader classLoader) throws ClassNotFoundException {
        Class<?> imServiceClz = Class.forName("com.netspace.library.im.IMService", true, classLoader);
        XposedHelpers.findAndHookMethod(imServiceClz, "reportBasicFields", new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                XposedHelpers.setBooleanField(param.thisObject, "mbBasicStatusReported", true);
                return null;
            }
        });
        XposedHelpers.findAndHookMethod(imServiceClz, "reportStatus", String.class, String.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                Log.e(TAG, "Catch report:" + param.args[0] + " value :" + param.args[1]);
                return null;
            }
        });
        XposedHelpers.findAndHookMethod("com.netspace.library.struct.UserInfo", classLoader, "UserScore", String.class, String.class, XC_MethodReplacement.returnConstant(null));
        XposedHelpers.findAndHookMethod("com.netspace.myipad.im.handles.everyone.Status", classLoader, "getStatusJson", XC_MethodReplacement.returnConstant("{}"));
        XposedHelpers.findAndHookMethod("com.netspace.myipad.im.handles.everyone.Status", classLoader, "getStatus", XC_MethodReplacement.returnConstant(""));
        XposedHelpers.findAndHookMethod("com.netspace.myipad.im.WmIMThread", classLoader, "updateStatus", String.class, String.class, XC_MethodReplacement.returnConstant(null));
    }

    void hookDisableUselessThread(ClassLoader classLoader) {
        XposedHelpers.findAndHookMethod("com.netspace.library.threads.UsageDataUploadThread", classLoader, "run", XC_MethodReplacement.returnConstant(null));
        XposedHelpers.findAndHookMethod("com.netspace.myipad.threads.TimeLockThread", classLoader, "run", XC_MethodReplacement.returnConstant(null));
    }

    void hookLockScreen(ClassLoader classLoader) {
        XposedHelpers.findAndHookMethod("com.netspace.myipad.im.handles.teacherpad.LockUnlockScreen", classLoader, "lockScreen", boolean.class, XC_MethodReplacement.returnConstant(null));
    }

    void hookIsTeacher(ClassLoader classLoader) {
        XposedHelpers.findAndHookMethod("com.netspace.library.struct.UserInfo", classLoader, "isTeacher", XC_MethodReplacement.returnConstant(true));
    }

    void hookSslPinning(ClassLoader classLoader) {
        XposedHelpers.findAndHookMethod("com.netspace.library.utilities.SSLConnection$_FakeX509TrustManager", classLoader, "checkClientTrusted", X509Certificate[].class, String.class, XC_MethodReplacement.returnConstant(null));
        XposedHelpers.findAndHookMethod("com.netspace.library.utilities.SSLConnection$_FakeX509TrustManager", classLoader, "checkServerTrusted", X509Certificate[].class, String.class, XC_MethodReplacement.returnConstant(null));
    }

    void hookLaunchPdf(ClassLoader classLoader, final Context context) throws ClassNotFoundException {
        Class<?> customDocumentViewClz = Class.forName("com.netspace.library.controls.CustomDocumentView", true, classLoader);
        XposedHelpers.findAndHookMethod(customDocumentViewClz, "launchPDF", String.class, boolean.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                String pdfFullPath = (String) param.args[0];
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri uri = Uri.fromFile(new File(pdfFullPath));
                intent.setDataAndType(uri, "application/pdf");
                context.startActivity(intent);
                return null;
            }
        });
    }

    void hookCheckPermission(ClassLoader classLoader) {
        XposedHelpers.findAndHookMethod("com.netspace.library.struct.UserInfo", classLoader, "checkPermission", String.class, XC_MethodReplacement.returnConstant(true));
    }

    void hookForHighApi(ClassLoader classLoader) {
        XposedHelpers.findAndHookMethod("com.netspace.library.activity.AccountConfigActivity$ResourceTestThreads", classLoader, "run", new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                Activity activity = (Activity) XposedHelpers.getObjectField(param.thisObject, "this$0");
                activity.runOnUiThread((Runnable) XposedHelpers.getObjectField(activity, "mSuccessRunnable"));
                return null;
            }
        });
    }

    void hookBackgroundPatcher(ClassLoader classLoader) throws ClassNotFoundException {
        Class<?> backgroundPatcherClz = classLoader.loadClass("com.netspace.library.upgrade.BackgroundPatcher");
        XposedHelpers.findAndHookMethod(backgroundPatcherClz, "start", XC_MethodReplacement.returnConstant(null));
    }

    void hookVersionName(ClassLoader classLoader, final String fakeVersionName) {
        XposedHelpers.findAndHookMethod("android.app.ApplicationPackageManager", classLoader, "getPackageInfo", String.class, int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                PackageInfo pi = (PackageInfo) param.getResult();
                if (pi.packageName.contains("com.netspace")) {
                    pi.versionName = fakeVersionName;
                } else {
                    Log.d(TAG, "getting non-myi package info");
                }
            }
        });
    }

    void hookFakeWifiInfo(String fakeWifiSsid) {
        XposedHelpers.findAndHookMethod(WifiInfo.class, "getIpAddress", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                int originIp = (int) param.getResult();
                int originalPrefix = originIp % 0x1000000;
                int randomIpSuffix = (int) (System.currentTimeMillis() % 0xFF);
                param.setResult((randomIpSuffix * 0x1000000) + originalPrefix);
            }
        });
        XposedHelpers.findAndHookMethod(WifiInfo.class, "getSSID", XC_MethodReplacement.returnConstant(fakeWifiSsid));
        XposedHelpers.findAndHookMethod(WifiInfo.class, "getMacAddress", XC_MethodReplacement.returnConstant(null));
    }

    void hookDisableMessage(ClassLoader classLoader) {
        XposedHelpers.findAndHookMethod("com.netspace.library.threads.MessageWaitThread2", classLoader, "run", XC_MethodReplacement.returnConstant(null));
        XposedHelpers.findAndHookConstructor("com.netspace.library.im.IMHttpTransportLayer", classLoader, Context.class, String.class, String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                XposedHelpers.setObjectField(param.thisObject, "mMonitorRunnable", UtilsForHook.doNothingRunnable);
                XposedHelpers.setObjectField(param.thisObject, "mReconnectRunnable", UtilsForHook.doNothingRunnable);
            }
        });
    }
}
