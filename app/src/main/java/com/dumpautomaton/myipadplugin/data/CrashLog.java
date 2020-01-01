package com.dumpautomaton.myipadplugin.data;

import java.util.Calendar;
import java.util.Date;

public class CrashLog {
    public Calendar date;
    public String packageName;
    public String exceptionName;
    public String stackTrace;

    public CrashLog(String log) {
        this.packageName = log.substring(0, log.indexOf(":"));
        this.date = Calendar.getInstance();
        this.date.setTimeInMillis(Long.parseLong(log.substring(log.indexOf(":") + 1, log.indexOf("\n"))));
        log = log.substring(log.indexOf("\n") + 1);
        this.exceptionName = log.substring(0, log.indexOf("\n"));
        this.stackTrace = log;
    }
}
