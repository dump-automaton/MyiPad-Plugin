package com.dumpautomaton.myipadplugin;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class PluginPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent xmlIntent = new Intent();
        ComponentName component = new ComponentName("com.dumpautomaton.myipadplugin", "com.dumpautomaton.myipadplugin.MainActivity");
        xmlIntent.setComponent(component);
        addPreferencesFromIntent(xmlIntent);

        Preference closePreference = findPreference("remove_fragment");
        final Fragment thisFragment = this;
        closePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                getFragmentManager().beginTransaction().remove(thisFragment).commit();
                return true;
            }
        });
    }
}
