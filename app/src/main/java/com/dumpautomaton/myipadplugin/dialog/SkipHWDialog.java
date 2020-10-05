package com.dumpautomaton.myipadplugin.dialog;

import com.dumpautomaton.myipadplugin.ActivityHook;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.content.*;

import android.widget.Toast;

public class SkipHWDialog extends SyncDialog {
    public SkipHWDialog(Context context) {
        super(context);
    }
    
    @Override
    public void onCreate() {
        setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityHook.getCurrentActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(ActivityHook.getCurrentActivity(), "Hook...", Toast.LENGTH_LONG).show();
                    }
                });
                result = new Object[]{true};
                dialog.dismiss();
            }
        });
    }
}
