package com.dumpautomaton.myipadplugin;

import android.app.Activity;
import de.robv.android.xposed.XC_MethodHook;

/**
 * @deprecated Now useless
 */
public class ActivityHook extends XC_MethodHook {
    /* Assure latest read of write */
    private static volatile Activity _currentActivity = null;

    public static Activity getCurrentActivity() {
        return _currentActivity;
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        _currentActivity = (Activity) param.getResult();
    }
}
