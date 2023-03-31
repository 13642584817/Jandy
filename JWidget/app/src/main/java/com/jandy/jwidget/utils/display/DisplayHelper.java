package com.jandy.jwidget.utils.display;

import android.content.Context;
import android.provider.Settings;

public class DisplayHelper {

    private static final String TAG = DisplayHelper.class.getSimpleName();


    /**
     * 设置屏幕亮度
     *
     * @param context
     * @param value
     */
    public static void setScreenBrightness(Context context, float value) {
        try {
            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, (int) value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 通过亮度值获取亮度等级
     *
     * @param value
     * @return
     */
    private static float getLevelBrightness(int value) {
        if (value <= Brightness.BRIGHTNESS_LEVEL_0) return Brightness.BRIGHTNESS_LEVEL_0;

        if (value <= Brightness.BRIGHTNESS_LEVEL_1) return Brightness.BRIGHTNESS_LEVEL_1;

        if (value <= Brightness.BRIGHTNESS_LEVEL_2) return Brightness.BRIGHTNESS_LEVEL_2;

        if (value <= Brightness.BRIGHTNESS_LEVEL_3) return Brightness.BRIGHTNESS_LEVEL_3;

        if (value <= Brightness.BRIGHTNESS_LEVEL_4) return Brightness.BRIGHTNESS_LEVEL_4;

        if (value <= Brightness.BRIGHTNESS_LEVEL_5) return Brightness.BRIGHTNESS_LEVEL_5;

        if (value <= Brightness.BRIGHTNESS_LEVEL_6) return Brightness.BRIGHTNESS_LEVEL_6;

        if (value <= Brightness.BRIGHTNESS_LEVEL_7) return Brightness.BRIGHTNESS_LEVEL_7;

        if (value <= Brightness.BRIGHTNESS_LEVEL_8) return Brightness.BRIGHTNESS_LEVEL_8;

        if (value <= Brightness.BRIGHTNESS_LEVEL_9) return Brightness.BRIGHTNESS_LEVEL_9;

        return Brightness.BRIGHTNESS_LEVEL_10;
    }

    /**
     * 获取亮度等级
     *
     * @param context
     * @return
     */
    public static float getBrightness(Context context) {
        try {
            int value = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            return getLevelBrightness(value);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return Brightness.BRIGHTNESS_LEVEL_1;
    }


    /**
     * 亮屏时间
     *
     * @param context
     * @return
     */
    public static String getLightTimeLevelDes(Context context) {
        int lightLevel = getLightTimeLevel(context);
        switch (lightLevel) {
            case Duration.LIGHT_TIME_5_S:
                return "5s";
            case Duration.LIGHT_TIME_10_S:
                return "10s";
            case Duration.LIGHT_TIME_20_S:
                return "20s";
            case Duration.LIGHT_TIME_30_S:
                return "30s";
            case Duration.LIGHT_TIME_40_S:
                return "40s";
            case Duration.LIGHT_TIME_50_S:
                return "50s";
            case Duration.LIGHT_TIME_1_M:
                return "1分钟";
            case Duration.LIGHT_TIME_2_M:
                return "2分钟";
            case Duration.LIGHT_TIME_3_M:
                return "3分钟";
            case Duration.LIGHT_TIME_4_M:
                return "4分钟";
            case Duration.LIGHT_TIME_5_M:
                return "5分钟";
            default:
                return "1小时";
        }
    }

    /**
     * 获取亮度等级
     *
     * @param context
     * @return
     */
    public static String getBrightnessLevelDes(Context context) {
        float level = getBrightness(context);
        if (level==Brightness.BRIGHTNESS_LEVEL_10){
            return "11级";
        }
        if (level==Brightness.BRIGHTNESS_LEVEL_9){
            return "10级";
        }
        if (level==Brightness.BRIGHTNESS_LEVEL_8){
            return "9级";
        }
        if (level==Brightness.BRIGHTNESS_LEVEL_7){
            return "8级";
        }
        if (level==Brightness.BRIGHTNESS_LEVEL_6){
            return "7级";
        }
        if (level==Brightness.BRIGHTNESS_LEVEL_5){
            return "6级";
        }
        if (level==Brightness.BRIGHTNESS_LEVEL_4){
            return "5级";
        }
        if (level==Brightness.BRIGHTNESS_LEVEL_3){
            return "4级";
        }
        if (level==Brightness.BRIGHTNESS_LEVEL_2){
            return "3级";
        }
        if (level==Brightness.BRIGHTNESS_LEVEL_1){
            return "2级";
        }
        return "1级";

    }

    /**
     * 获取亮屏时间
     *
     * @param context
     * @return
     */
    public static int getLightTimeLevel(Context context) {
        try {
            int time = (int) Settings.System.getLong(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, Duration.LIGHT_TIME_3_M);
            return getLevelByLightTime(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Duration.LIGHT_TIME_3_M;
    }

    private static int getLevelByLightTime(int duration) {
        if (duration <= Duration.LIGHT_TIME_5_S) return Duration.LIGHT_TIME_5_S;

        if (duration <= Duration.LIGHT_TIME_10_S) return Duration.LIGHT_TIME_10_S;

        if (duration <= Duration.LIGHT_TIME_20_S) return Duration.LIGHT_TIME_20_S;

        if (duration <= Duration.LIGHT_TIME_30_S) return Duration.LIGHT_TIME_30_S;

        if (duration <= Duration.LIGHT_TIME_40_S) return Duration.LIGHT_TIME_40_S;

        if (duration <= Duration.LIGHT_TIME_50_S) return Duration.LIGHT_TIME_50_S;

        if (duration <= Duration.LIGHT_TIME_1_M) return Duration.LIGHT_TIME_1_M;

        if (duration <= Duration.LIGHT_TIME_2_M) return Duration.LIGHT_TIME_2_M;

        if (duration <= Duration.LIGHT_TIME_3_M) return Duration.LIGHT_TIME_3_M;

        if (duration <= Duration.LIGHT_TIME_4_M) return Duration.LIGHT_TIME_4_M;

        if (duration <= Duration.LIGHT_TIME_5_M) return Duration.LIGHT_TIME_5_M;
        return Duration.LIGHT_TIME_60_M;

    }

    /**
     * 设置亮屏时间
     *
     * @param context
     * @param value
     */
    public static void setLightTimeLevel(Context context, long value) {
        try {
            Settings.System.putLong(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取最大亮度值
     *
     * @return 最大亮度值
     */
    public static float getMaxLevelBrightness() {
        return Brightness.BRIGHTNESS_LEVEL_10;
    }

    /**
     * 获取最小亮度值
     *
     * @return 最小亮度值
     */
    public static float getMinLevelBrightness() {
        return Brightness.BRIGHTNESS_LEVEL_0;
    }

    /**
     * 增加一级屏幕亮度
     *
     * @param context 上下文
     */
    public static void increaseScreenBrightness(Context context) {
        float brightness = getBrightness(context);
        try {
            float setValue = brightness + Brightness.BRIGHTNESS_LEVEL_1;
            if (setValue > Brightness.BRIGHTNESS_LEVEL_10) {
                setValue = Brightness.BRIGHTNESS_LEVEL_10;
            }
            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,
                    (int) setValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 降低一级屏幕亮度
     *
     * @param context 上下文
     */
    public static void decreaseScreenBrightness(Context context) {
        float brightness = getBrightness(context);
        try {
            float setValue = brightness - Brightness.BRIGHTNESS_LEVEL_1;
            if (setValue < Brightness.BRIGHTNESS_LEVEL_0) {
                setValue = Brightness.BRIGHTNESS_LEVEL_0;
            }
            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,
                    (int) setValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
