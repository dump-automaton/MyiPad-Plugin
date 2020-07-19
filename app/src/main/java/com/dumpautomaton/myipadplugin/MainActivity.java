package com.dumpautomaton.myipadplugin;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SharedPreferences pref = this.createDeviceProtectedStorageContext()
                    .getSharedPreferences("com.dumpautomaton.myipadplugin_preferences", MODE_PRIVATE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        setWorldReadable();
    }

    @SuppressWarnings({"deprecation", "ResultOfMethodCallIgnored"})
    @SuppressLint({"SetWorldReadable", "WorldReadableFiles"})
    private void setWorldReadable() {
        File dataDir = new File(getApplicationInfo().dataDir);
        File prefsDir = new File(dataDir, "shared_prefs");
        File prefsFile = new File(prefsDir, BuildConfig.APPLICATION_ID + "_preferences.xml");
        if (prefsFile.exists()) {
            for (File file : new File[]{dataDir, prefsDir, prefsFile}) {
                file.setReadable(true, false);
                file.setExecutable(true, false);
            }
        }
    }
}