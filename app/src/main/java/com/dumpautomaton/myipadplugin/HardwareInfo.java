package com.dumpautomaton.myipadplugin;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.StatFs;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HardwareInfo {

    public static String calculateMD5(File file) {
        String str = null;
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] bArr = new byte[5242880];
                while (true) {
                    try {
                        int read = fileInputStream.read(bArr);
                        if (read <= 0) {
                            break;
                        }
                        instance.update(bArr, 0, read);
                    } catch (IOException e) {
                        throw new RuntimeException("Unable to process file for MD5", e);
                    } catch (Throwable th) {
                        try {
                            fileInputStream.close();
                        } catch (IOException e2) {
                            Log.e("Login", "Exception on closing MD5 input stream", e2);
                        }
                    }
                }
                BigInteger bigInteger = new BigInteger(1, instance.digest());
                str = String.format("%32s", new Object[]{bigInteger.toString(16)}).replace(' ', '0');
                try {
                    fileInputStream.close();
                } catch (IOException e22) {
                    Log.e("Login", "Exception on closing MD5 input stream", e22);
                }
            } catch (FileNotFoundException e3) {
                Log.e("Login", "Exception while getting FileInputStream", e3);
            }
        } catch (NoSuchAlgorithmException e4) {
            Log.e("Login", "Exception while gettingDigest", e4);
        }
        return str;
    }

    public static String getCPUInfo() {
        StringBuffer stringBuffer = new StringBuffer();
        if (new File("/proc/cpuinfo").exists()) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(new File("/proc/cpuinfo")));
                while (true) {
                    String readLine = bufferedReader.readLine();
                    if (readLine == null) {
                        break;
                    }
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(readLine);
                    stringBuilder.append("\n");
                    stringBuffer.append(stringBuilder.toString());
                }
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stringBuffer.toString().replace("\t", "").replace("\n\n", "\n");
    }

    public static String getFilePath(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).applicationInfo.sourceDir;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getHardwareInfo(Context context) {
        String stringBuilder;
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService("wifi");
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("");
        stringBuilder2.append("BOARD: ");
        stringBuilder2.append(Build.BOARD);
        stringBuilder2.append("\n");
        String stringBuilder3 = stringBuilder2.toString();
        StringBuilder stringBuilder4 = new StringBuilder();
        stringBuilder4.append(stringBuilder3);
        stringBuilder4.append("BOOTLOADER: ");
        stringBuilder4.append("P350ZCU1AQE1");
        stringBuilder4.append("\n");
        stringBuilder3 = stringBuilder4.toString();
        stringBuilder4 = new StringBuilder();
        stringBuilder4.append(stringBuilder3);
        stringBuilder4.append("BRAND: ");
        stringBuilder4.append(Build.BRAND);
        stringBuilder4.append("\n");
        stringBuilder3 = stringBuilder4.toString();
        stringBuilder4 = new StringBuilder();
        stringBuilder4.append(stringBuilder3);
        stringBuilder4.append("CPU_ABI: ");
        stringBuilder4.append(Build.CPU_ABI);
        stringBuilder4.append("\n");
        stringBuilder3 = stringBuilder4.toString();
        stringBuilder4 = new StringBuilder();
        stringBuilder4.append(stringBuilder3);
        stringBuilder4.append("CPU_ABI2: ");
        stringBuilder4.append(Build.CPU_ABI2);
        stringBuilder4.append("\n");
        stringBuilder3 = stringBuilder4.toString();
        stringBuilder4 = new StringBuilder();
        stringBuilder4.append(stringBuilder3);
        stringBuilder4.append("DEVICE: ");
        stringBuilder4.append("gt5note8wifichn");
        stringBuilder4.append("\n");
        stringBuilder3 = stringBuilder4.toString();
        stringBuilder4 = new StringBuilder();
        stringBuilder4.append(stringBuilder3);
        stringBuilder4.append("DISPLAY: ");
        stringBuilder4.append("LRX22G.P350ZCU1AQE1");
        stringBuilder4.append("\n");
        stringBuilder3 = stringBuilder4.toString();
        stringBuilder4 = new StringBuilder();
        stringBuilder4.append(stringBuilder3);
        stringBuilder4.append("FINGERPRINT: ");
        stringBuilder4.append("samsung/gt5note8wifizc/gt5note8wifichn:5.0.2/LRX22G/P350ZCU1AQE1:user/release-keys");
        stringBuilder4.append("\n");
        stringBuilder3 = stringBuilder4.toString();
        stringBuilder4 = new StringBuilder();
        stringBuilder4.append(stringBuilder3);
        stringBuilder4.append("HARDWARE: ");
        stringBuilder4.append(Build.HARDWARE);
        stringBuilder4.append("\n");
        stringBuilder3 = stringBuilder4.toString();
        stringBuilder4 = new StringBuilder();
        stringBuilder4.append(stringBuilder3);
        stringBuilder4.append("HOST: ");
        stringBuilder4.append(Build.HOST);
        stringBuilder4.append("\n");
        stringBuilder3 = stringBuilder4.toString();
        stringBuilder4 = new StringBuilder();
        stringBuilder4.append(stringBuilder3);
        stringBuilder4.append("ID: ");
        stringBuilder4.append(Build.ID);
        stringBuilder4.append("\n");
        stringBuilder3 = stringBuilder4.toString();
        stringBuilder4 = new StringBuilder();
        stringBuilder4.append(stringBuilder3);
        stringBuilder4.append("MANUFACTURER: ");
        stringBuilder4.append(Build.MANUFACTURER);
        stringBuilder4.append("\n");
        stringBuilder3 = stringBuilder4.toString();
        stringBuilder4 = new StringBuilder();
        stringBuilder4.append(stringBuilder3);
        stringBuilder4.append("MODEL: ");
        stringBuilder4.append(Build.MODEL);
        stringBuilder4.append("\n");
        stringBuilder3 = stringBuilder4.toString();
        stringBuilder4 = new StringBuilder();
        stringBuilder4.append(stringBuilder3);
        stringBuilder4.append("PRODUCT: ");
        stringBuilder4.append(Build.PRODUCT);
        stringBuilder4.append("\n");
        stringBuilder3 = stringBuilder4.toString();
        if (Build.getRadioVersion() != null) {
            stringBuilder4 = new StringBuilder();
            stringBuilder4.append(stringBuilder3);
            stringBuilder4.append("RADIO: ");
            stringBuilder4.append(Build.getRadioVersion());
            stringBuilder4.append("\n");
            stringBuilder3 = stringBuilder4.toString();
        }
        stringBuilder4 = new StringBuilder();
        stringBuilder4.append(stringBuilder3);
        stringBuilder4.append("SERIAL: ");
        stringBuilder4.append(Build.SERIAL);
        stringBuilder4.append("\n");
        stringBuilder3 = stringBuilder4.toString();
        stringBuilder4 = new StringBuilder();
        stringBuilder4.append(stringBuilder3);
        stringBuilder4.append("TAGS: ");
        stringBuilder4.append(Build.TAGS);
        stringBuilder4.append("\n");
        stringBuilder3 = stringBuilder4.toString();
        stringBuilder4 = new StringBuilder();
        stringBuilder4.append(stringBuilder3);
        stringBuilder4.append("TIME: ");
        stringBuilder4.append(Build.TIME);
        stringBuilder4.append("\n");
        stringBuilder3 = stringBuilder4.toString();
        stringBuilder4 = new StringBuilder();
        stringBuilder4.append(stringBuilder3);
        stringBuilder4.append("TYPE: ");
        stringBuilder4.append(Build.TYPE);
        stringBuilder4.append("\n");
        stringBuilder3 = stringBuilder4.toString();
        stringBuilder4 = new StringBuilder();
        stringBuilder4.append(stringBuilder3);
        stringBuilder4.append("UNKNOWN: unknown\n");
        stringBuilder3 = stringBuilder4.toString();
        stringBuilder4 = new StringBuilder();
        stringBuilder4.append(stringBuilder3);
        stringBuilder4.append("USER: ");
        stringBuilder4.append(Build.USER);
        stringBuilder4.append("\n");
        stringBuilder3 = stringBuilder4.toString();
        stringBuilder4 = new StringBuilder();
        stringBuilder4.append(stringBuilder3);
        stringBuilder4.append("VERSION_CODENAME: ");
        stringBuilder4.append(VERSION.CODENAME);
        stringBuilder4.append("\n");
        stringBuilder3 = stringBuilder4.toString();
        stringBuilder4 = new StringBuilder();
        stringBuilder4.append(stringBuilder3);
        stringBuilder4.append("VERSION_RELEASE: ");
        stringBuilder4.append(VERSION.RELEASE);
        stringBuilder4.append("\n");
        stringBuilder3 = stringBuilder4.toString();
        stringBuilder4 = new StringBuilder();
        stringBuilder4.append(stringBuilder3);
        stringBuilder4.append("VERSION_SDK_INT: ");
        stringBuilder4.append(VERSION.SDK_INT);
        stringBuilder4.append("\n");
        stringBuilder3 = stringBuilder4.toString();
        stringBuilder4 = new StringBuilder();
        stringBuilder4.append(stringBuilder3);
        stringBuilder4.append("WifiMac: ");
        stringBuilder4.append("MACAdress test");
        stringBuilder4.append("\n");
        stringBuilder3 = stringBuilder4.toString();
        WifiInfo connectionInfo = wifiManager.getConnectionInfo();
        if (connectionInfo != null) {
            stringBuilder4 = new StringBuilder();
            stringBuilder4.append(stringBuilder3);
            stringBuilder4.append("WifiSSID: ");
            stringBuilder4.append(connectionInfo.getSSID());
            stringBuilder4.append("\n");
            stringBuilder = stringBuilder4.toString();
        } else {
            stringBuilder = stringBuilder3;
        }
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(stringBuilder);
        stringBuilder2.append(getTotalRAM());
        stringBuilder2.append("\n");
        stringBuilder = stringBuilder2.toString();
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(stringBuilder);
        stringBuilder2.append(getCPUInfo());
        stringBuilder2.append("\n");
        stringBuilder = stringBuilder2.toString();
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(stringBuilder);
        stringBuilder2.append("IMEI: ");
        stringBuilder2.append(getIMEI(context));
        stringBuilder2.append("\n");
        stringBuilder = stringBuilder2.toString();
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(stringBuilder);
        stringBuilder2.append("Internal: ");
        stringBuilder2.append(getTotalInternalMemory());
        stringBuilder2.append("\n");
        stringBuilder = stringBuilder2.toString();
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(stringBuilder);
        stringBuilder2.append("CPUCores: ");
        stringBuilder2.append("4");
        stringBuilder2.append("\n");
        stringBuilder3 = stringBuilder2.toString();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
        StringBuilder stringBuilder5 = new StringBuilder();
        stringBuilder5.append(stringBuilder3);
        stringBuilder5.append("Screen: ");
        stringBuilder5.append(displayMetrics.widthPixels);
        stringBuilder5.append("x");
        stringBuilder5.append(displayMetrics.heightPixels);
        stringBuilder5.append("\n");
        stringBuilder = stringBuilder5.toString();
        stringBuilder4 = new StringBuilder();
        stringBuilder4.append(stringBuilder);
        stringBuilder4.append("services.jar: ");
        stringBuilder4.append("4413a7c677c6f0b0e4aa3ef90af21797");
        stringBuilder4.append("\n");
        stringBuilder = stringBuilder4.toString();
        stringBuilder4 = new StringBuilder();
        stringBuilder4.append(stringBuilder);
        stringBuilder4.append("framework.jar: ");
        stringBuilder4.append("afce46aac8820539344e6b6aa3cfc9dc");
        stringBuilder4.append("\n");
        stringBuilder = stringBuilder4.toString();
        stringBuilder4 = new StringBuilder();
        stringBuilder4.append(stringBuilder);
        stringBuilder4.append("RECOVERY: ");
        stringBuilder4.append("recovery test");
        stringBuilder4.append("\n");
        stringBuilder = stringBuilder4.toString();

        stringBuilder4 = new StringBuilder();
        stringBuilder4.append(stringBuilder);
        stringBuilder4.append("BOOT: ");
        stringBuilder4.append("boot test");
        stringBuilder4.append("\n");
        stringBuilder = stringBuilder4.toString();

        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(stringBuilder);
        stringBuilder2.append("PackageName: ");
        stringBuilder2.append("com.netspace.myipad");
        stringBuilder2.append("\n");
        stringBuilder = stringBuilder2.toString();

        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(stringBuilder);
        stringBuilder2.append("ClientVersion: ");
        stringBuilder2.append(getVersionName(context));
        stringBuilder2.append("\n");
        stringBuilder = stringBuilder2.toString();
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(stringBuilder);
        stringBuilder2.append("ClientSign: ");
        stringBuilder2.append(getSign(context));
        stringBuilder2.append("\n");
        stringBuilder = stringBuilder2.toString();
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(stringBuilder);
        stringBuilder2.append("ClientPath: ");
        stringBuilder2.append(getFilePath(context));
        stringBuilder2.append("\n");
        stringBuilder = stringBuilder2.toString();
        stringBuilder3 = calculateMD5(new File(getFilePath(context)));
        if (stringBuilder3 == null) {
            return stringBuilder;
        }
        stringBuilder4 = new StringBuilder();
        stringBuilder4.append(stringBuilder);
        stringBuilder4.append("ClientMD5: ");
        stringBuilder4.append(stringBuilder3);
        stringBuilder4.append("\n");
        return stringBuilder4.toString();
    }

    private static String getIMEI(Context context) {
        String deviceId = "12345678987654321";
        return deviceId == null ? "" : deviceId;
    }
/*
    public static String getMD5usingShell(String str) {
        String str2;
        InterruptedException e;
        TimeoutException e2;
        RootDeniedException e3;
        IOException e4;
        IllegalStateException e5;
        RootTools.debugMode = false;
        if (!mbRootTested) {
            mbRooted = true;
            if (!RootTools.isRootAvailable()) {
                mbRooted = false;
            }
            if (!RootTools.isAccessGiven()) {
                mbRooted = false;
            }
            mbRootTested = true;
        }
        if (!mbRooted) {
            return null;
        }
        try {
            Shell shell = RootTools.getShell(true);
            if (!Shell.isAnyShellOpen()) {
                return null;
            }
            shell.add(new CommandCapture(0, new String[]{"mount -o remount,rw /system"})).waitForFinish();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("md5 ");
            stringBuilder.append(str);
            CommandCapture commandCapture = new CommandCapture(0, new String[]{stringBuilder.toString()});
            shell.add(commandCapture).waitForFinish();
            String commandCapture2 = commandCapture.toString();
            try {
                if (commandCapture2.indexOf(" ") != -1) {
                    commandCapture2 = commandCapture2.substring(0, commandCapture2.indexOf(" "));
                }
                str2 = commandCapture2.length() != 32 ? null : commandCapture2;
            } catch (InterruptedException e6) {
                str2 = commandCapture2;
                e = e6;
                e.printStackTrace();
                return str2;
            } catch (TimeoutException e7) {
                str2 = commandCapture2;
                e2 = e7;
                e2.printStackTrace();
                return str2;
            } catch (RootDeniedException e8) {
                str2 = commandCapture2;
                e3 = e8;
                e3.printStackTrace();
                return str2;
            } catch (IOException e9) {
                str2 = commandCapture2;
                e4 = e9;
                e4.printStackTrace();
                return str2;
            } catch (IllegalStateException e10) {
                str2 = commandCapture2;
                e5 = e10;
                e5.printStackTrace();
                return str2;
            }
            return str2;
        } catch (InterruptedException e11) {
            e = e11;
            str2 = null;
            e.printStackTrace();
            return str2;
        } catch (TimeoutException e12) {
            e2 = e12;
            str2 = null;
            e2.printStackTrace();
            return str2;
        } catch (RootDeniedException e13) {
            e3 = e13;
            str2 = null;
            e3.printStackTrace();
            return str2;
        } catch (IOException e14) {
            e4 = e14;
            str2 = null;
            e4.printStackTrace();
            return str2;
        } catch (IllegalStateException e15) {
            e5 = e15;
            str2 = null;
            e5.printStackTrace();
            return str2;
        }
    }
 */


    static String getSign(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 64).signatures[0].toCharsString();
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getTotalInternalMemory() {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
        long blockSize = (((long) statFs.getBlockSize()) * ((long) statFs.getBlockCount())) / 1048576;
        String stringBuilder = blockSize +
                "MB";
        return stringBuilder;
    }

    public static String getTotalRAM() {
        try {
            return new RandomAccessFile("/proc/meminfo", "r").readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }
}