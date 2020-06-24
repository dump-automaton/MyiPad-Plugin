package com.dumpautomaton.myipadplugin;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

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
                    hookAutoUpdate(realClassLoader);

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
                XposedBridge.log("[HookMyiPad]Hooked getHardwareInfo");

                String str = HardwareInfo.getHardwareInfo((Context) param.args[0]);
                param.setResult(str);
            }
        });
    }

    private void hookAutoUpdate(ClassLoader realClassLoader) {
        final TextView[] versionStatusTextView = new TextView[1];
        XposedHelpers.findAndHookMethod("com.netspace.library.utilities.MyiUpdate2", realClassLoader, "setTextView", TextView.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                versionStatusTextView[0] = (TextView) param.args[0];
            }
        });

        XposedHelpers.findAndHookMethod("com.netspace.library.utilities.MyiUpdate2", realClassLoader, "CompareVersion", String.class, String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if ((int) param.getResult() == 1) {
                    final String newVersionName = (String) param.args[1];
                    Timer timer = new Timer();// 实例化Timer类
                    timer.schedule(new TimerTask() {
                        public void run() {
                            if (versionStatusTextView[0] != null) {
                                versionStatusTextView[0].setText("New version: " + newVersionName);
                            }
                        }
                    }, 500);// 毫秒
                }
                param.setResult(0);
            }
        });
    }

    private String getHardwareInfoWithoutHardware(Context var0) {
        StringBuilder var2 = new StringBuilder();
        var2.append("PackageName: ");
        var2.append(var0.getPackageName());
        var2.append("\n");
        String var8 = var2.toString();
        String var4;
        StringBuilder var10 = new StringBuilder();
        var10.append(var8);
        var10.append("ClientVersion: ");
        var10.append(HardwareInfo.getVersionName(var0));
        var10.append("\n");
        var8 = var10.toString();
        var10 = new StringBuilder();
        var10.append(var8);
        var10.append("ClientSign: ");
        var10.append(HardwareInfo.getSign(var0));
        var10.append("\n");
        String var3 = var10.toString();
        var2 = new StringBuilder();
        var2.append(var3);
        var2.append("ClientPath: ");
        var2.append(HardwareInfo.getFilePath(var0));
        var2.append("\n");
        var8 = var2.toString();
        var4 = HardwareInfo.calculateMD5(new File(HardwareInfo.getFilePath(var0)));
        if (var4 != null) {
            var10 = new StringBuilder();
            var10.append(var8);
            var10.append("ClientMD5: ");
            var10.append(var4);
            var10.append("\n");
            var4 = var10.toString();
        } else {
            var4 = var8;
        }
        return var4;
    }
}
