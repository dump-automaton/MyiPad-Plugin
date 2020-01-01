package com.dumpautomaton.myipadplugin.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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

        FrameLayout downloadMyiPad = v.findViewById(R.id.download_myipad);
        downloadMyiPad.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LinearLayout linearLayout = new LinearLayout(getActivity());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            final EditText schoolDomain = new EditText(getActivity());
            final EditText versionName = new EditText(getActivity());
            schoolDomain.setHint("example.lexuewang.cn");
            versionName.setHint("5.2.3.52405");
            linearLayout.addView(schoolDomain);
            linearLayout.addView(versionName);
            builder.setView(linearLayout);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String domain = schoolDomain.getText().toString();
                    if (!domain.contains(":")){
                        domain = domain + ":8001";
                    }
                    if (!domain.contains("http")) {
                        domain = "http://" + domain;
                    }
                    Uri uri = Uri.parse(domain + "/updates/release/updates/MyiPad_" + versionName.getText().toString() + ".apk");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
            builder.show();
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
