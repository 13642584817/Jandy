package com.jandy.jwidget.utils;

import android.os.Environment;
import android.text.TextUtils;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.Utils;
import com.tencent.mmkv.MMKV;

import java.io.File;

public class DeviceUtils {
    public static final String TAG = DeviceUtils.class.getName();
    public static final String MMKV_FILE_DEVICE_SETTINGS = "ntt_l1_device_settings"; //设置配置信息
    public static String DIR_CM_DATA_ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "NttData" + File.separator;
    public static final String MMKV_KEY_DEVICE_MOBILE_NET_DOWNLOAD = "ntt_kv_device_mobile_net_download"; //4g网络缓存开关

    /**
     * 获取4G下载的开关是否打开
     *
     * @return
     */
    public static boolean getDevice4GDownload() {
        try {
            boolean v = UtMMKV.getInstance(MMKV_FILE_DEVICE_SETTINGS, MMKV.MULTI_PROCESS_MODE, UtMMKV.getMMKVCryptKey(), DIR_CM_DATA_ROOT_PATH).decodeBool(MMKV_KEY_DEVICE_MOBILE_NET_DOWNLOAD, false);
            return v;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void saveDevice4GDownload(boolean v) {
        try {
            UtMMKV.getInstance(MMKV_FILE_DEVICE_SETTINGS, MMKV.MULTI_PROCESS_MODE, UtMMKV.getMMKVCryptKey(), DIR_CM_DATA_ROOT_PATH).encode(MMKV_KEY_DEVICE_MOBILE_NET_DOWNLOAD, v);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否显示缓存按钮
     *
     * @return
     */
    public static boolean isShowCacheButton() {
        if (NetworkUtils.isWifiConnected()) {
            return true;
        }
        if ("m1".equals(CmdUtils.deviceProductMode()) || "m1c".equals(CmdUtils.deviceProductMode())) {
            if (M1DeviceInfoUtil.isWiFiVer()) {
                return false;
            }
            if (DeviceUtils.getDevice4GDownload()) {//电话版，判断4g下载开关是否打开,并且是蜂窝网络
                return true;
            }
        }

        if ("m1o".equals(CmdUtils.deviceProductMode()) || "m1w".equals(CmdUtils.deviceProductMode())) {
            //获取正在使用的流量卡槽
            int type = DeviceNetworkUtils.getMobileSlotId(Utils.getApp());
            if (type == 0 && DeviceUtils.getDevice4GDownload()) {//正在使用sim卡，并且4g允许下载的开关打开的
                return true;
            }
        }
        return false;
    }

    /**
     * 目标版本是否大于当前版本
     *
     * @param target   目标版本
     * @param original 当前版本
     * @return
     */
    public static boolean isNewUpgradeVer(String target, String original) {
        if (TextUtils.isEmpty(target) || TextUtils.isEmpty(original)) return false;
        String[] targetArray = target.split("\\.");
        String[] originalArray = original.split("\\.");
        int targetCount = targetArray.length;
        int originCount = originalArray.length;
        int verCount = originCount;
        if (targetCount > originCount) {
            verCount = targetCount;
        }
        int lastNum = verCount - 1;
        for (int i = 0; i < verCount; i++) {
            String tarVer = "0";
            String oriVer = "0";
            if (i < targetArray.length) {
                tarVer = targetArray[i];
            }
            if (i < originalArray.length) {
                oriVer = originalArray[i];
            }

            if (!TextUtils.isDigitsOnly(tarVer) || !TextUtils.isDigitsOnly(oriVer))
                return false;
            if (Integer.parseInt(tarVer) > Integer.parseInt(oriVer)) {
                if (i < lastNum) {
                    return true;
                }
                return true;
            }
        }
        return false;
    }
}
