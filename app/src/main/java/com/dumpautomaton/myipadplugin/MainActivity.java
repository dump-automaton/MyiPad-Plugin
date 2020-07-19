package com.dumpautomaton.myipadplugin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    PreferenceFragmentCompat mPreferenceFragmentCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SharedPreferences pref = this.createDeviceProtectedStorageContext()
                    .getSharedPreferences("com.dumpautomaton.myipadplugin_preferences", MODE_PRIVATE);
        }

        mPreferenceFragmentCompat = (PreferenceFragmentCompat)
                getSupportFragmentManager().findFragmentById(R.id.preferences_fragment);

        mPreferenceFragmentCompat.findPreference("apply_changes").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                setWorldReadable();
                return true;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        setWorldReadable();
    }

    @SuppressWarnings({"deprecation", "ResultOfMethodCallIgnored"})
    @SuppressLint({"SetWorldReadable", "WorldReadableFiles"})
    private void setWorldReadable() {
        File dataDir;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            dataDir = new File(getApplicationInfo().deviceProtectedDataDir);
        } else {
            dataDir = new File(getApplicationInfo().dataDir);
        }

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