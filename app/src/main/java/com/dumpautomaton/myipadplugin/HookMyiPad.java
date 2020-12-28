package com.dumpautomaton.myipadplugin;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import android.app.Application;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.dumpautomaton.myipadplugin.ui.MyiPadPluginPreferenceFragment;
import com.dumpautomaton.myipadplugin.ui.PluginPreferenceFragment;
import com.dumpautomaton.myipadplugin.utils.FileIOUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.security.cert.X509Certificate;
import java.util.Calendar;

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
        TEACHERPAD
    }
    private static final String TAG = "HookMyiPad";
    private static boolean isSafeMode = false;

    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) throws Exception {
        if (!lpparam.packageName.contains("com.netspace")) {
            return;
        }
        File safeModeFile = new File("/storage/emulated/0/plugin_safe_mode.txt");
        isSafeMode = safeModeFile.exists();
        XposedHelpers.findAndHookMethod(Thread.class, "dispatchUncaughtException", Throwable.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                String stackTraceString = Log.getStackTraceString((Throwable) param.args[0]);
                FileIOUtils.writeFileFromString("/storage/emulated/0/MyiPad_Plugin_Crash_" + Calendar.getInstance().getTimeInMillis() + ".txt", stackTraceString);
                FileIOUtils.writeFileFromString("/storage/emulated/0/plugin_safe_mode.txt", "delete me to exit safe mode");
            }
        });
        XposedHelpers.findAndHookMethod("android.app.Instrumentation", lpparam.classLoader, "newApplication", ClassLoader.class, String.class, Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Log.e(TAG, "newApplication=" + param.getResult());
                Application app = (Application) param.getResult();
                if (!app.getClass().getName().contains("com.netspace")) {
                    return;
                }

                CurrentAppType currentAppType = CurrentAppType.UNKNOWN;
                if (app.getPackageName().equals("com.netspace.myipad")) {
                    currentAppType = CurrentAppType.MYIPAD;
                } else if (app.getPackageName().equals("com.netspace.teacherpad")) {
                    currentAppType = CurrentAppType.TEACHERPAD;
                }

                ClassLoader realClassLoader = app.getClassLoader();
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(app);

                addPreferencesUi(realClassLoader, currentAppType);

                if (isSafeMode) {
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
                        hookDisableTimeLockThread(realClassLoader);
                    }
                    if (sharedPreferences.getBoolean("in_private", true)) {
                        hookStatusReport(realClassLoader);
                    }
                    if (sharedPreferences.getBoolean("teacher_mode", false)) {
                        hookIsTeacher(realClassLoader);
                    }
                }

                if (sharedPreferences.getBoolean("fake_hardware_info", true)) {
                    hookHardwareInfo(realClassLoader, sharedPreferences.getString("fake_hardware_info_content", ""));
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
                if (sharedPreferences.getBoolean("high_api_version_compatibility", false)) {
                    hookForHighApi(realClassLoader);
                }
            }
        });
    }

    private static class AddPreferenceHookCallback extends XC_MethodHook {
        private final CurrentAppType currentAppType;
        public AddPreferenceHookCallback(CurrentAppType currentAppType) {
            this.currentAppType = currentAppType;
        }
        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            Activity activity = (Activity) param.thisObject;
            if (isSafeMode) {
                Toast.makeText(activity, "Currently in safe mode. No hooks will be applied.", Toast.LENGTH_LONG).show();
            }
            PreferenceFragment preferenceFragment;
            switch (currentAppType) {
                case MYIPAD:
                    preferenceFragment = new MyiPadPluginPreferenceFragment();
                    break;
                case TEACHERPAD:
                    preferenceFragment = new PluginPreferenceFragment();
                    break;
                default:
                    return;
            }
            activity.getFragmentManager().beginTransaction().add(preferenceFragment, "pref").commit();
        }
    }

    void addPreferencesUi(ClassLoader classLoader, CurrentAppType currentAppType) throws ClassNotFoundException {
        if (currentAppType == CurrentAppType.UNKNOWN) {
            return;
        } else {
            PluginPreferenceFragment.dumpLogcatMethod = XposedHelpers.findMethodExact("com.netspace.library.utilities.Utilities", classLoader, "dumpLogcatToFile", String.class);
            XposedHelpers.findAndHookMethod("com.netspace.library.activity.WifiConfigActivity", classLoader, "onStart", new AddPreferenceHookCallback(currentAppType));
            String settingsActivityClzName = "";
            if (currentAppType == CurrentAppType.MYIPAD) {
                settingsActivityClzName = "com.netspace.myipad.SettingsActivity";
            } else if (currentAppType == CurrentAppType.TEACHERPAD) {
                settingsActivityClzName = "com.netspace.teacherpad.SettingsActivity";
            }
            Class<?> settingsActivityClz = Class.forName(settingsActivityClzName, true, classLoader);
            XposedHelpers.findAndHookMethod(settingsActivityClz, "onCreate", Bundle.class, new AddPreferenceHookCallback(currentAppType));
        }
    }

    void hookHardwareInfo(final ClassLoader realClassLoader, String fakeHardwareInfo) throws ClassNotFoundException {
        final Class<?> clazz = realClassLoader.loadClass("com.netspace.library.utilities.HardwareInfo");
        Method m = XposedHelpers.findMethodExact(clazz, "getHardwareInfo", Context.class);
        XposedBridge.hookMethod(m, XC_MethodReplacement.returnConstant(fakeHardwareInfo.equals("") ? UtilsForHook.getHardwareInfoWithoutHardware() : fakeHardwareInfo));
    }

    void hookAlertDialog(ClassLoader realClassLoader) {
        XposedHelpers.findAndHookMethod("android.app.Dialog", realClassLoader, "setCancelable", boolean.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                return param.thisObject;
            }
        });
    }

    void hookELMActivation(ClassLoader realClassLoader) throws ClassNotFoundException {
        Class<?> clazz = realClassLoader.loadClass("com.netspace.library.utilities.Utilities");
        Method m = XposedHelpers.findMethodExact(clazz, "isSkipELMCheck");
        XposedBridge.hookMethod(m, XC_MethodReplacement.returnConstant(true));
    }

    void hookAutoUpdate(ClassLoader realClassLoader) {
        XposedHelpers.findAndHookMethod("com.netspace.library.utilities.MyiUpdate2", realClassLoader, "CompareVersion", String.class, String.class, XC_MethodReplacement.returnConstant(0));
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
    }

    void hookDisableTimeLockThread(ClassLoader classLoader) {
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
}
