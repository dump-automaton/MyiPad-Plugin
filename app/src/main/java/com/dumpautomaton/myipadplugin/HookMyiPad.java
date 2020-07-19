package com.dumpautomaton.myipadplugin;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class HookMyiPad implements IXposedHookLoadPackage, IXposedHookZygoteInit {
    static XSharedPreferences pluginPreferences;
    static boolean applyChangesInstantly = false;

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
        Toast.makeText(context, getPreferencesString("pref_hardware_info"), Toast.LENGTH_LONG).show();
        // 加载app的指定类
        final Class clazz = realClassLoader.loadClass("com.netspace.library.utilities.HardwareInfo");

        Method m = XposedHelpers.findMethodExact(clazz, "getHardwareInfo", Context.class);
        XposedBridge.hookMethod(m, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                XposedBridge.log("[HookMyiPad]Hooked getHardwareInfo");

                if (getPreferencesBoolean("skip_hardware_certification")) {
                    String pref = getPreferencesString("pref_hardware_info");
                    String res = "";
                    if (pref.equals("p350")) {
                        res = HardwareInfo.getHardwareInfo((Context) param.args[0]);
                    } else if (pref.equals("min_5.2.3.52303")) {
                        res = getHardwareInfoWithoutHardware((Context) param.args[0]);
                    }
                    param.setResult(res);
                }
            }
        });
    }

    private void hookAutoUpdate(ClassLoader realClassLoader) {
        XposedHelpers.findAndHookMethod("com.netspace.library.utilities.MyiUpdate2", realClassLoader, "CompareVersion", String.class, String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (getPreferencesBoolean("disable_auto_update")) {
                    param.setResult(0);
                }
            }
        });
    }

    private String getHardwareInfoWithoutHardware(Context var0) {
        StringBuilder var2 = new StringBuilder();
        var2.append("PackageName: ");
        var2.append("com.netspace.myipad");
        var2.append("\n");
        String var8 = var2.toString();
        String var4;
        StringBuilder var10 = new StringBuilder();
        var10.append(var8);
        var10.append("ClientVersion: ");
        var10.append("5.2.3.52303");
        var10.append("\n");
        var8 = var10.toString();
        var10 = new StringBuilder();
        var10.append(var8);
        var10.append("ClientSign: ");
        var10.append("308203253082020da00302010202040966f52d300d06092a864886f70d01010b05003042310b300906035504061302434e310f300d060355040713064e696e67426f3122302006035504");
        var10.append("0a13194e696e67426f2052756959694b654a6920436f2e204c74642e3020170d3132313231313130313133355a180f32303632313132393130313133355a3042310b300906035504061302434e310");
        var10.append("f300d060355040713064e696e67426f31223020060355040a13194e696e67426f2052756959694b654a6920436f2e204c74642e30820122300d06092a864886f70d01010105000382010f003082010");
        var10.append("a0282010100abf2c60e5fcb7776da3d22c3180e284da9c4e715cec2736646da086cbf979a7f74bc147167f0f32ef0c52458e9183f0dd9571d7971e49564c00fbfd30bef3ca9a2d52bffcd0142c72e10fac1");
        var10.append("58cb62c7bc7e9e17381a555ad7d39a24a470584a0e6aafdce2e4d6877847b15cbf4de89e3e4e71b11dca9920843ccc055acf8781db29bdaf3f06e16f055bf579a35ae3adb4d1149f8d43d90add54596a");
        var10.append("cef8e4a28905f9f19fc0aa7fda9e8d56aa63db5d8d5e0fc4c536629f0a25a44429c699318329af6a3e869dd5e8289c78f5");
        var10.append("\n");
        String var3 = var10.toString();
        var2 = new StringBuilder();
        var2.append(var3);
        var2.append("ClientPath: ");
        var2.append("/data/app/com.netspace.myipad/base.apk");
        var2.append("\n");
        var8 = var2.toString();
        var4 = HardwareInfo.calculateMD5(new File(HardwareInfo.getFilePath(var0)));
        if (var4 != null) {
            var10 = new StringBuilder();
            var10.append(var8);
            var10.append("ClientMD5: ");
            var10.append("d641121123578f86e519e5b422d9971e");
            var10.append("\n");
            var4 = var10.toString();
        } else {
            var4 = var8;
        }
        return var4;
    }

    private boolean getPreferencesBoolean(String key) {
        if (applyChangesInstantly) {
            pluginPreferences.reload();
        }
        return pluginPreferences.getBoolean(key, false);
    }

    private String getPreferencesString(String key) {
        if (applyChangesInstantly) {
            pluginPreferences.reload();
        }
        return pluginPreferences.getString(key, "0");
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            File prefsFileProt =
                    new File("/data/user_de/0/com.dumpautomaton.myipadplugin/shared_prefs/com.dumpautomaton.myipadplugin_preferences.xml");
            pluginPreferences = new XSharedPreferences(prefsFileProt);
        } else {
            pluginPreferences = new XSharedPreferences(BuildConfig.APPLICATION_ID);
        }

        if (pluginPreferences.getBoolean("enable_apply_changes_instantly", false)) {
            XposedBridge.log("[HookMyiPad]Apply changes instantly enabled");
            applyChangesInstantly = true;
        }
    }
}
