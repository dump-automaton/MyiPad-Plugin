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
    public void handleLoadPackage(LoadPackageParam lpparam) throws Exception{
        if (lpparam.packageName.equals("com.netspace.myipad")) {
            //XposedBridge.log("[HookMyiPad]Loaded App:" + lpparam.packageName);
            final Class clazz = lpparam.classLoader.loadClass("com.netspace.library.utilities.HardwareInfo");
            //XposedBridge.log("[HookMyiPad]Hooked Class: " + clazz.getName());

            for (Method m : clazz.getDeclaredMethods()) {
                //XposedBridge.log("[HookMyiPad]Method: " + m.getReturnType().getName() + " " + m.getName());
                Log.e("[HookMyiPad]","Method: " + m.getReturnType().getName() + " " + m.getName());

                // Check method name
                if (m.getName().contains("getHardwareInfo")) {
                    //XposedBridge.log("[HookMyiPad]GOT METHOD: " + clazz.getName() + " - " + m.getName());
                    Log.e("[HookMyiPad]","GOT METHOD: " + clazz.getName() + " - " + m.getName());

                    XposedBridge.hookMethod(m, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            XposedBridge.log("[HookMyiPad]HOOKED METHOD: " + param.method.toString());
                            Log.e("Xposed","HOOKED :" + param.thisObject);
                            //Context context = (Context) AndroidAppHelper.currentApplication();
                            //Toast.makeText(context, param.thisObject.toString(), Toast.LENGTH_LONG);
                            String str = HardwareInfo.getHardwareInfo((Context) param.args[0]);
                            XposedBridge.log(str);
                            param.setResult(str);
                        }
                    });
                }
            }
        }
    }
}
