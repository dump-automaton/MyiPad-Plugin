package com.dumpautomaton.myipadplugin;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class HardwareInfo {
    public static String getHardwareInfo(Context var0) {
        WifiManager var1 = (WifiManager) var0.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        StringBuilder var2 = new StringBuilder();
        var2.append("");
        var2.append("BOARD: ");
        var2.append(Build.BOARD);
        var2.append("\n");
        String var3 = var2.toString();
        var2 = new StringBuilder();
        var2.append(var3);
        var2.append("BOOTLOADER: ");
        var2.append(Build.BOOTLOADER);
        var2.append("\n");
        String var8 = var2.toString();
        StringBuilder var10 = new StringBuilder();
        var10.append(var8);
        var10.append("BRAND: ");
        var10.append(Build.BRAND);
        var10.append("\n");
        var8 = var10.toString();
        var10 = new StringBuilder();
        var10.append(var8);
        var10.append("CPU_ABI: ");
        var10.append(Build.CPU_ABI);
        var10.append("\n");
        var8 = var10.toString();
        var10 = new StringBuilder();
        var10.append(var8);
        var10.append("CPU_ABI2: ");
        var10.append(Build.CPU_ABI2);
        var10.append("\n");
        var3 = var10.toString();
        var2 = new StringBuilder();
        var2.append(var3);
        var2.append("DEVICE: ");
        var2.append(Build.DEVICE);
        var2.append("\n");
        var3 = var2.toString();
        var2 = new StringBuilder();
        var2.append(var3);
        var2.append("DISPLAY: ");
        var2.append(Build.DISPLAY);
        var2.append("\n");
        var8 = var2.toString();
        var10 = new StringBuilder();
        var10.append(var8);
        var10.append("FINGERPRINT: ");
        var10.append(Build.FINGERPRINT);
        var10.append("\n");
        var8 = var10.toString();
        var10 = new StringBuilder();
        var10.append(var8);
        var10.append("HARDWARE: ");
        var10.append(Build.HARDWARE);
        var10.append("\n");
        var3 = var10.toString();
        var2 = new StringBuilder();
        var2.append(var3);
        var2.append("HOST: ");
        var2.append(Build.HOST);
        var2.append("\n");
        var8 = var2.toString();
        var10 = new StringBuilder();
        var10.append(var8);
        var10.append("ID: ");
        var10.append(Build.ID);
        var10.append("\n");
        var3 = var10.toString();
        var2 = new StringBuilder();
        var2.append(var3);
        var2.append("MANUFACTURER: ");
        var2.append(Build.MANUFACTURER);
        var2.append("\n");
        var8 = var2.toString();
        var10 = new StringBuilder();
        var10.append(var8);
        var10.append("MODEL: ");
        var10.append(Build.MODEL);
        var10.append("\n");
        var8 = var10.toString();
        var10 = new StringBuilder();
        var10.append(var8);
        var10.append("PRODUCT: ");
        var10.append(Build.PRODUCT);
        var10.append("\n");
        var3 = var10.toString();
        var8 = var3;
        if (Build.getRadioVersion() != null) {
            var2 = new StringBuilder();
            var2.append(var3);
            var2.append("RADIO: ");
            var2.append(Build.getRadioVersion());
            var2.append("\n");
            var8 = var2.toString();
        }

        if (VERSION.SDK_INT >= 26) {
            var10 = new StringBuilder();
            var10.append(var8);
            var10.append("SERIAL: ");
//var10.append(Build.getSerial());
            var10.append("R22KB003PJZ");
            var10.append("\n");
            var8 = var10.toString();
        } else {
            var10 = new StringBuilder();
            var10.append(var8);
            var10.append("SERIAL: ");
//var10.append(Build.SERIAL);
            var10.append("R22KB003PJZ");
            var10.append("\n");
            var8 = var10.toString();
        }

        var10 = new StringBuilder();
        var10.append(var8);
        var10.append("TAGS: ");
        var10.append(Build.TAGS);
        var10.append("\n");
        var8 = var10.toString();
        var10 = new StringBuilder();
        var10.append(var8);
        var10.append("TIME: ");
        var10.append(Build.TIME);
        var10.append("\n");
        var3 = var10.toString();
        var2 = new StringBuilder();
        var2.append(var3);
        var2.append("TYPE: ");
        var2.append(Build.TYPE);
        var2.append("\n");
        var3 = var2.toString();
        var2 = new StringBuilder();
        var2.append(var3);
        var2.append("UNKNOWN: unknown\n");
        var8 = var2.toString();
        var10 = new StringBuilder();
        var10.append(var8);
        var10.append("USER: ");
        var10.append(Build.USER);
        var10.append("\n");
        var3 = var10.toString();
        var2 = new StringBuilder();
        var2.append(var3);
        var2.append("VERSION_CODENAME: ");
        var2.append(VERSION.CODENAME);
        var2.append("\n");
        var3 = var2.toString();
        var2 = new StringBuilder();
        var2.append(var3);
        var2.append("VERSION_RELEASE: ");
        var2.append(VERSION.RELEASE);
        var2.append("\n");
        var8 = var2.toString();
        var10 = new StringBuilder();
        var10.append(var8);
        var10.append("VERSION_SDK_INT: ");
        var10.append(VERSION.SDK_INT);
        var10.append("\n");
        var8 = var10.toString();
        var10 = new StringBuilder();
        var10.append(var8);
        var10.append("WifiMac: ");
// Hardcode Utilities.getMacAddr()
        var10.append("02:21:60:00:00:00");
        var10.append("\n");
        var3 = var10.toString();
        WifiInfo var5 = var1.getConnectionInfo();
        var8 = var3;
        if (var5 != null) {
            var2 = new StringBuilder();
            var2.append(var3);
            var2.append("WifiSSID: ");
            var2.append(var5.getSSID());
            var2.append("\n");
            var8 = var2.toString();
        }

        var10 = new StringBuilder();
        var10.append(var8);
//Hardcode getTotalRAM()
        var10.append("MemTotal:        3973736 kB");
        var10.append("\n");
        var8 = var10.toString();
        var10 = new StringBuilder();
        var10.append(var8);
        var10.append(getCPUInfo());
        var10.append("\n");
        var3 = var10.toString();
        var2 = new StringBuilder();
        var2.append(var3);
        var2.append("IMEI: ");
        var2.append(getIMEI(var0));
        var2.append("\n");
        var8 = var2.toString();
        var10 = new StringBuilder();
        var10.append(var8);
        var10.append("Internal: ");
        var10.append(getTotalInternalMemory());
        var10.append("\n");
        var8 = var10.toString();
        var10 = new StringBuilder();
        var10.append(var8);
        var10.append("CPUCores: ");
// Hardcode getNumCores()
        var10.append("8");
        var10.append("\n");
        var3 = var10.toString();
        DisplayMetrics var6 = new DisplayMetrics();
        ((WindowManager) var0.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(var6);
        var2 = new StringBuilder();
        var2.append(var3);
        var2.append("Screen: ");
        var2.append(var6.widthPixels);
        var2.append("x");
        var2.append(var6.heightPixels);
        var2.append("\n");
        var3 = var2.toString();
// Hardcode calculateMD5(new File("/system/framework/services.jar"))
        String var7 = "49B5385E9223EF1EA4E864CDE1723177";
        var8 = var3;
        if (var7 != null) {
            var2 = new StringBuilder();
            var2.append(var3);
            var2.append("services.jar: ");
            var2.append(var7);
            var2.append("\n");
            var8 = var2.toString();
        }

// Hardcode calculateMD5(new File("/system/framework/framework.jar"))
        var7 = "EA94F1A0BC309B31C49F7664BA878789";
        var3 = var8;
        if (var7 != null) {
            var10 = new StringBuilder();
            var10.append(var8);
            var10.append("framework.jar: ");
            var10.append(var7);
            var10.append("\n");
            var3 = var10.toString();
        }

        /*
        var8 = getMD5usingShell("/dev/block/platform/dw_mmc/by-name/RECOVERY");
        if (var8 != null) {
            StringBuilder var9 = new StringBuilder();
            var9.append(var3);
            var9.append("RECOVERY: ");
            var9.append(var8);
            var9.append("\n");
            var8 = var9.toString();
        } else {
            var7 = getMD5usingShell("/dev/block/by-name/recovery");
            var8 = var3;
            if (var7 != null) {
                var2 = new StringBuilder();
                var2.append(var3);
                var2.append("RECOVERY: ");
                var2.append(var7);
                var2.append("\n");
                var8 = var2.toString();
            }
        }

        var7 = getMD5usingShell("/dev/block/platform/dw_mmc/by-name/BOOT");
        if (var7 != null) {
            var10 = new StringBuilder();
            var10.append(var8);
            var10.append("BOOT: ");
            var10.append(var7);
            var10.append("\n");
            var3 = var10.toString();
        } else {
            var7 = getMD5usingShell("/dev/block/by-name/boot");
            var3 = var8;
            if (var7 != null) {
                var10 = new StringBuilder();
                var10.append(var8);
                var10.append("BOOT: ");
                var10.append(var7);
                var10.append("\n");
                var3 = var10.toString();
            }
        }

         */

        var2 = new StringBuilder();
        var2.append(var3);
        var2.append("PackageName: ");
        var2.append(var0.getPackageName());
        var2.append("\n");
        var8 = var2.toString();
        String var4;
// Hardcode MyiBaseApplication.DEBUG
        if (false) {
            var10 = new StringBuilder();
            var10.append(var8);
            var10.append("ClientVersion: 5.2.2.52264\n");
            var8 = var10.toString();
            var10 = new StringBuilder();
            var10.append(var8);
            var10.append("ClientMD5: 43D4A8C735FA8B3F4357F1AA8736FF4E\n");
            var3 = var10.toString();
            var2 = new StringBuilder();
            var2.append(var3);
            var2.append("ClientSign: ");
            //var2.append(getSign(var0));
            var2.append("\n");
            var3 = var2.toString();
            var2 = new StringBuilder();
            var2.append(var3);
            var2.append("ClientPath: ");
            //var2.append(getFilePath(var0));
            var2.append("\n");
            var4 = var2.toString();
        } else {
            var10 = new StringBuilder();
            var10.append(var8);
            var10.append("ClientVersion: ");
            var10.append(getVersionName(var0));
            var10.append("\n");
            var8 = var10.toString();
            var10 = new StringBuilder();
            var10.append(var8);
            var10.append("ClientSign: ");
            var10.append(getSign(var0));
            var10.append("\n");
            var3 = var10.toString();
            var2 = new StringBuilder();
            var2.append(var3);
            var2.append("ClientPath: ");
            var2.append(getFilePath(var0));
            var2.append("\n");
            var8 = var2.toString();
//var4 = calculateMD5(new File(getFilePath(var0)));
            var4 = "D641121123578F86E519E5B422D9971E";
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
        }

        return var4;
    }


    public static String getCPUInfo() {
        StringBuffer var0 = new StringBuffer();
        if ((new File("/proc/cpuinfo")).exists()) {
            IOException var10000;
            label35:
            {
                BufferedReader var1;
                boolean var10001;
                try {
                    File var3 = new File("/proc/cpuinfo");
                    FileReader var2 = new FileReader(var3);
                    var1 = new BufferedReader(var2);
                } catch (IOException var7) {
                    var10000 = var7;
                    var10001 = false;
                    break label35;
                }

                while (true) {
                    String var10;
                    try {
                        var10 = var1.readLine();
                    } catch (IOException var5) {
                        var10000 = var5;
                        var10001 = false;
                        break;
                    }

                    if (var10 == null) {
                        try {
                            var1.close();
                            return var0.toString().replace("\t", "").replace("\n\n", "\n");
                        } catch (IOException var4) {
                            var10000 = var4;
                            var10001 = false;
                            break;
                        }
                    }

                    StringBuilder var9 = new StringBuilder();
                    var9.append(var10);
                    var9.append("\n");
                    var0.append(var9.toString());
                }
            }

            IOException var8 = var10000;
            var8.printStackTrace();
        }

        return var0.toString().replace("\t", "").replace("\n\n", "\n");
    }

    private static String getIMEI(Context var0) {
        String var1 = ((TelephonyManager)var0.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        String var2 = var1;
        if (var1 == null) {
            var2 = "";
        }

        return var2;
    }

    public static String getTotalInternalMemory() {
        StatFs var0 = new StatFs(Environment.getDataDirectory().getAbsolutePath());
        long var1 = (long)var0.getBlockCount() * (long)var0.getBlockSize() / 1048576L;
        StringBuilder var3 = new StringBuilder();
        var3.append(var1);
        var3.append("MB");
        return var3.toString();
    }

    public static String getVersionName(Context var0) {
        PackageManager var1 = var0.getPackageManager();

        String var3;
        try {
            var3 = var1.getPackageInfo(var0.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException var2) {
            var2.printStackTrace();
            var3 = "";
        }

        return var3;
    }

    private static String getSign(Context var0) {
        PackageManager var1 = var0.getPackageManager();

        String var3;
        try {
            var3 = var1.getPackageInfo(var0.getPackageName(), PackageManager.GET_SIGNATURES).signatures[0].toCharsString();
        } catch (PackageManager.NameNotFoundException var2) {
            var2.printStackTrace();
            var3 = "";
        }

        return var3;
    }

    public static String getFilePath(Context var0) {
        PackageManager var1 = var0.getPackageManager();

        String var3;
        try {
            var3 = var1.getPackageInfo(var0.getPackageName(), 0).applicationInfo.sourceDir;
        } catch (PackageManager.NameNotFoundException var2) {
            var2.printStackTrace();
            var3 = "";
        }

        return var3;
    }
}