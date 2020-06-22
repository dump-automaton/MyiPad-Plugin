package com.dumpautomaton.myipadplugin;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class HookMyiPad implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) throws Exception {
        // app load时调用
        // 匹配钩住的app的包名
        if (lpparam.packageName.equals("com.netspace.myipad")) {

            // 加载app的指定类
            final Class clazz = lpparam.classLoader.loadClass("com.netspace.library.utilities.HardwareInfo");

            // getDeclaredMethods()应该是返回包含此类中所有方法的数组，遍历此数组
            for (Method m : clazz.getDeclaredMethods()) {
                Log.e("[HookMyiPad]","Method: " + m.getReturnType().getName() + " " + m.getName());

                // 匹配遍历到的方法名称
                if (m.getName().contains("getHardwareInfo")) {
                    Log.e("[HookMyiPad]","GOT METHOD: " + clazz.getName() + " - " + m.getName());

                    XposedBridge.hookMethod(m, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            // 在方法结束时拦截，并执行此处代码

                            XposedBridge.log("[HookMyiPad]HOOKED METHOD: " + param.method.toString());
                            Log.e("Xposed","HOOKED :" + param.thisObject);
                            //Context context = (Context) AndroidAppHelper.currentApplication();
                            Toast.makeText((Context) param.args[0], "Hooked", Toast.LENGTH_LONG).show();
                            String str = HardwareInfo.getHardwareInfo((Context) param.args[0]);
                            XposedBridge.log(str);

                            // 设定返回值
                            param.setResult(str);
                        }
                    });
                }
            }
        }
    }
}
