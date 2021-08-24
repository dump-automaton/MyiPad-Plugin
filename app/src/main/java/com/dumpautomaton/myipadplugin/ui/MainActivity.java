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
import android.widget.TextView;
import android.widget.Toast;

import com.dumpautomaton.myipadplugin.R;

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

        refreshInstallStatus();
    }

    private void refreshInstallStatus() {
        TextView txtInstallError = (TextView) findViewById(R.id.plugin_install_errors);
        View txtInstallContainer = findViewById(R.id.status_container);
        ImageView txtInstallIcon = (ImageView) findViewById(R.id.status_icon);

        if (!isActive()) {
            txtInstallError.setText(R.string.plugin_not_active);
            txtInstallError.setTextColor(getResources().getColor(R.color.warning));
            txtInstallContainer.setBackgroundColor(getResources().getColor(R.color.warning));
            txtInstallIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_error));
        } else {
            txtInstallError.setText(R.string.plugin_active);
            txtInstallError.setTextColor(getResources().getColor(R.color.darker_green));
            txtInstallContainer.setBackgroundColor(getResources().getColor(R.color.darker_green));
            txtInstallIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_circle));
        }
    }

    public void demoModalDialog() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
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
