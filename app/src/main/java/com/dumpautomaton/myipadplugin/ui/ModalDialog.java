package com.dumpautomaton.myipadplugin.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public abstract class ModalDialog extends AlertDialog {
    public int dialogResult;
    public Handler handler = new SyncDialogMessageHandler(Looper.getMainLooper());

    public ModalDialog(Context context) {
        super(context);
        //setOwnerActivity(context);
        onCreate();
    }

    // set on click listener: handler.sendMessage(handler.obtainMessage());
    public void onCreate() {
        this.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.sendMessage(handler.obtainMessage());
            }
        });
    }

    public int showDialog() {
        super.show();
        try {
            Looper.loop();
        } catch(RuntimeException ignored) { }
        return dialogResult;
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
}
