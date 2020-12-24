package com.dumpautomaton.myipadplugin.ui;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.robv.android.xposed.XposedHelpers;

public class PluginPreferenceFragment extends PreferenceFragment {
    public static Method dumpLogcatMethod;
    public static String screenCastURL;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent xmlIntent = new Intent();
        ComponentName component = new ComponentName("com.dumpautomaton.myipadplugin", "com.dumpautomaton.myipadplugin.ui.MainActivity");
        xmlIntent.setComponent(component);
        addPreferencesFromIntent(xmlIntent);

        final Preference fakeHardwareInfoPreference = findPreference("fake_hardware_info");
        fakeHardwareInfoPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final EditText editText = new EditText(getActivity());
                editText.setHint("Leave out blank for default");
                builder.setView(editText);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        SharedPreferences sharedPreferences =
                                PreferenceManager.getDefaultSharedPreferences(getActivity());
                        sharedPreferences.edit().putString("fake_hardware_info_content", editText.getText().toString()).apply();
                    }
                });
                builder.show();
                return true;
            }
        });

        final Preference dumpLogcatPreference = findPreference("dump_logcat");
        dumpLogcatPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (dumpLogcatMethod != null) {
                    try {
                        dumpLogcatMethod.invoke(null, "myipad_logcat.txt");
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
        });

        final Preference screenCastPreference = findPreference("screen_cast_with_url");
        screenCastPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final EditText editText = new EditText(getActivity());
                editText.setText(screenCastURL);
                builder.setTitle("Enter Streaming URL").setView(editText);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent intent = new Intent();
                        intent.setClassName(getActivity(), "com.netspace.myipad.ScreenDisplayActivity");
                        intent.putExtra("MJpegServer", editText.getText().toString());
                        startActivity(intent);
                    }
                });
                builder.show();
                return true;
            }
        });
    }
}
