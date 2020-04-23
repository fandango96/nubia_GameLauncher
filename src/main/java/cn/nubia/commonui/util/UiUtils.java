package cn.nubia.commonui.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.provider.Settings.System;
import cn.nubia.commonui.R;

public class UiUtils {
    public static boolean isNubiaUi(Context context) {
        TypedArray b = context.obtainStyledAttributes(new int[]{R.attr.isNubiaStyle});
        boolean isNubiaStyle = b.getBoolean(0, false);
        b.recycle();
        return isNubiaStyle;
    }

    public static int getAnimationSwitch(Context context) {
        return System.getInt(context.getContentResolver(), "dynamic_effect", 0);
    }

    public static boolean isFullScreenWindow(int flag) {
        return (flag & 1024) != 0;
    }

    public static boolean isImmersedStatusBar(int flag) {
        return ((67108864 & flag) == 0 && (134217728 & flag) == 0 && (Integer.MIN_VALUE & flag) == 0) ? false : true;
    }
}
