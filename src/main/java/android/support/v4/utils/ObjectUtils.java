package android.support.v4.utils;

public class ObjectUtils {
    public static boolean objectEquals(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }

    private ObjectUtils() {
    }
}
