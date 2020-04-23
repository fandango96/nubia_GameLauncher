package cn.nubia.commonui;

import android.content.Context;

public class VersionName {
    public static String getVersionName(Context context) {
        return context.getResources().getString(R.string.nubia_common_versionName);
    }
}
