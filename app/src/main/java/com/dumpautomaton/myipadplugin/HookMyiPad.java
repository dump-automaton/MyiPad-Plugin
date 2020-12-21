package com.dumpautomaton.myipadplugin;

import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;

import android.app.Application;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.dumpautomaton.myipadplugin.ui.PluginPreferenceFragment;

import java.io.File;
import java.lang.reflect.Method;
import java.security.cert.X509Certificate;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class HookMyiPad implements IXposedHookLoadPackage {
    private static final String TAG = "HookMyiPad";
    private static final XSharedPreferences sharedPreferences = new XSharedPreferences("com.netspace.myipad");

    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) throws Exception {
        if (lpparam.packageName.equals("com.netspace.myipad")) {
            XposedBridge.log("[HookMyiPad]getting classLoader...");
            XposedHelpers.findAndHookMethod("android.app.Instrumentation", lpparam.classLoader, "newApplication", ClassLoader.class, String.class, Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Log.e(TAG, "Application=" + param.getResult());
                    Application app = (Application) param.getResult();
                    ClassLoader realClassLoader = app.getClassLoader();

                    hookNewActivity(realClassLoader);
                    hookHardwareInfo(realClassLoader);
                    hookAddPluginPreferencesUI(realClassLoader);
                    if (sharedPreferences.getBoolean("cancelable_dialog", true)) {
                        hookAlertDialog(realClassLoader);
                    }
                    if (sharedPreferences.getBoolean("disable_mdm", true)) {
                        hookELMActivation(realClassLoader);
                    }
                    if (sharedPreferences.getBoolean("disable_auto_update", true)) {
                        hookAutoUpdate(realClassLoader);
                    }
                    if (sharedPreferences.getBoolean("in_private", true)) {
                        hookStatusReport(realClassLoader);
                    }
                    if (sharedPreferences.getBoolean("show_direct_im_ip", true)) {
                        hookShowDirectImIp(realClassLoader, app);
                    }
                    if (sharedPreferences.getBoolean("disable_lock_screen", true)) {
                        hookLockScreen(realClassLoader);
                    }
                    if (sharedPreferences.getBoolean("disable_useless_service", true)) {
                        hookDisableTimeLockThread(realClassLoader);
                    }
                    if (sharedPreferences.getBoolean("teacher_mode", false)) {
                        hookIsTeacher(realClassLoader);
                    }
                    if (sharedPreferences.getBoolean("disable_ssl_pinning", false)) {
                        hookSslPinning(realClassLoader);
                    }
                    if (sharedPreferences.getBoolean("use_external_pdf_viewer", false)) {
                        hookLaunchPdf(realClassLoader);
                    }
                }
            });
        }
    }

    private void hookNewActivity(ClassLoader realClassLoader) throws ClassNotFoundException {
        Class<?> instrumentationClass = XposedHelpers.findClass("android.app.Instrumentation", realClassLoader);
        Method method = XposedHelpers.findMethodExact(instrumentationClass, "newActivity",
                ClassLoader.class, String.class, Intent.class);
        XposedBridge.hookMethod(method, new ActivityHook());
    }

    private void hookHardwareInfo(final ClassLoader realClassLoader) throws ClassNotFoundException {
        final Class<?> clazz = realClassLoader.loadClass("com.netspace.library.utilities.HardwareInfo");
        Method m = XposedHelpers.findMethodExact(clazz, "getHardwareInfo", Context.class);
        XposedBridge.hookMethod(m, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                final Activity activity = ActivityHook.getCurrentActivity();
                if (Looper.myLooper() == null) {
                    Looper.prepare();
                }
                String result = UtilsForHook.showSyncEditDialog(Looper.myLooper(), "Plugin",
                        (String)param.getResult(), UtilsForHook.getHardwareInfoWithoutHardware(), activity);
                if (result != null && !result.equals("")) {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(activity, "Hooking!", Toast.LENGTH_LONG).show();
                        }
                    });

                    try {
                        param.setResult(result);
                    } catch (Exception e) {
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(activity, "Hook failed!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
        });
    }

    private void hookAlertDialog(ClassLoader realClassLoader) {
        XposedHelpers.findAndHookMethod("androidx.appcompat.app.AlertDialog.Builder", realClassLoader, "setCancelable", boolean.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                return param.thisObject;
            }
        });
    }

    private void hookAddPluginPreferencesUI(final ClassLoader realClassLoader) throws ClassNotFoundException {
        PluginPreferenceFragment.dumpLogcatMethod = XposedHelpers.findMethodExact("com.netspace.library.utilities.Utilities", realClassLoader, "dumpLogcatToFile", String.class);
        XposedHelpers.findAndHookMethod("com.netspace.library.activity.WifiConfigActivity", realClassLoader, "onStart", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Activity activity = (Activity) param.thisObject;
                activity.getFragmentManager().beginTransaction().add(new PluginPreferenceFragment(), "pref").commit();
            }
        });

        Class<?> settingsActivityClz = Class.forName("com.netspace.myipad.SettingsActivity", true, realClassLoader);
        XposedHelpers.findAndHookMethod(settingsActivityClz, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Activity activity = (Activity) param.thisObject;
                activity.getFragmentManager().beginTransaction().add(new PluginPreferenceFragment(), "pref").commit();
            }
        });
    }

    private void hookELMActivation(ClassLoader realClassLoader) throws ClassNotFoundException {
        Class<?> clazz = realClassLoader.loadClass("com.netspace.library.utilities.Utilities");
        Method m = XposedHelpers.findMethodExact(clazz, "isSkipELMCheck");

        XposedBridge.hookMethod(m, XC_MethodReplacement.returnConstant(true));
    }

    private void hookAutoUpdate(ClassLoader realClassLoader) {
        XposedHelpers.findAndHookMethod("com.netspace.library.utilities.MyiUpdate2", realClassLoader, "CompareVersion", String.class, String.class, XC_MethodReplacement.returnConstant(0));
    }

    private void hookStatusReport(ClassLoader classLoader) throws ClassNotFoundException {
        Class<?> imServiceClz = Class.forName("com.netspace.library.im.IMService", true, classLoader);
        XposedHelpers.findAndHookMethod(imServiceClz, "reportBasicFields", new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                XposedHelpers.setBooleanField(param.thisObject, "mbBasicStatusReported", true);
                return null;
            }
        });

        XposedHelpers.findAndHookMethod("com.netspace.library.struct.UserInfo", classLoader, "UserScore", String.class, String.class, XC_MethodReplacement.returnConstant(null));

        XposedHelpers.findAndHookMethod("com.netspace.myipad.im.handles.everyone.Status", classLoader, "getStatusJson", XC_MethodReplacement.returnConstant("{}"));
    }

    private void hookShowDirectImIp(ClassLoader classLoader, final Context context) throws ClassNotFoundException {
        Class<?> imServiceClz = Class.forName("com.netspace.library.im.IMService", true, classLoader);
        XposedHelpers.findAndHookMethod(imServiceClz, "reportStatus", String.class, String.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                Log.e(TAG, "Catch report:" + param.args[0] + " value :" + param.args[1]);
                String key = (String)param.args[0];
                if (key.equalsIgnoreCase("teacherdirectim")) {
                    PluginPreferenceFragment.screenCastURL = (String)param.args[1];
                }
                return null;
            }
        });
    }

    private void hookDisableTimeLockThread(ClassLoader classLoader) {
        XposedHelpers.findAndHookMethod("com.netspace.myipad.threads.TimeLockThread", classLoader, "run", XC_MethodReplacement.returnConstant(null));
    }

    private void hookLockScreen(ClassLoader classLoader) {
        XposedHelpers.findAndHookMethod("com.netspace.myipad.im.handles.teacherpad.LockUnlockScreen", classLoader, "lockScreen", boolean.class, XC_MethodReplacement.returnConstant(null));
    }

    private void hookIsTeacher(ClassLoader classLoader) {
        XposedHelpers.findAndHookMethod("com.netspace.library.struct.UserInfo", classLoader, "isTeacher", XC_MethodReplacement.returnConstant(true));
    }

    private void hookSslPinning(ClassLoader classLoader) {
        XposedHelpers.findAndHookMethod("com.netspace.library.utilities.SSLConnection$_FakeX509TrustManager", classLoader, "checkClientTrusted", X509Certificate[].class, String.class, XC_MethodReplacement.returnConstant(null));
        XposedHelpers.findAndHookMethod("com.netspace.library.utilities.SSLConnection$_FakeX509TrustManager", classLoader, "checkServerTrusted", X509Certificate[].class, String.class, XC_MethodReplacement.returnConstant(null));
    }

    private void hookLaunchPdf(ClassLoader classLoader) throws ClassNotFoundException {
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
                ActivityHook.getCurrentActivity().startActivity(intent);
                return null;
            }
        });
    }
}
