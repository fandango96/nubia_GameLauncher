package cn.nubia.gamelauncher.util;

import android.text.TextUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ToastUtil {
    public static void showGamemodeToast(String text) {
        if (!TextUtils.isEmpty(text)) {
            try {
                Method method = Class.forName("nubia.os.ApplicationManager$Trigger").getDeclaredMethod("showGamemodeCenterToast", new Class[]{String.class});
                method.setAccessible(true);
                method.invoke(null, new Object[]{text});
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e2) {
                e2.printStackTrace();
            } catch (InvocationTargetException e3) {
                e3.printStackTrace();
            } catch (IllegalAccessException e4) {
                e4.printStackTrace();
            }
        }
    }
}
