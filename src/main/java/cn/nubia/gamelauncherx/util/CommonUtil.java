package cn.nubia.gamelauncherx.util;

import android.content.ComponentName;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import cn.nubia.gamelauncherx.GameLauncherApplication;
import cn.nubia.gamelauncherx.R;
import cn.nubia.gamelauncherx.commoninterface.NeoGameDBColumns;

public class CommonUtil {
    public static final String GAMESPACE_PACKAGENAME = "cn.nubia.gamelauncher";

    public static String convertToShowStateText(String status) {
        if (!TextUtils.isEmpty(status)) {
            if (NeoGameDBColumns.STATUS_CONNECT.equals(status)) {
                return GameLauncherApplication.CONTEXT.getResources().getString(R.string.connecting);
            }
            if (NeoGameDBColumns.STATUS_DOWNLOADING.equals(status)) {
                return GameLauncherApplication.CONTEXT.getResources().getString(R.string.downloading);
            }
            if (NeoGameDBColumns.STATUS_PAUSE.equals(status)) {
                return GameLauncherApplication.CONTEXT.getResources().getString(R.string.paused);
            }
            if (NeoGameDBColumns.STATUS_IN_INSTALLTION.equals(status)) {
                return GameLauncherApplication.CONTEXT.getResources().getString(R.string.installing);
            }
        }
        return null;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap b = Bitmap.createBitmap(Math.max(drawable.getIntrinsicWidth(), 1), Math.max(drawable.getIntrinsicHeight(), 1), Config.ARGB_8888);
        Canvas c = new Canvas(b);
        drawable.setBounds(0, 0, b.getWidth(), b.getHeight());
        drawable.draw(c);
        c.setBitmap(null);
        return b;
    }

    public static ComponentName createComponentName(String componentStr) {
        try {
            return new ComponentName(componentStr.substring(0, componentStr.indexOf(",")), componentStr.substring(componentStr.indexOf(",") + 1, componentStr.length()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String convertPackageName(String componentStr) {
        try {
            return componentStr.substring(0, componentStr.indexOf(","));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isInternalVersion() {
        return true;
    }

    public static boolean isNX629J_Project() {
        return "NX629J".equals(Build.DEVICE) || "NX629J-EEA".equals(Build.DEVICE);
    }

    public static boolean isNX619J_Project() {
        return "NX619J".equals(Build.DEVICE) || "NX619J-EEA".equals(Build.DEVICE) || "NX609J".equals(Build.DEVICE) || "NX609J-EEA".equals(Build.DEVICE);
    }

    public static boolean isNX609J_Project() {
        return "NX609J".equals(Build.DEVICE) || "NX609J-EEA".equals(Build.DEVICE);
    }

    public static boolean isNX627J_Project() {
        return "NX627J".equals(Build.DEVICE) || "NX627J-EEA".equals(Build.DEVICE);
    }

    public static boolean isRedMagicPhone() {
        if ("NX627J".equals(Build.DEVICE) || "NX627J-EEA".equals(Build.DEVICE)) {
            return false;
        }
        return true;
    }

    public static boolean isNX659J_Project() {
        if ("NX659J".equals(Build.DEVICE) || "NX659J-EEA".equals(Build.DEVICE)) {
            return true;
        }
        return false;
    }

    public static boolean isNX651J_Project() {
        if ("NX651J".equals(Build.DEVICE) || "NX651J-EEA".equals(Build.DEVICE)) {
            return true;
        }
        return false;
    }
}
