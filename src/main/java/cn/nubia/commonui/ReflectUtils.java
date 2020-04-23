package cn.nubia.commonui;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectUtils {
    public static Object invoke(Object object, String methodName, boolean hasReturn, boolean isStatic) {
        return invoke(object, methodName, hasReturn, isStatic, null, null);
    }

    public static Object invoke(Object object, String methodName, boolean hasReturn, boolean isStatic, Object[] args, Class<?>... parameterTypes) {
        Class clazz;
        if (object == null) {
            return null;
        }
        Object result = null;
        Object host = object;
        if (!isStatic) {
            try {
                clazz = host.getClass();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            clazz = Class.forName(String.valueOf(host));
            host = null;
        }
        Method method = getMethod(clazz, methodName, parameterTypes);
        if (method == null) {
            return null;
        }
        boolean isAccessible = method.isAccessible();
        if (!isAccessible) {
            method.setAccessible(true);
        }
        if (hasReturn) {
            result = method.invoke(host, args);
        } else {
            method.invoke(host, args);
        }
        method.setAccessible(isAccessible);
        return result;
    }

    public static Object getStyleable(String fieldName) {
        Field field = null;
        try {
            field = Class.forName("com.android.internal.R$styleable").getDeclaredField(fieldName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e2) {
            e2.printStackTrace();
        }
        Object mIds = null;
        if (field == null) {
            return mIds;
        }
        try {
            return field.get(null);
        } catch (IllegalAccessException e3) {
            e3.printStackTrace();
            return mIds;
        } catch (IllegalArgumentException e4) {
            e4.printStackTrace();
            return mIds;
        }
    }

    public static Object getFromInternalR(String type, String fieldName) {
        Field field = null;
        try {
            Class clazz = Class.forName("com.android.internal.R$" + type);
            if (clazz != null) {
                field = clazz.getDeclaredField(fieldName);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e2) {
            e2.printStackTrace();
        }
        Object ids = null;
        if (field == null) {
            return ids;
        }
        try {
            return field.get(null);
        } catch (IllegalAccessException e3) {
            e3.printStackTrace();
            return ids;
        } catch (IllegalArgumentException e4) {
            e4.printStackTrace();
            return ids;
        }
    }

    public static Object getValueByName(Object object, String fieldName) {
        if (object == null) {
            return null;
        }
        Field field = getField(object.getClass(), fieldName);
        if (field == null) {
            return null;
        }
        boolean isAccessible = field.isAccessible();
        if (!isAccessible) {
            field.setAccessible(true);
        }
        Object value = null;
        try {
            value = field.get(object);
            field.setAccessible(isAccessible);
            return value;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return value;
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
            return value;
        }
    }

    public static Object newInstanceByConstructor(String className, Object[] args, Class<?>... parameterTypes) {
        Class clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }
        Constructor constructor = null;
        if (clazz == null) {
            return null;
        }
        try {
            Constructor[] declaredConstructors = clazz.getDeclaredConstructors();
            constructor = clazz.getDeclaredConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        if (constructor == null) {
            return null;
        }
        boolean z = false;
        try {
            return constructor.newInstance(args);
        } catch (InstantiationException e2) {
            e2.printStackTrace();
            return z;
        } catch (IllegalAccessException e3) {
            e3.printStackTrace();
            return z;
        } catch (IllegalArgumentException e4) {
            e4.printStackTrace();
            return z;
        } catch (InvocationTargetException e5) {
            e5.printStackTrace();
            return z;
        }
    }

    private static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        if (clazz == null) {
            return null;
        }
        try {
            return clazz.getDeclaredMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            return getMethod(clazz.getSuperclass(), methodName, parameterTypes);
        }
    }

    private static Field getField(Class<?> clazz, String fieldName) {
        if (clazz == null) {
            return null;
        }
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            return getField(clazz.getSuperclass(), fieldName);
        }
    }
}
