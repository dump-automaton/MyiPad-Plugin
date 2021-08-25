package com.dumpautomaton.myipadplugin.ui;

import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.dumpautomaton.myipadplugin.R;
import com.dumpautomaton.myipadplugin.UtilsForHook;

public class StatusFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.status_fragment, container, false);
        Switch safeModeSwitch = v.findViewById(R.id.safe_mode_switch);
        safeModeSwitch.setChecked(UtilsForHook.isSafeMode());
        safeModeSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
            if (!UtilsForHook.setSafeMode(b)) {
                compoundButton.setChecked(!b);
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshInstallStatus();
    }

    private void refreshInstallStatus() {
        View v = getView();
        TextView txtInstallError = (TextView) v.findViewById(R.id.plugin_install_errors);
        View txtInstallContainer = v.findViewById(R.id.status_container);
        ImageView txtInstallIcon = (ImageView) v.findViewById(R.id.status_icon);
        Switch safeModeSwitch = v.findViewById(R.id.safe_mode_switch);

        if (!MainActivity.isActive()) {
            txtInstallError.setText(R.string.plugin_not_active);
            txtInstallError.setTextColor(getResources().getColor(R.color.warning));
            txtInstallContainer.setBackgroundColor(getResources().getColor(R.color.warning));
            txtInstallIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_error));
        } else if (MainActivity.isActive() && safeModeSwitch.isChecked()) {
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
}
