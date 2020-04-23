package cn.nubia.gamelauncher.util;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import java.util.Date;
import java.util.Locale;

public class TimerServiceUtil {
    private static final String DATE_KEY = "date";
    private static final String SHARED_PERFERENCES_FILES = "game_launcher_timer";
    public static final String SHARED_PREFERENCES_NAME = "data";
    private static final String TIMER_KEY = "timer";
    public static final String TIME_OUT_4H = "time_out_4h";
    public static final String WEEKLY_TIME_OUT = "weekly_time_out";
    private static Date mDate;

    public static boolean isUpdateDate(Context context) {
        boolean isUpdate;
        Date now = new Date();
        mDate = readDataTosharedPrefs(context);
        if (mDate == null) {
            return resetDatas();
        }
        if (now.getDate() > mDate.getDate() || now.getMonth() > mDate.getMonth() || now.getYear() > mDate.getYear()) {
            isUpdate = true;
        } else {
            isUpdate = false;
        }
        if (!isUpdate || now.getHours() < 5) {
            return false;
        }
        return resetDatas();
    }

    public static boolean isGameTimeWeeklyRemind(Context context) {
        Date now = new Date();
        if (now == null) {
            return false;
        }
        int hours = now.getHours();
        String week = String.format(Locale.US, "%ta", new Object[]{now});
        if (week == null) {
            return false;
        }
        if ((week.equals("Mon") && hours >= 9) || (week.equals("Tues") && hours < 9)) {
            return true;
        }
        if (getWeeklyTimeoutValue(context)) {
            return false;
        }
        setWeeklyTimeoutValue(context, true);
        return false;
    }

    private static boolean resetDatas() {
        mDate = new Date();
        return true;
    }

    public static Date readDataTosharedPrefs(Context context) {
        long value = context.getSharedPreferences(SHARED_PERFERENCES_FILES, 0).getLong(DATE_KEY, -1);
        if (value == -1) {
            return null;
        }
        return new Date(value);
    }

    public static int readTimerTosharedPrefs(Context context) {
        return context.getSharedPreferences(SHARED_PERFERENCES_FILES, 0).getInt(TIMER_KEY, -1);
    }

    public static void writeDataTosharedPrefs(Context context, long dateTime) {
        context.getSharedPreferences(SHARED_PERFERENCES_FILES, 0).edit().putLong(DATE_KEY, dateTime).apply();
    }

    public static void writeTimerTosharedPrefs(Context context, int time) {
        context.getSharedPreferences(SHARED_PERFERENCES_FILES, 0).edit().putInt(TIMER_KEY, time).apply();
    }

    public static void setTimeOut4HValue(Context context) {
        Editor editor = context.getSharedPreferences("data", 0).edit();
        editor.putBoolean(TIME_OUT_4H, false);
        editor.apply();
    }

    public static boolean getTimeOut4HValue(Context context) {
        return context.getSharedPreferences("data", 0).getBoolean(TIME_OUT_4H, true);
    }

    public static void setWeeklyTimeoutValue(Context context, boolean flag) {
        Editor editor = context.getSharedPreferences("data", 0).edit();
        editor.putBoolean(WEEKLY_TIME_OUT, flag);
        editor.apply();
    }

    public static boolean getWeeklyTimeoutValue(Context context) {
        return context.getSharedPreferences("data", 0).getBoolean(WEEKLY_TIME_OUT, false);
    }
}
