package com.dumpautomaton.myipadplugin;

import android.app.Application;
import android.content.Context;

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
                    hookSkipElm(realClassLoader);
                }
            });
        }
    }

    private void hookHardwareInfo(final ClassLoader realClassLoader, final Context context) throws ClassNotFoundException {
        final Class<?> clazz = realClassLoader.loadClass("com.netspace.library.utilities.HardwareInfo");
        Method m = XposedHelpers.findMethodExact(clazz, "getHardwareInfo", Context.class);
        XposedBridge.hookMethod(m, XC_MethodReplacement.returnConstant(UtilsForHook.getHardwareInfoWithoutHardware()));
    }

    private void hookAlertDialog(ClassLoader realClassLoader) {
        XposedHelpers.findAndHookMethod("androidx.appcompat.app.AlertDialog.Builder", realClassLoader, "setCancelable", boolean.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                return param.thisObject;
            }
        });
    }

    private void hookSkipElm(ClassLoader classLoader) throws ClassNotFoundException {
        Class<?> clazz = classLoader.loadClass("com.netspace.library.utilities.Utilities");
        Method m = XposedHelpers.findMethodExact(clazz, "isSkipELMCheck");
        XposedBridge.hookMethod(m, XC_MethodReplacement.returnConstant(true));
    }
}
