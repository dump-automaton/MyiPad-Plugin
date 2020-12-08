package com.dumpautomaton.myipadplugin;

import android.os.Bundle;
import android.os.Looper;

import android.app.Application;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class HookMyiPad implements IXposedHookLoadPackage {
    private static XSharedPreferences sharedPreferences = new XSharedPreferences("com.netspace.myipad");

    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) throws Exception {
        if (lpparam.packageName.equals("com.netspace.myipad")) {
            XposedBridge.log("[HookMyiPad]getting classLoader...");
            XposedHelpers.findAndHookMethod("s.h.e.l.l.S", lpparam.classLoader, "onCreate", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                    Application appClz = (Application) param.thisObject;
                    ClassLoader realClassLoader = appClz.getClassLoader();
                    hookNewActivity(realClassLoader);
                    hookHardwareInfo(realClassLoader);
                    hookAlertDialog(realClassLoader);
                    hookAddPluginPreferencesUI(realClassLoader);
                    hookNeedMDM(realClassLoader);
                    if (sharedPreferences.getBoolean("disable_auto_update", true)) {
                        hookAutoUpdate(realClassLoader);
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
        XposedHelpers.findAndHookMethod("com.netspace.library.activity.WifiConfigActivity", realClassLoader, "onStart", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Activity activity = (Activity) param.thisObject;
                activity.getFragmentManager().beginTransaction().add(android.R.id.content, new PluginPreferenceFragment()).commit();
            }
        });

        Class settingsActivityClasss = Class.forName("com.netspace.myipad.SettingsActivity", true, realClassLoader);
        XposedHelpers.findAndHookMethod(settingsActivityClasss, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Activity activity = (Activity) param.thisObject;
                activity.getFragmentManager().beginTransaction().add(android.R.id.content, new PluginPreferenceFragment()).commit();
            }
        });
    }

    private void hookNeedMDM(final ClassLoader realClassLoader) throws ClassNotFoundException {
        Class clazz = realClassLoader.loadClass("com.netspace.library.utilities.Utilities");
        Method m = XposedHelpers.findMethodExact(clazz, "isSkipELMCheck");

        XposedBridge.hookMethod(m, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Class baseAppClz = Class.forName("com.netspace.library.application.MyiBaseApplication", true, realClassLoader);
                Application baseApp = (Application) XposedHelpers.callStaticMethod(baseAppClz, "getInstance");
                Toast.makeText(baseApp, String.valueOf(param.getResult()), Toast.LENGTH_LONG).show();
                XposedHelpers.setBooleanField(baseApp, "mbNeedMDM", false);
            }
        });
    }

    private void hookAutoUpdate(ClassLoader realClassLoader) {
        XposedHelpers.findAndHookMethod("com.netspace.library.utilities.MyiUpdate2", realClassLoader, "CompareVersion", String.class, String.class, XC_MethodReplacement.returnConstant(0));
    }
}
