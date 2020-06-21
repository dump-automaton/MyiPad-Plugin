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
//import com.stericson.RootTools.RootTools;
//import com.stericson.RootTools.exceptions.RootDeniedException;
//import com.stericson.RootTools.execution.CommandCapture;
//import com.stericson.RootTools.execution.Shell;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

public class HardwareInfoDTSDAO {
    private static boolean mbRootTested;
    private static boolean mbRooted;

    public static String calculateMD5(File file) {
        String str = null;
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            try {
                InputStream fileInputStream = new FileInputStream(file);
                byte[] bArr = new byte[5242880];
                while (true) {
                    try {
                        int read = fileInputStream.read(bArr);
                        if (read <= 0) {
                            break;
                        }
                        instance.update(bArr, 0, read);
                    } catch (Throwable e) {
                        throw new RuntimeException("Unable to process file for MD5", e);
                    } /*catch (Throwable th) {
                        try {
                            fileInputStream.close();
                        } catch (Throwable e2) {
                            Log.e("Login", "Exception on closing MD5 input stream", e2);
                        }
                    }
                    */
                }
                BigInteger bigInteger = new BigInteger(1, instance.digest());
                str = String.format("%32s", new Object[]{bigInteger.toString(16)}).replace(' ', '0');
                try {
                    fileInputStream.close();
                } catch (Throwable e22) {
                    Log.e("Login", "Exception on closing MD5 input stream", e22);
                }
            } catch (Throwable e222) {
                Log.e("Login", "Exception while getting FileInputStream", e222);
            }
        } catch (Throwable e2222) {
            Log.e("Login", "Exception while gettingDigest", e2222);
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
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
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
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append("BOARD: ");
        stringBuilder.append(Build.BOARD);
        stringBuilder.append("\n");
        String stringBuilder2 = stringBuilder.toString();
        StringBuilder stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append("BOOTLOADER: ");
        stringBuilder3.append(Build.BOOTLOADER);
        stringBuilder3.append("\n");
        stringBuilder2 = stringBuilder3.toString();
        stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append("BRAND: ");
        stringBuilder3.append(Build.BRAND);
        stringBuilder3.append("\n");
        stringBuilder2 = stringBuilder3.toString();
        stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append("CPU_ABI: ");
        stringBuilder3.append(Build.CPU_ABI);
        stringBuilder3.append("\n");
        stringBuilder2 = stringBuilder3.toString();
        stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append("CPU_ABI2: ");
        stringBuilder3.append(Build.CPU_ABI2);
        stringBuilder3.append("\n");
        stringBuilder2 = stringBuilder3.toString();
        stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append("DEVICE: ");
        stringBuilder3.append(Build.DEVICE);
        stringBuilder3.append("\n");
        stringBuilder2 = stringBuilder3.toString();
        stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append("DISPLAY: ");
        stringBuilder3.append(Build.DISPLAY);
        stringBuilder3.append("\n");
        stringBuilder2 = stringBuilder3.toString();
        stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append("FINGERPRINT: ");
        stringBuilder3.append(Build.FINGERPRINT);
        stringBuilder3.append("\n");
        stringBuilder2 = stringBuilder3.toString();
        stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append("HARDWARE: ");
        stringBuilder3.append(Build.HARDWARE);
        stringBuilder3.append("\n");
        stringBuilder2 = stringBuilder3.toString();
        stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append("HOST: ");
        stringBuilder3.append(Build.HOST);
        stringBuilder3.append("\n");
        stringBuilder2 = stringBuilder3.toString();
        stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append("ID: ");
        stringBuilder3.append(Build.ID);
        stringBuilder3.append("\n");
        stringBuilder2 = stringBuilder3.toString();
        stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append("MANUFACTURER: ");
        stringBuilder3.append(Build.MANUFACTURER);
        stringBuilder3.append("\n");
        stringBuilder2 = stringBuilder3.toString();
        stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append("MODEL: ");
        stringBuilder3.append(Build.MODEL);
        stringBuilder3.append("\n");
        stringBuilder2 = stringBuilder3.toString();
        stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append("PRODUCT: ");
        stringBuilder3.append(Build.PRODUCT);
        stringBuilder3.append("\n");
        stringBuilder2 = stringBuilder3.toString();
        stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append("RADIO: ");
        stringBuilder3.append(Build.RADIO);
        stringBuilder3.append("\n");
        stringBuilder2 = stringBuilder3.toString();
        stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append("SERIAL: ");
        stringBuilder3.append(Build.SERIAL);
        stringBuilder3.append("\n");
        stringBuilder2 = stringBuilder3.toString();
        stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append("TAGS: ");
        stringBuilder3.append(Build.TAGS);
        stringBuilder3.append("\n");
        stringBuilder2 = stringBuilder3.toString();
        stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append("TIME: ");
        stringBuilder3.append(Build.TIME);
        stringBuilder3.append("\n");
        stringBuilder2 = stringBuilder3.toString();
        stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append("TYPE: ");
        stringBuilder3.append(Build.TYPE);
        stringBuilder3.append("\n");
        stringBuilder2 = stringBuilder3.toString();
        stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append("UNKNOWN: unknown\n");
        stringBuilder2 = stringBuilder3.toString();
        stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append("USER: ");
        stringBuilder3.append(Build.USER);
        stringBuilder3.append("\n");
        stringBuilder2 = stringBuilder3.toString();
        stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append("VERSION_CODENAME: ");
        stringBuilder3.append(VERSION.CODENAME);
        stringBuilder3.append("\n");
        stringBuilder2 = stringBuilder3.toString();
        stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append("VERSION_RELEASE: ");
        stringBuilder3.append(VERSION.RELEASE);
        stringBuilder3.append("\n");
        stringBuilder2 = stringBuilder3.toString();
        stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append("VERSION_SDK_INT: ");
        stringBuilder3.append(VERSION.SDK_INT);
        stringBuilder3.append("\n");
        String stringBuilder4 = stringBuilder3.toString();
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        StringBuilder stringBuilder5 = new StringBuilder();
        stringBuilder5.append(stringBuilder4);
        stringBuilder5.append("WifiMac: ");
        //stringBuilder5.append(Utilities.getMacAddr());
        stringBuilder5.append(getMacAddr());
        stringBuilder5.append("\n");
        stringBuilder4 = stringBuilder5.toString();
        WifiInfo connectionInfo = wifiManager.getConnectionInfo();
        stringBuilder5 = new StringBuilder();
        stringBuilder5.append(stringBuilder4);
        stringBuilder5.append("WifiSSID: ");
        stringBuilder5.append(connectionInfo.getSSID());
        stringBuilder5.append("\n");
        stringBuilder2 = stringBuilder5.toString();
        stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append(getTotalRAM());
        stringBuilder3.append("\n");
        stringBuilder2 = stringBuilder3.toString();
        stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append(getCPUInfo());
        stringBuilder3.append("\n");
        stringBuilder2 = stringBuilder3.toString();
        stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append("Internal: ");
        stringBuilder3.append(getTotalInternalMemory());
        stringBuilder3.append("\n");
        stringBuilder2 = stringBuilder3.toString();
        stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append("CPUCores: ");
        stringBuilder3.append(String.valueOf(getNumCores()));
        stringBuilder3.append("\n");
        stringBuilder4 = stringBuilder3.toString();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
        stringBuilder = new StringBuilder();
        stringBuilder.append(stringBuilder4);
        stringBuilder.append("Screen: ");
        stringBuilder.append(String.valueOf(displayMetrics.widthPixels));
        stringBuilder.append("x");
        stringBuilder.append(String.valueOf(displayMetrics.heightPixels));
        stringBuilder.append("\n");
        stringBuilder2 = stringBuilder.toString();
        stringBuilder4 = calculateMD5(new File("/system/framework/services.jar"));
        if (stringBuilder4 != null) {
            stringBuilder5 = new StringBuilder();
            stringBuilder5.append(stringBuilder2);
            stringBuilder5.append("services.jar: ");
            stringBuilder5.append(stringBuilder4);
            stringBuilder5.append("\n");
            stringBuilder2 = stringBuilder5.toString();
        }
        stringBuilder4 = calculateMD5(new File("/system/framework/framework.jar"));
        if (stringBuilder4 != null) {
            stringBuilder5 = new StringBuilder();
            stringBuilder5.append(stringBuilder2);
            stringBuilder5.append("framework.jar: ");
            stringBuilder5.append(stringBuilder4);
            stringBuilder5.append("\n");
            stringBuilder2 = stringBuilder5.toString();
        }
        //stringBuilder4 = getMD5usingShell("/dev/block/platform/dw_mmc/by-name/RECOVERY");
        stringBuilder4 = null;
        if (stringBuilder4 != null) {
            stringBuilder5 = new StringBuilder();
            stringBuilder5.append(stringBuilder2);
            stringBuilder5.append("RECOVERY: ");
            stringBuilder5.append(stringBuilder4);
            stringBuilder5.append("\n");
            stringBuilder2 = stringBuilder5.toString();
        } else {
            //stringBuilder4 = getMD5usingShell("/dev/block/by-name/recovery");
            if (stringBuilder4 != null) {
                stringBuilder5 = new StringBuilder();
                stringBuilder5.append(stringBuilder2);
                stringBuilder5.append("RECOVERY: ");
                stringBuilder5.append(stringBuilder4);
                stringBuilder5.append("\n");
                stringBuilder2 = stringBuilder5.toString();
            }
        }
        //stringBuilder4 = getMD5usingShell("/dev/block/platform/dw_mmc/by-name/BOOT");
        stringBuilder4 = null;
        if (stringBuilder4 != null) {
            stringBuilder5 = new StringBuilder();
            stringBuilder5.append(stringBuilder2);
            stringBuilder5.append("BOOT: ");
            stringBuilder5.append(stringBuilder4);
            stringBuilder5.append("\n");
            stringBuilder2 = stringBuilder5.toString();
        } else {
            //stringBuilder4 = getMD5usingShell("/dev/block/by-name/boot");
            if (stringBuilder4 != null) {
                stringBuilder5 = new StringBuilder();
                stringBuilder5.append(stringBuilder2);
                stringBuilder5.append("BOOT: ");
                stringBuilder5.append(stringBuilder4);
                stringBuilder5.append("\n");
                stringBuilder2 = stringBuilder5.toString();
            }
        }
        stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append("ClientVersion: ");
        stringBuilder3.append(getVersionName(context));
        stringBuilder3.append("\n");
        stringBuilder2 = stringBuilder3.toString();
        stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append("ClientSign: ");
        stringBuilder3.append(getSign(context));
        stringBuilder3.append("\n");
        stringBuilder2 = stringBuilder3.toString();
        stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append("ClientPath: ");
        stringBuilder3.append(getFilePath(context));
        stringBuilder3.append("\n");
        stringBuilder2 = stringBuilder3.toString();
        stringBuilder4 = calculateMD5(new File(getFilePath(context)));
        if (stringBuilder4 == null) {
            return stringBuilder2;
        }
        stringBuilder5 = new StringBuilder();
        stringBuilder5.append(stringBuilder2);
        stringBuilder5.append("ClientMD5: ");
        stringBuilder5.append(stringBuilder4);
        stringBuilder5.append("\n");
        return stringBuilder5.toString();
    }
    /*
        public static String getMD5usingShell(String str) {
            InterruptedException e;
            TimeoutException e2;
            RootDeniedException e3;
            IOException e4;
            IllegalStateException e5;
            String str2 = null;
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
                    return commandCapture2.length() == 32 ? commandCapture2 : null;
                } catch (InterruptedException e6) {
                    InterruptedException interruptedException = e6;
                    str2 = commandCapture2;
                    e = interruptedException;
                    e.printStackTrace();
                    return str2;
                } catch (TimeoutException e7) {
                    TimeoutException timeoutException = e7;
                    str2 = commandCapture2;
                    e2 = timeoutException;
                    e2.printStackTrace();
                    return str2;
                } catch (RootDeniedException e8) {
                    RootDeniedException rootDeniedException = e8;
                    str2 = commandCapture2;
                    e3 = rootDeniedException;
                    e3.printStackTrace();
                    return str2;
                } catch (IOException e9) {
                    IOException iOException = e9;
                    str2 = commandCapture2;
                    e4 = iOException;
                    e4.printStackTrace();
                    return str2;
                } catch (IllegalStateException e10) {
                    IllegalStateException illegalStateException = e10;
                    str2 = commandCapture2;
                    e5 = illegalStateException;
                    e5.printStackTrace();
                    return str2;
                }
            } catch (InterruptedException e11) {
                e = e11;
                e.printStackTrace();
                return str2;
            } catch (TimeoutException e12) {
                e2 = e12;
                e2.printStackTrace();
                return str2;
            } catch (RootDeniedException e13) {
                e3 = e13;
                e3.printStackTrace();
                return str2;
            } catch (IOException e14) {
                e4 = e14;
                e4.printStackTrace();
                return str2;
            } catch (IllegalStateException e15) {
                e5 = e15;
                e5.printStackTrace();
                return str2;
            }
        }
    */
    public static int getNumCores() {
        try {
            return new File("/sys/devices/system/cpu/").listFiles(new FileFilter() {
                public boolean accept(File file) {
                    return Pattern.matches("cpu[0-9]", file.getName());
                }
            }).length;
        } catch (Exception e) {
            return 1;
        }
    }

    public static String getSign(Context context) {
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
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.valueOf(blockSize));
        stringBuilder.append("MB");
        return stringBuilder.toString();
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

    public static String getMacAddr() {
        label81: {
            Iterator var0;
            boolean var10001;
            try {
                var0 = Collections.list(NetworkInterface.getNetworkInterfaces()).iterator();
            } catch (Exception var10) {
                var10001 = false;
                break label81;
            }

            NetworkInterface var1;
            while(true) {
                try {
                    if (!var0.hasNext()) {
                        return "02:00:00:00:00:00";
                    }

                    var1 = (NetworkInterface)var0.next();
                    if (var1.getName().equalsIgnoreCase("wlan0")) {
                        break;
                    }
                } catch (Exception var11) {
                    var10001 = false;
                    break label81;
                }
            }

            byte[] var13;
            try {
                var13 = var1.getHardwareAddress();
            } catch (Exception var9) {
                var10001 = false;
                break label81;
            }

            if (var13 == null) {
                return "";
            }

            int var2;
            StringBuilder var12;
            try {
                var12 = new StringBuilder();
                var2 = var13.length;
            } catch (Exception var8) {
                var10001 = false;
                break label81;
            }

            for(int var3 = 0; var3 < var2; ++var3) {
                byte var4 = var13[var3];

                try {
                    var12.append(String.format(Locale.CHINA, "%02X:", var4));
                } catch (Exception var7) {
                    var10001 = false;
                    break label81;
                }
            }

            try {
                if (var12.length() > 0) {
                    var12.deleteCharAt(var12.length() - 1);
                }
            } catch (Exception var6) {
                var10001 = false;
                break label81;
            }

            try {
                String var14 = var12.toString();
                return var14;
            } catch (Exception var5) {
                var10001 = false;
            }
        }

        Log.d("Utilities", "getMacAddr: exception happen.");
        return "02:00:00:00:00:00";
    }
}