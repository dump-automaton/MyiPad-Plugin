package com.dumpautomaton.myipadplugin.ui;

import android.Manifest;
import android.app.ActionBar;
import android.app.ListFragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dumpautomaton.myipadplugin.R;
import com.dumpautomaton.myipadplugin.UtilsForHook;
import com.dumpautomaton.myipadplugin.data.CrashLog;
import com.dumpautomaton.myipadplugin.utils.FileIOUtils;

import java.util.ArrayList;
import java.util.List;

public class CrashLogFragment extends ListFragment {
    private CrashLogArrayAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        ArrayList<CrashLog> crashLogs = new ArrayList<>();
        try {
            String[] logs = FileIOUtils.readFile2String(UtilsForHook.getCrashLogTxtFile()).split("------");
            for (String log : logs) {
                if (!log.equals("")) {
                    crashLogs.add(new CrashLog(log));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mAdapter = new CrashLogArrayAdapter(getActivity(), crashLogs);
        setListAdapter(mAdapter);
        setEmptyText("No crash log found.");

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int sixDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, metrics);
        int eightDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, metrics);
        getListView().setDivider(null);
        getListView().setDividerHeight(sixDp);
        getListView().setPadding(eightDp, eightDp, eightDp, eightDp);
        getListView().setClipToPadding(false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ClipboardManager clipboard = (ClipboardManager)
                getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("simple text", mAdapter.getItem(position).stackTrace);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getActivity(), "Crash log is copied to clipboard.", Toast.LENGTH_LONG).show();
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
