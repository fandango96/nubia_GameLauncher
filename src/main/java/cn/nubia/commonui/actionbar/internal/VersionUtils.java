package cn.nubia.commonui.actionbar.internal;

import android.os.Build.VERSION;

public class VersionUtils {
    private VersionUtils() {
    }

    public static boolean isAtLeastL() {
        return VERSION.SDK_INT >= 21;
    }
}
