package com.dumpautomaton.myipadplugin;

import android.os.Handler;
import android.os.Looper;

import android.app.Application;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.widget.Toast;

import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class HookMyiPad implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) throws Exception {
        // app load时调用
        // 匹配钩住的app的包名
        if (lpparam.packageName.equals("com.netspace.myipad")) {
            XposedBridge.log("[HookMyiPad]getting classLoader...");
            XposedHelpers.findAndHookMethod("s.h.e.l.l.S", lpparam.classLoader, "onCreate", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                    Application appClz = (Application) param.thisObject;
                    ClassLoader realClassLoader = appClz.getClassLoader();
                    hookNewActivity(realClassLoader);
                    hookHardwareInfo(realClassLoader);
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
        final Class clazz = realClassLoader.loadClass("com.netspace.library.utilities.HardwareInfo");
        Method m = XposedHelpers.findMethodExact(clazz, "getHardwareInfo", Context.class);
        XposedBridge.hookMethod(m, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                final Activity activity = ActivityHook.getCurrentActivity();
                if (Looper.myLooper() == null)
                    Looper.prepare();
                if (UtilsForHook.showSyncBinaryDialog("Plugin", "Skip HW Authentication?", activity)) {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(activity, "Hooking!", Toast.LENGTH_LONG).show();
                        }
                    });
                    //setResult
                    try {
                        XposedBridge.log("[HookMyiPad]Hooked getHardwareInfo");
                        String str = UtilsForHook.getHardwareInfoWithoutHardware();
                        param.setResult(str);
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
}
