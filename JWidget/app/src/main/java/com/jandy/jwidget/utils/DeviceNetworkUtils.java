package com.jandy.jwidget.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.blankj.utilcode.util.Utils;
import com.jandy.jwidget.utils.network.NetworkType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DeviceNetworkUtils {

    /**
     * 获取当前连接的网络类型
     *
     * @return
     */
    public static int getConnectNetworkType() {
        ConnectivityManager cm = (ConnectivityManager) Utils.getApp().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return NetworkType.NETWORK_NO;
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                return NetworkType.NETWORK_WIFI;
            } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                switch (info.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_GSM:
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        return NetworkType.NETWORK_2G;

                    case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        return NetworkType.NETWORK_3G;

                    case TelephonyManager.NETWORK_TYPE_IWLAN:
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        return NetworkType.NETWORK_4G;

                    //                    case TelephonyManager.NETWORK_TYPE_NR:
                    //                        return NetworkType.NETWORK_5G;
                    default:
                        String subtypeName = info.getSubtypeName();
                        if (subtypeName.equalsIgnoreCase("TD-SCDMA")
                                || subtypeName.equalsIgnoreCase("WCDMA")
                                || subtypeName.equalsIgnoreCase("CDMA2000")) {
                            return NetworkType.NETWORK_3G;
                        } else {
                            return NetworkType.NETWORK_UNKNOWN;
                        }
                }
            } else {
                return NetworkType.NETWORK_UNKNOWN;
            }
        }
        return NetworkType.NETWORK_NO;
    }


    /**
     * 设置飞行模式是否开启
     *
     * @param enable
     */
    public static void setAirplaneEnabled(boolean enable) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            Settings.System.putInt(Utils.getApp().getContentResolver(), Settings.System.AIRPLANE_MODE_ON, enable ? 1 : 0);
        } else {
            Settings.Global.putInt(Utils.getApp().getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, enable ? 1 : 0);
        }
        Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intent.putExtra("state", enable);
        Utils.getApp().sendBroadcast(intent);
    }

    /**
     * 飞行模式是否开启
     *
     * @return
     */
    public static boolean getAirplaneEnabled() {
        int ret;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            ret = Settings.System.getInt(Utils.getApp().getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0);
        } else {
            ret = Settings.Global.getInt(Utils.getApp().getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0);
        }
        if (ret == 0)
            return false;

        return true;
    }

    /**
     * 设置流量开发是否开启
     *
     * @param enable
     */
    public static void setMobileDataEnabled(boolean enable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TelephonyManager telephonyManager = (TelephonyManager) Utils.getApp().getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager == null) return;
            try {
                Method method = telephonyManager.getClass().getDeclaredMethod("setDataEnabled", boolean.class);
                method.invoke(telephonyManager, enable);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            ConnectivityManager connectivityManager = (ConnectivityManager) Utils.getApp().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager == null) return;
            try {
                Method method = connectivityManager.getClass().getDeclaredMethod("setMobileDataEnabled", boolean.class);
                method.invoke(connectivityManager, enable);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 流量开关是否开启
     *
     * @return
     */
    public static boolean getMobileDataEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TelephonyManager telephonyManager = (TelephonyManager) Utils.getApp().getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager == null) return false;
            try {
                Method method = telephonyManager.getClass().getDeclaredMethod("getDataEnabled");
                return (boolean) method.invoke(telephonyManager);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            ConnectivityManager connectivityManager = (ConnectivityManager) Utils.getApp().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager == null) return false;
            try {
                Method method = connectivityManager.getClass().getMethod("getMobileDataEnabled");
                return (boolean) method.invoke(connectivityManager);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    public static boolean isUseMobileData(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取当前正在使用的流量的卡槽
     *
     * @param context
     * @return 0：sim卡 1：红茶 -1:获取错误(包含网络没通)
     */
    @SuppressLint("MissingPermission")
    public static int getDataSlotId(Context context) {
        int slotId = -1;
        if (!isUseMobileData(context)) return slotId;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                int subId = SubscriptionManager.getDefaultDataSubscriptionId();
                slotId = SubscriptionManager.from(context).getActiveSubscriptionInfo(subId).getSimSlotIndex();
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return slotId;
    }

    /**
     * 获取当前正在使用的流量的卡槽
     *
     * @param context
     * @return 0：sim卡 1：红茶 -1:获取错误(包含网络没通)
     */
    @SuppressLint("MissingPermission")
    public static int getMobileSlotId(Context context) {
        int slotId = -1;
        if (!getMobileDataEnabled()) return slotId;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                int subId = SubscriptionManager.getDefaultDataSubscriptionId();
                slotId = SubscriptionManager.from(context).getActiveSubscriptionInfo(subId).getSimSlotIndex();
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return slotId;
    }


    /**
     * 设置数据流量的卡槽
     * 0：SIM卡
     * 1：红茶
     *
     * @param slot
     * @return 是否切换成功
     */
    @SuppressLint("MissingPermission")
    public static boolean setDataSlot(Context context, int slot) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                SubscriptionManager sm = SubscriptionManager.from(context);
                int subId = sm.getActiveSubscriptionInfoForSimSlotIndex(slot).getSubscriptionId();
                Method method = sm.getClass().getMethod("setDefaultDataSubId", int.class);
                if (method != null) {
                    method.invoke(sm, subId);
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        } else {
            return true;
        }
    }


    /**
     * 是否插入smi卡，默认会有一个牛听听王卡，所以判断当前smi卡数量是否等于2
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public static boolean readSIMCard() {
        if (ActivityCompat.checkSelfPermission(Utils.getApp(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        int count = SubscriptionManager.from(Utils.getApp()).getActiveSubscriptionInfoCount();

        TelephonyManager tm = (TelephonyManager) Utils.getApp().getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null) {
        }
        return count == 2;
    }


}
