package com.jandy.jwidget.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * M1设备信息
 */
public class M1DeviceInfoUtil {
    private static final String FIRMWARE_PROPERTIES_PATH = "/system/build.prop";
    private static String mDisplay;
    private static String mDisplayVendor;
    private static String mCategoryVer;


    /**
     * 获取屏幕尺寸
     *
     * @param context
     * @return
     */
    public static String getDisplayMetrics(Context context) {
        if (!TextUtils.isEmpty(mDisplay)) return mDisplay;
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        display.getRealMetrics(dm);
        mDisplay = dm.widthPixels + "*" + dm.heightPixels;
        return mDisplay;
    }

    /**
     * 获取供应商
     *
     * @return
     */
    public static String getDisplayVendor() {
        if (!TextUtils.isEmpty(mDisplayVendor)) return mDisplayVendor;
        try {
            mDisplayVendor = UtSystemProperty.getString("ro.sys.displayVendor", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mDisplayVendor;
    }


    /**
     * WiFi/4G版本
     *
     * @return
     */
    public static boolean isWiFiVer() {
        if("m1".equals(CmdUtils.deviceProductMode())){
            if (TextUtils.isEmpty(mCategoryVer)) {
                if (UtSystemProperty.getBoolean("ro.radio.noril", false)) {
                    mCategoryVer = "wifi";
                    return true;
                }
                mCategoryVer = readFirmwareFileFor4G("ro.sys.model");
            }
            return "wifi".equals(mCategoryVer);
        }else if ("m1c".equals(CmdUtils.deviceProductMode())){
            if (TextUtils.isEmpty(mCategoryVer)) {
              String deviceMode=  CmdUtils.m1cDeviceMode();
              if ("0".equals(deviceMode) || "1".equals(deviceMode)){
                  mCategoryVer = "wifi";
                  return true;
              }else if ("2".equals(deviceMode) || "3".equals(deviceMode)){
                  mCategoryVer = "4G";
                  return false;
              }
            }
        }
        return "wifi".equals(mCategoryVer);
    }

    private static String readFirmwareFileFor4G(String key) {
        File f = new File(FIRMWARE_PROPERTIES_PATH);
        if (!f.exists()) return "";
        BufferedReader r = null;
        InputStreamReader is = null;
        FileInputStream fip = null;
        try {
            fip = new FileInputStream(f);
            is = new InputStreamReader(fip);
            r = new BufferedReader(is);
            String line;
            while ((line = r.readLine()) != null) {
                if (!line.contains("ro.sys.model")) continue;
                if (!line.contains("=")) return "";
                return line.split("=")[1];
            }
            return "";
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (r != null)
                    r.close();
                if (is != null)
                    is.close();
                if (fip != null)
                    fip.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static int getFirmwareEmmcSize() {
        try {
            String value = readFirmwareFileFor4G("ro.sys.emmc_size");
            if (TextUtils.isEmpty(value)) return 0;
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
    }
}
