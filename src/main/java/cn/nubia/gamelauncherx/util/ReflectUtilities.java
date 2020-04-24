package cn.nubia.gamelauncherx.util;

import android.content.Context;
import android.os.Binder;
import android.os.IBinder;
import java.lang.reflect.Method;

public class ReflectUtilities {
    private static final String GAME_KEYS_CLASS_NAME = "cn.nubia.game.GameKeysHelper";
    private static final String GAME_KEYS_CLASS_NAME_INTER = "com.android.internal.policy.gamekeys.GameKeysHelper";
    private static final String GAME_MODE_CLASS_NAME = "cn.nubia.game.GameModeHelper";
    private static final int NETWORK_ACCELERATION_OFF_ON = 64;

    public static void openSub(Context context, int sub) {
        try {
            Class<?> cls = Class.forName(getGameKeyClassName());
            cls.getDeclaredMethod("openSub", new Class[]{Context.class, Integer.TYPE}).invoke(cls.newInstance(), new Object[]{context, Integer.valueOf(sub)});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeSub(Context context, int sub) {
        try {
            Class<?> cls = Class.forName(getGameKeyClassName());
            cls.getMethod("closeSub", new Class[]{Context.class, Integer.TYPE}).invoke(cls.newInstance(), new Object[]{context, Integer.valueOf(sub)});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getGameKeysDBValue(Context context) {
        try {
            Class<?> cls = Class.forName(getGameKeyClassName());
            return ((Integer) cls.getMethod("getGameKeysDBValue", new Class[]{Context.class}).invoke(cls.newInstance(), new Object[]{context})).intValue();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int getGameModeDBValue(Context context) {
        try {
            Class<?> cls = Class.forName(GAME_MODE_CLASS_NAME);
            Method method = cls.getDeclaredMethod("getGameModeDBValue", new Class[]{Context.class});
            method.setAccessible(true);
            return ((Integer) method.invoke(cls.newInstance(), new Object[]{context})).intValue();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static boolean geGameModeNetAccOffOn(Context context) {
        return (getGameModeDBValue(context) & 64) != 0;
    }

    private static String getGameKeyClassName() {
        return GAME_KEYS_CLASS_NAME;
    }

    public static void requestCPUBoost() {
        try {
            Class<?> trigger = Class.forName("android.os.BSPApplicationManager$Trigger");
            trigger.getMethod("acquirePerformanceLock", new Class[]{IBinder.class, String.class, Integer.TYPE, Long.TYPE}).invoke(trigger, new Object[]{new Binder(), "startApp", Integer.valueOf(7), Integer.valueOf(500)});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
