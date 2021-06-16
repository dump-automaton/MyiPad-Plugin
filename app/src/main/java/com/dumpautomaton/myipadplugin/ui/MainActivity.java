package com.dumpautomaton.myipadplugin.ui;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dumpautomaton.myipadplugin.R;
import com.dumpautomaton.myipadplugin.UtilsForHook;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(UtilsForHook.getHardwareInfoWithoutHardware());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
        demoModalDialog();
        getFragmentManager().beginTransaction().add(new PluginPreferenceFragment(), "pref").commit();
    }

    public void demoModalDialog() {
        ModalDialogBuilder builder = new ModalDialogBuilder(this);
        final EditText editText = new EditText(this);
        editText.setHint("Leave out blank for default");
        builder.setView(editText);
        builder.setPositiveButton("Yes", (dialog, whichButton) -> {
            builder.result = editText.getText();
            builder.handler.sendMessage(builder.handler.obtainMessage());
        });
        String result = "" + builder.showWithResult();
        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
    }
}
