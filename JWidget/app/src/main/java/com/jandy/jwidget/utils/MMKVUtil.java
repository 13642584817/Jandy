package com.jandy.jwidget.utils;

import android.text.TextUtils;

import com.tencent.mmkv.MMKV;

import java.util.HashMap;
import java.util.Map;

/**
 * 封装了腾讯的MMKV，能取代SharePreferences 做键值对存储
 * 存储的内容是没有加密的，如需加密需要进一步做加密处理
 * https://github.com/Tencent/MMKV/wiki/android_tutorial_cn
 */
public class MMKVUtil {

    private static final String MMKV_CRYPT_KEY = "benew"; //mmkv 跨进程key

    private static final String MMKV_KEY_DEF = "MMKV_UTILS_KEY"; //默认文件名，存储的key
    private static final Map<String, MMKVUtil> MMKV_UTILS_MAP = new HashMap<>();
    private MMKV mmkv;

    public static MMKVUtil getInstance() {
        return getInstance("", MMKV.SINGLE_PROCESS_MODE, null);
    }

    /**
     * @param mode     MMKV.SINGLE_PROCESS_MODE /MMKV.MULTI_PROCESS_MODE
     * @param cryptKey
     * @return
     */
    public static MMKVUtil getInstance(int mode, String cryptKey) {
        return getInstance("", mode, cryptKey);
    }


    public static MMKVUtil getInstance(String name) {
        return getInstance(name, MMKV.SINGLE_PROCESS_MODE, null);
    }

    public static MMKVUtil getInstance(String name, int mode, String cryptKey) {
        return getInstance(name, mode, cryptKey, "");
    }

    public static MMKVUtil getInstance(String name, String rootPath, int mode) {
        return getInstance(name, mode, "", rootPath);
    }

    public static MMKVUtil getInstance(String name, int mode, String cryptKey, String rootPath) {
        if (isSpace(name)) name = MMKV_KEY_DEF;
        MMKVUtil mmkvUtils = MMKV_UTILS_MAP.get(name);
        if (mmkvUtils == null) {
            synchronized (MMKVUtil.class) {
                mmkvUtils = MMKV_UTILS_MAP.get(name);
                if (mmkvUtils == null) {
                    mmkvUtils = new MMKVUtil(name, mode, cryptKey, rootPath);
                    MMKV_UTILS_MAP.put(name, mmkvUtils);
                }
            }
        }
        return mmkvUtils;
    }

    private MMKVUtil(String name, int mode, String cryptKey, String rootPath) {
        if (!TextUtils.isEmpty(rootPath)) {
            mmkv = MMKV.mmkvWithID(name, mode, cryptKey, rootPath);
        } else if (MMKV_KEY_DEF.equals(name)) {
            mmkv = MMKV.defaultMMKV(mode, cryptKey);
        } else {
            mmkv = MMKV.mmkvWithID(name, mode, cryptKey);
        }
    }


    private static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }


    public void encode(String key, boolean value) {
        mmkv.encode(key, value);
    }

    public void encode(String key, double value) {
        mmkv.encode(key, value);
    }

    public void encode(String key, String value) {
        mmkv.encode(key, value);
    }

    public void encode(String key, float value) {
        mmkv.encode(key, value);
    }

    public void encode(String key, long value) {
        mmkv.encode(key, value);
    }

    public void encode(String key, int value) {
        mmkv.encode(key, value);
    }

    public void encode(String key, byte[] value) {
        mmkv.encode(key, value);
    }

    public void removeValueForKey(String key) {
        mmkv.removeValueForKey(key);
    }

    public void removeValuesForKeys(String[] keys) {
        if (keys == null || keys.length == 0) throw new IllegalArgumentException("params is error");
        mmkv.removeValuesForKeys(keys);
    }

    public String decodeString(String key) {
        return mmkv.decodeString(key);
    }

    public String decodeString(String key, String defaultValue) {
        return mmkv.decodeString(key, defaultValue);
    }

    public boolean decodeBool(String key) {
        return mmkv.decodeBool(key);
    }

    public boolean decodeBool(String key, boolean defaultValue) {
        return mmkv.decodeBool(key, defaultValue);
    }

    public double decodeDouble(String key) {
        return mmkv.decodeDouble(key);
    }

    public double decodeDouble(String key, double defaultValue) {
        return mmkv.decodeDouble(key, defaultValue);
    }

    public long decodeLong(String key) {
        return mmkv.decodeLong(key,0L);
    }

    public long decodeLong(String key, long defaultValue) {
        return mmkv.decodeLong(key, defaultValue);
    }

    public int decodeInt(String key) {
        return mmkv.decodeInt(key);
    }

    public int decodeInt(String key, int defaultValue) {
        return mmkv.decodeInt(key, defaultValue);
    }

    public float decodeFloat(String key) {
        return mmkv.decodeFloat(key);
    }

    public float decodeFloat(String key, float defaultValue) {
        return mmkv.decodeFloat(key, defaultValue);
    }

    public byte[] decodeByte(String key) {
        return mmkv.decodeBytes(key);
    }

    public boolean containsKey(String key) {
        return mmkv.containsKey(key);
    }

    /**
     * 清除所有
     */
    public void clearAll() {
        mmkv.clearAll();
    }

    /**
     * 清除内存中缓存
     */
    public void clearMemoryCache() {
        mmkv.clearMemoryCache();
    }

    /**
     * 获取CryptKey
     *
     * @return
     */
    public static String getMMKVCryptKey() {
        return MMKV_CRYPT_KEY;
    }
}
