package com.dumpautomaton.myipadplugin.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.dumpautomaton.myipadplugin.R;
import com.dumpautomaton.myipadplugin.UtilsForHook;
import com.dumpautomaton.myipadplugin.ui.PluginPreferenceFragment;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(UtilsForHook.getHardwareInfoWithoutHardware());

        getFragmentManager().beginTransaction().add(new PluginPreferenceFragment(), "pref").commit();
    }
}
