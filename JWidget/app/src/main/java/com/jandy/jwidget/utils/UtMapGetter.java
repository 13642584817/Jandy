package com.jandy.jwidget.utils;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

/**
 * Map中安全的获取Object的类型值，避免处理强制转换出现的崩溃
 */
public class UtMapGetter {


    /**
     * 获取 Map中的String类型值
     *
     * @param data
     * @param key
     * @return
     */
    public static String getMapObj2String(Map<String, Object> data, String key) {
        if (data == null) return "";

        if (!data.containsKey(key)) return "";

        if (data.get(key) instanceof String) {
            return (String) data.get(key);
        } else {
            return "";
        }
    }

    /**
     * 获取Map中的boolean类型值
     *
     * @param data
     * @param key
     * @return
     */
    public static boolean getMapObj2Boolean(Map<String, Object> data, String key) {
        if (data == null) return false;

        if (!data.containsKey(key)) return false;

        if (data.get(key) instanceof Boolean) {
            return (boolean) data.get(key);
        } else {
            return false;
        }
    }


    /**
     * 获取Map中的Integer类型的值
     *
     * @param data
     * @param key
     * @return
     */
    public static int getMapObj2Int(Map<String, Object> data, String key) {
        if (data == null) {
            return 0;
        }

        if (!data.containsKey(key)) {
            return 0;
        }

        if (data.get(key) == null) {
            return 0;
        }
        try {
            if (data.get(key) instanceof Integer) {
                return (int) data.get(key);
            } else if (data.get(key) instanceof Double) {
                return Double.valueOf(Objects.requireNonNull(data.get(key)).toString()).intValue();
            } else if (data.get(key) instanceof BigDecimal) {
                return Double.valueOf(Objects.requireNonNull(data.get(key)).toString()).intValue();
            } else if (data.get(key) instanceof String) {
                return Integer.parseInt(Objects.requireNonNull(data.get(key)).toString());
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }



    /**
     * 获取Map中的Long类型的值
     *
     * @param data
     * @param key
     * @return
     */
    public static long getMapObj2Long(Map<String, Object> data, String key) {
        if (data == null) return 0L;

        if (!data.containsKey(key)) return 0L;

        if (data.get(key) instanceof Long) {
            return (long) data.get(key);
        } else if (data.get(key) instanceof Integer) {
            return (int) data.get(key);
        } else if (data.get(key) instanceof Double) {
            return (long)((double)data.get(key));
        } else if (data.get(key) instanceof Float) {
            return (long)((float)data.get(key));
        } else {
            return 0L;
        }
    }

    /**
     * 获取Float类型
     *
     * @param data
     * @param key
     * @return
     */
    public static float getMapObjFloat(Map<String, Object> data, String key) {
        if (data == null) return 0f;
        if (!data.containsKey(key)) return 0f;

        if (data.get(key) instanceof Float) {
            return (float) data.get(key);
        } else {
            return 0f;
        }
    }

    /**
     * 获取Double类型
     *
     * @param data
     * @param key
     * @return
     */
    public static double getMapObjDouble(Map<String, Object> data, String key) {
        if (data == null) return 0d;
        if (!data.containsKey(key)) return 0d;
        if (data.get(key) instanceof Double) {
            return (double) data.get(key);
        } else {
            return 0d;
        }
    }


    /**
     * 获取Map中的String,转换成int类型
     *
     * @param data
     * @param key
     * @return
     */
    public static int getMapString2Int(Map<String, Object> data, String key) {
        if (data == null) return 0;
        String value = getMapObj2String(data, key);
        if (!TextUtils.isDigitsOnly(value)) return 0;
        return Integer.parseInt(value);
    }

    /**
     * Map 2 JSONObject
     *
     * @param map
     * @return
     */
    public static JSONObject map2JsonObject(Map<String, Object> map) {
        try {
            String str = JSON.toJSONString(map);
            return JSONObject.parseObject(str);
        } catch (Exception e) {
            return null;
        }
    }


}
