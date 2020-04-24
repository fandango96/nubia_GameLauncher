package cn.nubia.gamelauncherx.util;

import android.os.Build;
import android.os.Build.VERSION;

public final class BuildDeviceUtil {
    public static boolean is609Project() {
        if (Build.DEVICE.equals("NX609J")) {
            return true;
        }
        return false;
    }

    public static boolean is619Project() {
        if (Build.DEVICE.equals("NX619J")) {
            return true;
        }
        return false;
    }

    public static boolean isAndroidQ() {
        return VERSION.SDK_INT >= 29;
    }
}
