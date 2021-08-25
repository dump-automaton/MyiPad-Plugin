package com.dumpautomaton.myipadplugin.ui;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dumpautomaton.myipadplugin.R;
import com.dumpautomaton.myipadplugin.data.CrashLog;

import java.util.List;

public class CrashLogArrayAdapter extends ArrayAdapter<CrashLog> {
    public CrashLogArrayAdapter(Context context, List<CrashLog> objects) {
        super(context, R.layout.list_item_crash_log, R.id.exception_name, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CrashLog crashLog = getItem(position);
        View view = super.getView(position, convertView, parent);
        TextView exceptionName = view.findViewById(R.id.exception_name);
        exceptionName.setText(crashLog.exceptionName);

        PackageManager pm = this.getContext().getPackageManager();
        ApplicationInfo info = null;
        try {
            info = pm.getApplicationInfo(crashLog.packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        ((ImageView) view.findViewById(R.id.icon)).setImageDrawable(info.loadIcon(pm));
        return view;
    }
}