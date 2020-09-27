package com.dumpautomaton.myipadplugin;

import android.os.Looper;

import android.app.AndroidAppHelper;
import android.app.Application;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
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
                    
                    hookWifiConfigActivity(realClassLoader);
                    //hookHardwareInfo(realClassLoader);
                }
            });
        }
    }

    private void hookHardwareInfo(ClassLoader realClassLoader) throws ClassNotFoundException {
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
    
    private void hookWifiConfigActivity(final ClassLoader realClassLoader) {
        XposedHelpers.findAndHookMethod("com.netspace.library.activity.WifiConfigActivity", realClassLoader, "onStart", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                final Activity activity = (Activity) param.thisObject;
                
                new Thread() {
                    public void run() {
                        Looper.prepare();
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle("Plugin");
                        builder.setMessage("Skip HW Authentication?");
                        //点击对话框以外的区域是否让对话框消失
                        builder.setCancelable(true);
                        //设置正面按钮
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Hook getHardwareInfo()
                                try {
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
                                } catch (Exception e) {
                                    activity.runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(activity, "Hook failed!", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        
                        builder.create().show();
                        Looper.loop();
                    }
                }.start();
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
