package com.dumpautomaton.myipadplugin.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

public class ModalDialogBuilder extends AlertDialog.Builder {
    public Object result;
    private Handler handler = new SyncDialogMessageHandler(Looper.getMainLooper());

    public ModalDialogBuilder(Context context) {
        super(context);
        this.setOnDismissListener(dialog -> handler.sendMessage(handler.obtainMessage()));
    }

    public Object showWithResult() {
        super.show();
        try {
            Looper.loop();
        } catch (RuntimeException ignored) { }
        return result;
    }

    private static class SyncDialogMessageHandler extends Handler {
        public SyncDialogMessageHandler(Looper looper) {
            super(looper, msg -> {
                throw new RuntimeException();
            });
        }
    }
}
