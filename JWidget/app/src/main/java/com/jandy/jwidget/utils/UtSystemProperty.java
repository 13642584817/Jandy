package com.jandy.jwidget.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;

import java.lang.reflect.InvocationTargetException;

public class UtSystemProperty {
    @Deprecated
    public static final long PROPERTY_MOBILE_DATA_CONTROL_NOT_OPEN = -1;
    private static final String TAG = "SystemPropertyUtil";

    @SuppressLint("PrivateApi")
    @TargetApi(19)
    @Deprecated
    public static void setSystemProperty(String propertyName, boolean value) {
        try {
             Class<?> c = Class.forName("android.os.SystemProperties");
            c.getMethod("set", String.class, String.class).invoke(c, propertyName, String.valueOf(value));
        } catch (InvocationTargetException | ClassNotFoundException | IllegalAccessException | NoSuchMethodException e) {
        }
    }

    @SuppressLint("PrivateApi")
    @TargetApi(19)
    @Deprecated
    public static void setSystemProperty(String propertyName, long defaultValue) {
        try {
             Class<?> c = Class.forName("android.os.SystemProperties");
            c.getMethod("set", String.class, String.class).invoke(c, propertyName, String.valueOf(defaultValue));
        } catch (InvocationTargetException | ClassNotFoundException | IllegalAccessException | NoSuchMethodException e) {
        }
    }

    @SuppressLint("PrivateApi")
    @TargetApi(19)
    @Deprecated
    public static boolean getSystemProperty(String propertyName, boolean defaultValue) {
        boolean property = defaultValue;
        try {
           Class<?> c = Class.forName("android.os.SystemProperties");
            property = (Boolean) c.getMethod("getBoolean", String.class, Boolean.TYPE).invoke(c, propertyName, defaultValue);
        } catch (InvocationTargetException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException e) {
        }
        return property;
    }

    @TargetApi(19)
    @SuppressLint("PrivateApi")
    @Deprecated
    public static long getSystemProperty(String propertyName, long defaultValue) {
        long property = defaultValue;
        try {
             Class<?> c = Class.forName("android.os.SystemProperties");
            property = (Long) c.getMethod("getLong", String.class, Long.TYPE).invoke(c, propertyName, defaultValue);
        } catch (InvocationTargetException | ClassNotFoundException | IllegalAccessException | NoSuchMethodException e) {
        }
        return property;
    }

    @SuppressLint("PrivateApi")
    public static void setString(String propertyName, String value) {
        try {
            Class.forName("android.os.SystemProperties").getMethod("set", String.class, String.class).invoke(null, propertyName, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setBoolean(String propertyName, boolean value) {
        setString(propertyName, String.valueOf(value));
    }

    public static void setInt(String propertyName, int value) {
        setString(propertyName, String.valueOf(value));
    }

    public static void setLong(String propertyName, long value) {
        setString(propertyName, String.valueOf(value));
    }

    @SuppressLint("PrivateApi")
    public static String getString(String propertyName, String defaultValue) {
        try {
            return (String) Class.forName("android.os.SystemProperties").getMethod("get", String.class, String.class).invoke(null, propertyName, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    @SuppressLint("PrivateApi")
    public static boolean getBoolean(String propertyName, boolean defaultValue) {
        try {
            Boolean invoke = (Boolean) Class.forName("android.os.SystemProperties").getMethod("getBoolean", String.class, Boolean.TYPE).invoke(null, propertyName, Boolean.valueOf(defaultValue));
            if (invoke == null) {
                return defaultValue;
            }
            return invoke;
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    @SuppressLint("PrivateApi")
    public static int getInt(String propertyName, int defaultValue) {
        try {
             Integer invoke = (Integer) Class.forName("android.os.SystemProperties").getMethod("getInt", String.class, Integer.TYPE).invoke(null, propertyName, Integer.valueOf(defaultValue));
            if (invoke == null) {
                return defaultValue;
            }
            return invoke;
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static long getLong(String propertyName, long defaultValue) {
        try {
            Long invoke = (Long) Class.forName("android.os.SystemProperties").getMethod("getLong", String.class, Long.TYPE).invoke(null, propertyName, Long.valueOf(defaultValue));
            if (invoke == null) {
                return defaultValue;
            }
            return invoke.longValue();
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }
}
