package com.jandy.jwidget.utils;

import android.content.Context;
import android.media.AudioManager;
import android.provider.Settings;

import com.blankj.utilcode.util.Utils;
import com.blankj.utilcode.util.VolumeUtils;

public class SysVolumeUtils {


    /**
     * 获取最大音量
     *
     * @param type
     * @return
     */
    public static int getMaxVolume(int type) {
        return VolumeUtils.getMaxVolume(type);
    }

    /**
     * 获取当前音量
     *
     * @param type
     * @return
     */
    public static int getVolume(int type) {
        return VolumeUtils.getVolume(type);
    }


    /**
     * 设置音量
     *
     * @param type
     * @param volume
     * @param flags
     */
    public static void setVolume(int type, int volume, int flags) {
        VolumeUtils.setVolume(type, volume, flags);
    }

    /**
     * 增加多媒体音量
     */
    public static void adjustRaiseVolume(int type,int flags) {
        AudioManager am = (AudioManager) Utils.getApp().getSystemService(Context.AUDIO_SERVICE);
        am.adjustStreamVolume(type,AudioManager.ADJUST_RAISE,flags);
    }



    /**
     * 减小播放多媒体音量
     */
    public static void adjustLowerVolume(int type,int flags) {
        AudioManager am = (AudioManager) Utils.getApp().getSystemService(Context.AUDIO_SERVICE);
        am.adjustStreamVolume(type,AudioManager.ADJUST_LOWER,flags);
    }

    /**
     * 触摸提示音开关
     */
    public static void touchSwitch(boolean isChecked) {
        Settings.System.putInt(Utils.getApp().getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, isChecked ? 1 : 0);
        AudioManager mAudioManager = (AudioManager) Utils.getApp().getSystemService(Context.AUDIO_SERVICE);
        if (isChecked) {
            mAudioManager.loadSoundEffects();
        } else {
            mAudioManager.unloadSoundEffects();
        }
    }

    /**
     * 获取提示音开关
     */
    public static int getTouchSwitch() {
        try {
            int anInt = Settings.System.getInt(Utils.getApp().getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED);
            return anInt;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
