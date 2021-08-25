package com.dumpautomaton.myipadplugin.data;

import java.util.Date;

public class CrashLog {
    public Date date;
    public String packageName;
    public String exceptionName;
    public String stackTrace;

    public CrashLog(String log) {
        this.packageName = log.substring(0, log.indexOf(":"));
        this.date = new Date(Long.parseLong(log.substring(log.indexOf(":") + 1, log.indexOf("\n"))));
        log = log.substring(log.indexOf("\n") + 1);
        this.exceptionName = log.substring(0, log.indexOf("\n"));
        this.stackTrace = log;
    }
}
