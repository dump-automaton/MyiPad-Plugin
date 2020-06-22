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

        // getDeclaredMethods()应该是返回包含此类中所有方法的数组，遍历此数组
        for (Method m : clazz.getDeclaredMethods()) {

            // 匹配遍历到的方法名称
            if (m.getName().contains("getHardwareInfo")) {
                Log.e("[HookMyiPad]", "GOT METHOD: " + clazz.getName() + " - " + m.getName());
                Toast.makeText(context, "Got method: " + clazz.getName() + " - " + m.getName(), Toast.LENGTH_LONG).show();
                XposedBridge.hookMethod(m, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        // 在方法结束时拦截，并执行此处代码
                        //Context context = (Context) AndroidAppHelper.currentApplication();
                        Toast.makeText((Context) param.args[0], "Hooked", Toast.LENGTH_LONG).show();
                        //String str = HardwareInfo.getHardwareInfo((Context) param.args[0]);
                        //XposedBridge.log(str);

                        // 设定返回值
                        param.setResult("\n");
                    }
                });
            }
        }
    }
}
