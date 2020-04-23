package cn.nubia.gamelauncher.aimhelper;

import android.text.TextUtils;
import java.lang.reflect.InvocationTargetException;

public class ActivityUtils {
    public static String getCurrentTopPkg() {
        String packageName = null;
        try {
            Class cl = Class.forName("android.app.ActivityTaskManager");
            Object obj = cl.getDeclaredMethod("getService", new Class[0]).invoke(cl, new Object[0]);
            if (obj != null) {
                packageName = (String) Class.forName("android.app.IActivityTaskManager").getDeclaredMethod("getFocusedStackResumedPkg", new Class[0]).invoke(obj, new Object[0]);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e2) {
            e2.printStackTrace();
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
        } catch (IllegalAccessException e4) {
            e4.printStackTrace();
        }
        LogUtil.d("ActivityUtils", "getCurrentTopPkg:" + packageName);
        return packageName;
    }

    public static String getPackageFromComponent(String component) {
        if (!TextUtils.isEmpty(component)) {
            return component.split("/")[0];
        }
        return "";
    }
}
