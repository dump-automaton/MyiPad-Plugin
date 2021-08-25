package com.dumpautomaton.myipadplugin.ui;

import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dumpautomaton.myipadplugin.R;
import com.dumpautomaton.myipadplugin.UtilsForHook;
import com.dumpautomaton.myipadplugin.data.CrashLog;
import com.dumpautomaton.myipadplugin.utils.FileIOUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CrashLogFragment extends ListFragment {
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayList<CrashLog> crashLogs = new ArrayList<>();
        String[] logs = FileIOUtils.readFile2String(UtilsForHook.getCrashLogTxtFile()).split("------");
        for (String log : logs) {
            if (!log.equals("")) {
                crashLogs.add(new CrashLog(log));
            }
        }
        setListAdapter(new CrashLogArrayAdapter(getActivity(), crashLogs));
    }

    public class CrashLogArrayAdapter extends ArrayAdapter<CrashLog> {
        public CrashLogArrayAdapter(Context context, List<CrashLog> objects) {
            super(context, R.layout.list_item_crash_log, R.id.exception_name, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CrashLog crashLog = getItem(position);
            View view = super.getView(position, convertView, parent);
            TextView exceptionName = view.findViewById(R.id.exception_name);
            TextView exceptionDate = view.findViewById(R.id.exception_date);
            exceptionName.setText(crashLog.exceptionName);
            exceptionDate.setText(crashLog.date.getTime().toLocaleString());

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
}
