package cn.nubia.gamelauncher.util;

import android.util.Log;

public class LogUtil {
    private static final boolean LOGD = true;
    private static final boolean LOGE = true;
    private static final boolean LOGI = true;
    private static final boolean LOGV = true;
    private static final boolean LOGW = true;
    private static final String TAG = "GameSpace";

    public static void v(String obj, String message) {
        Log.v(TAG, "[" + obj + "] " + message);
    }

    public static void d(String obj, String message) {
        Log.d(TAG, "[" + obj + "] " + message);
    }

    public static void i(String obj, String message) {
        Log.i(TAG, "[" + obj + "] " + message);
    }

    public static void w(String obj, String message) {
        Log.w(TAG, "[" + obj + "] " + message);
    }

    public static void e(String obj, String message) {
        Log.e(TAG, "[" + obj + "] " + message);
    }

    public static void v(Object obj, String message) {
        Log.v(TAG, "[" + obj.getClass().getSimpleName() + "] " + message);
    }

    public static void d(Object obj, String message) {
        Log.d(TAG, "[" + obj.getClass().getSimpleName() + "] " + message);
    }

    public static void i(Object obj, String message) {
        Log.i(TAG, "[" + obj.getClass().getSimpleName() + "] " + message);
    }

    public static void w(Object obj, String message) {
        Log.w(TAG, "[" + obj.getClass().getSimpleName() + "] " + message);
    }

    public static void e(Object obj, String message) {
        Log.e(TAG, "[" + obj.getClass().getSimpleName() + "] " + message);
    }
}
