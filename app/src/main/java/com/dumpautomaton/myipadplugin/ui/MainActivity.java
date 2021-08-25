package com.dumpautomaton.myipadplugin.ui;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.dumpautomaton.myipadplugin.R;
import com.dumpautomaton.myipadplugin.UtilsForHook;
import com.dumpautomaton.myipadplugin.data.CrashLog;
import com.dumpautomaton.myipadplugin.utils.FileIOUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    public boolean isActive() {
        Log.d("MyiPad-Plugin", "Test is active");
        return false;
    }

    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "null";
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Switch safeModeSwitch = findViewById(R.id.safe_mode_switch);
        safeModeSwitch.setChecked(UtilsForHook.isSafeMode());
        safeModeSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
            if (!UtilsForHook.setSafeMode(b)) {
                compoundButton.setChecked(!b);
            }
        });
        refreshInstallStatus();

        ListView crashLogListview = (ListView) findViewById(R.id.crash_log_listview);
        ArrayList<CrashLog> crashLogs = new ArrayList<>();
        String[] logs = FileIOUtils.readFile2String(UtilsForHook.getCrashLogTxtFile()).split("------");
        for (String log : logs) {
            if (!log.equals("")) {
                crashLogs.add(new CrashLog(log));
            }
        }
        crashLogListview.setAdapter(new CrashLogArrayAdapter(this, crashLogs));
    }

    private void refreshInstallStatus() {
        TextView txtInstallError = (TextView) findViewById(R.id.plugin_install_errors);
        View txtInstallContainer = findViewById(R.id.status_container);
        ImageView txtInstallIcon = (ImageView) findViewById(R.id.status_icon);
        Switch safeModeSwitch = findViewById(R.id.safe_mode_switch);

        if (!isActive()) {
            txtInstallError.setText(R.string.plugin_not_active);
            txtInstallError.setTextColor(getResources().getColor(R.color.warning));
            txtInstallContainer.setBackgroundColor(getResources().getColor(R.color.warning));
            txtInstallIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_error));
        } else if (isActive() && safeModeSwitch.isChecked()) {
            txtInstallError.setText(R.string.plugin_safe_mode);
            txtInstallError.setTextColor(getResources().getColor(R.color.amber_500));
            txtInstallContainer.setBackgroundColor(getResources().getColor(R.color.amber_500));
            txtInstallIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_warning));
        } else {
            txtInstallError.setText(R.string.plugin_active);
            txtInstallError.setTextColor(getResources().getColor(R.color.darker_green));
            txtInstallContainer.setBackgroundColor(getResources().getColor(R.color.darker_green));
            txtInstallIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_circle));
        }
    }

    public void demoModalDialog() {
        ModalDialogBuilder builder = new ModalDialogBuilder(this);
        final EditText editText = new EditText(this);
        editText.setHint("Leave out blank for default");
        builder.setView(editText);
        builder.setPositiveButton("Yes", (dialog, whichButton) -> {
            builder.result = editText.getText();
        });
        String result = "" + builder.showWithResult();
        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
    }
}
