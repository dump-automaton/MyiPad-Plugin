package com.dumpautomaton.myipadplugin.ui;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.dumpautomaton.myipadplugin.R;
import com.dumpautomaton.myipadplugin.UtilsForHook;
import com.dumpautomaton.myipadplugin.data.CrashLog;
import com.dumpautomaton.myipadplugin.utils.FileIOUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    public static boolean isActive() {
        Log.d("MyiPad-Plugin", "Test is active");
        return false;
    }

    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "null";
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigate(R.id.nav_status);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        navigate(item.getItemId());
        return true;
    }

    private void navigate(final int itemId) {
        //final View elevation = findViewById(R.id.elevation);
        Fragment navFragment = null;
        switch (itemId) {
            case R.id.nav_status:
                //mPrevSelectedId = itemId;
                setTitle(R.string.app_name);
                navFragment = new StatusFragment();
                break;
            case R.id.nav_crash_log:
                setTitle(R.string.crash_log);
                navFragment = new CrashLogFragment();
                break;
        }

        //final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(4));

        if (navFragment != null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            //transaction.setCustomAnimations(R.animator.fade_in, R.animator.fade_out);
            try {
                transaction.replace(R.id.content_frame, navFragment).commit();
            } catch (IllegalStateException ignored) {
            }
        }
    }

    public void demoModalDialog() {
        ModalDialogBuilder builder = new ModalDialogBuilder(this);
        final EditText editText = new EditText(this);
        editText.setHint("Leave out blank for default");
        builder.setView(editText);
        builder.setPositiveButton("Yes", (dialog, whichButton) -> {
            builder.result = editText.getText();
        });
        String result = "" + builder.showWithResult();
        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
    }
}
