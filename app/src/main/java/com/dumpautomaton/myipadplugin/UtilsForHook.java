package com.dumpautomaton.myipadplugin;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.widget.EditText;

import com.dumpautomaton.myipadplugin.utils.FileIOUtils;

import java.io.File;

@SuppressWarnings("CatchMayIgnoreException")
public class UtilsForHook {

    public static final Runnable doNothingRunnable = new Runnable() {
        @Override
        public void run() {
        }
    };

    private static File safeModeTxtFile;
    public static File getSafeModeTxtFile() {
        if (safeModeTxtFile == null) {
            safeModeTxtFile = new File(Environment.getExternalStorageDirectory(), "plugin_safe_mode.txt");
        }
        return safeModeTxtFile;
    }

    public static boolean isSafeMode() {
        return getSafeModeTxtFile().exists();
    }

    public static boolean setSafeMode(boolean safeMode) {
        if (safeMode && !isSafeMode()) {
            return FileIOUtils.writeFileFromString(UtilsForHook.getSafeModeTxtFile(), "delete me to exit safe mode");
        } else if (!safeMode && isSafeMode()) {
            return getSafeModeTxtFile().delete();
        }
        return true;
    }

    private static File crashLogTxtFile;
    public static File getCrashLogTxtFile() {
        if (crashLogTxtFile == null) {
            crashLogTxtFile = new File(Environment.getExternalStorageDirectory(), "MyiPad_Plugin_Crash.txt");
        }
        return crashLogTxtFile;
    }

    public static void restartApp(Application app) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent LaunchIntent = app.getPackageManager().getLaunchIntentForPackage(app.getPackageName());
                app.startActivity(LaunchIntent);
                Process.killProcess(Process.myPid());
            }
        }, 100);
    }

    private static String mStringResult;
    /**
     * @deprecated Now useless
     * @param title title of the dialog
     * @param editString string inserted into the editText
     * @param defaultString String that will return when Use Default button pressed
     * @return user's input
     */
    public static String showSyncEditDialog(Looper looper, String title, String editString, final String defaultString, Context context) {
        // make a handler that throws a runtime exception when a message is received
        final Handler handler = new SyncDialogMessageHandler(looper);
        // make a text input dialog and show it
        final EditText editText = new EditText(context);
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(title).setView(editText);
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mStringResult = editText.getText().toString();
                handler.sendMessage(handler.obtainMessage());
            }
        });
        if (editString != null) {
            editText.setText(editString);
        }
        if (defaultString != null) {
            alert.setNeutralButton("Use Default", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    mStringResult = defaultString;
                    handler.sendMessage(handler.obtainMessage());
                }
            });
        }
        alert.show();
        // loop till a runtime exception is triggered.
        try {
            Looper.loop();
        } catch (RuntimeException e2) {
        }
        return mStringResult;
    }

    private static boolean mResult;
    /**
     * @deprecated Now useless
     */
    public static boolean showSyncBinaryDialog(Looper looper, String title, String message, Context context) {
        // make a handler that throws a runtime exception when a message is received
        final Handler handler = new SyncDialogMessageHandler(looper);
        // make a text input dialog and show it
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mResult = true;
                handler.sendMessage(handler.obtainMessage());
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mResult = false;
                handler.sendMessage(handler.obtainMessage());
            }
        });
        alert.show();

        // loop till a runtime exception is triggered.
        try {
            Looper.loop();
        } catch (RuntimeException e2) {
        }
        return mResult;
    }

    private static class SyncDialogMessageHandler extends Handler {
        public SyncDialogMessageHandler(Looper looper) {
            super(looper, new Handler.Callback() {

                @Override
                public boolean handleMessage(Message msg) {
                    throw new RuntimeException();
                }
            });
        }
    }

    public static String getHardwareInfoWithoutHardware() {
        return "MODEL: BZT-W09\n" +
                "WifiMac: 12:34:56:78:90:ab\n" +
                "services.jar: d54f80b88122485ef2e8efb9c6e81a06\n" +
                "framework.jar: d54f80b88122485ef2e8efb9c6e81a06\n" +
                "ClientVersion: 5.2.3.52427\n" +
                "ClientSign: 308203253082020da00302010202040966f52d300d06092a864886f70d01010b05003042310b300906035504061302434e310f300d060355040713064e696e67426f31223020060355040a13194e696e67426f2052756959694b654a6920436f2e204c74642e3020170d3132313231313130313133355a180f32303632313132393130313133355a3042310b300906035504061302434e310f300d060355040713064e696e67426f31223020060355040a13194e696e67426f2052756959694b654a6920436f2e204c74642e30820122300d06092a864886f70d01010105000382010f003082010a0282010100abf2c60e5fcb7776da3d22c3180e284da9c4e715cec2736646da086cbf979a7f74bc147167f0f32ef0c52458e9183f0dd9571d7971e49564c00fbfd30bef3ca9a2d52bffcd0142c72e10fac158cb62c7bc7e9e17381a555ad7d39a24a470584a0e6aafdce2e4d6877847b15cbf4de89e3e4e71b11dca9920843ccc055acf8781db29bdaf3f06e16f055bf579a35ae3adb4d1149f8d43d90add54596acef8e4a28905f9f19fc0aa7fda9e8d56aa63db5d8d5e0fc4c536629f0a25a44429c699318329af6a3e869dd5e8289c78f55d14563559ffc9ccbf71fac5a03e13a3ee1fb8fc3857d10d5d3990bf9b84cd6fa555eb17a74809a7bb501e953a639104146adb0203010001a321301f301d0603551d0e04160414da4b4d8147840ff4b03f10fc5dd534bb133204e6300d06092a864886f70d01010b05000382010100801b8d796b90ab7a711a88f762c015158d75f1ae5caf969767131e6980ebe7f194ce33750902e6aa561f33d76d37f4482ff22cccbf9d5fecb6ed8e3f278fd1f988ea85ae30f8579d4afe710378b3ccb9cb41beaddef22fb3d128d9d61cfcb3cb05d32ab3b2c4524815bfc9a53c8e5ee3ad4589dc888bcdbdaf9270268eb176ff2d43c2fd236b5bf4ef8ffa8dd920d1583d70f971b988ee4054e1f739ea71510ee7172546ffcda31e6b270178f91086db9ff1051dedf453a6bad4f9b432d362bbe173fd1cc7350853fddd552a27a82fdfaf98e5b08186a03ffc6e187387e4bbd52195126c7c6cec6ab07fd5aadc43a0edb7826b237ba8c8aa443f132516fe89ba\n" +
                "AppKey: MyiPad\n" +
                "Flavor: normal";
    }
}
