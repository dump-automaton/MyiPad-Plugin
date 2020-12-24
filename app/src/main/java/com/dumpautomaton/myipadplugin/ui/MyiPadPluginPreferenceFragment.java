package com.dumpautomaton.myipadplugin.ui;

import android.os.Bundle;
import android.preference.PreferenceCategory;

public class MyiPadPluginPreferenceFragment extends PluginPreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceCategory myipadTweaksCategory = (PreferenceCategory) findPreference("myipad_tweaks");
        myipadTweaksCategory.setEnabled(true);
    }
}
