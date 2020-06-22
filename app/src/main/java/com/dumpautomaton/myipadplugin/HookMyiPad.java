package com.dumpautomaton.myipadplugin;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.util.Log;
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
            XposedHelpers.findAndHookMethod("s.h.e.l.l.S", lpparam.classLoader, "attachBaseContext", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    //获取到Context对象，通过这个对象来获取classloader
                    Context context = (Context) param.args[0];

                    Toast.makeText(context, "Getting ClassLoader...", Toast.LENGTH_LONG).show();
                    //获取classloader，之后hook加固后的就使用这个classloader
                    ClassLoader realClassLoader = context.getClassLoader();
                    //下面就是将classloader修改成壳的classloader就可以成功的hook了
                    hookHardwareInfo(realClassLoader, context);
                    XposedBridge.log("[HookMyiPad]OK");
                }
            });
        }
    }

    private void hookHardwareInfo(ClassLoader realClassLoader, Context context) throws ClassNotFoundException {
        Toast.makeText(context, "ClassLoader get!", Toast.LENGTH_LONG).show();
        // 加载app的指定类
        final Class clazz = realClassLoader.loadClass("com.netspace.library.utilities.HardwareInfo");

        Method m = XposedHelpers.findMethodExact(clazz, "getHardwareInfo", Context.class);
        XposedBridge.hookMethod(m, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Toast.makeText((Context) param.args[0], "Hooked", Toast.LENGTH_LONG).show();
                param.setResult("\n");
            }
        });

        // 由于getHardwareInfo是静态方法，这样hook可能会出现未知问题
        /*
        XposedHelpers.findAndHookMethod(clazz, "getHardwareInfo", String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Toast.makeText((Context) param.args[0], "Result replaced", Toast.LENGTH_LONG).show();
                param.setResult("\n");
            }
        });
         */
    }
}
