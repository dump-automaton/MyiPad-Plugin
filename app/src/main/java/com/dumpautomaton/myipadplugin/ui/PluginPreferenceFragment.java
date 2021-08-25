package com.dumpautomaton.myipadplugin.ui;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.dumpautomaton.myipadplugin.UtilsForHook;
import com.dumpautomaton.myipadplugin.utils.FileIOUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PluginPreferenceFragment extends PreferenceFragment {
    public static Method dumpLogcatMethod;

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

        final SwitchPreference safeModePreference = (SwitchPreference) findPreference("safe_mode");
        safeModePreference.setChecked(UtilsForHook.isSafeMode());
        safeModePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object object) {
                boolean checked = (boolean) object;
                return UtilsForHook.setSafeMode(checked);
            }
        });

        final Preference restartAppPreference = findPreference("restart_app");
        restartAppPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                UtilsForHook.restartApp(getActivity().getApplication());
                return true;
            }
        });

        final CheckBoxPreference fakeHardwareInfoPreference = (CheckBoxPreference) findPreference("fake_hardware_info");
        fakeHardwareInfoPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object object) {
                if (!((boolean) object)) {
                    return true;
                }
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

        final CheckBoxPreference fakeVersionPreference = (CheckBoxPreference) findPreference("fake_version");
        fakeVersionPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object object) {
                if (!((boolean) object)) {
                    return true;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LinearLayout linearLayout = new LinearLayout(getActivity());
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                final EditText versionCodeEdit = new EditText(getActivity());
                final EditText versionNameEdit = new EditText(getActivity());
                versionCodeEdit.setHint("Version Code (Deprecated)");
                versionNameEdit.setText("5.2.3.52405");
                linearLayout.addView(versionCodeEdit);
                linearLayout.addView(versionNameEdit);
                builder.setView(linearLayout);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        SharedPreferences sharedPreferences =
                                PreferenceManager.getDefaultSharedPreferences(getActivity());
                        sharedPreferences.edit().putString("fake_version_code", versionCodeEdit.getText().toString()).apply();
                        sharedPreferences.edit().putString("fake_version_name", versionNameEdit.getText().toString()).apply();
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
                    } catch (IllegalAccessException | InvocationTargetException e) {
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
