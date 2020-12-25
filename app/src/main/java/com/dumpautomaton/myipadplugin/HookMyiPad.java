package com.dumpautomaton.myipadplugin;

import android.app.Application;
import android.content.Context;
import android.os.Looper;

import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class HookMyiPad implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) throws Exception {
        if (lpparam.packageName.equals("com.netspace.myipad")) {
            XposedHelpers.findAndHookMethod("android.app.Instrumentation", lpparam.classLoader, "newApplication", ClassLoader.class, String.class, Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Application app = (Application) param.getResult();
                    if (!app.getClass().getName().contains("com.netspace")) {
                        return;
                    }
                    ClassLoader realClassLoader = app.getClassLoader();
                    hookHardwareInfo(realClassLoader, app);
                    hookAlertDialog(realClassLoader);
                }
            });
        }
    }

    private void hookHardwareInfo(final ClassLoader realClassLoader, final Context context) throws ClassNotFoundException {
        final Class<?> clazz = realClassLoader.loadClass("com.netspace.library.utilities.HardwareInfo");
        Method m = XposedHelpers.findMethodExact(clazz, "getHardwareInfo", Context.class);
        XposedBridge.hookMethod(m, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (Looper.myLooper() == null) {
                    Looper.prepare();
                }
                String result = UtilsForHook.showSyncEditDialog(Looper.myLooper(), "Plugin",
                        (String) param.getResult(), UtilsForHook.getHardwareInfoWithoutHardware(), context);
                if (result != null && !result.equals("")) {
                    param.setResult(result);
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
}
