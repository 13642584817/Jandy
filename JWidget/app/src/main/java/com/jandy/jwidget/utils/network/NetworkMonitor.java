package com.jandy.jwidget.utils.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.telephony.TelephonyManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;


import java.io.IOException;

/**
 * 网络监听器
 */
public class NetworkMonitor {

    public static final int NETWORK_TYPE_UNKNOWN = 0; //未知
    public static final int NETWORK_TYPE_WIFI = 1; //wifi网络
    public static final int NETWORK_TYPE_2G = 2; //2G网络
    public static final int NETWORK_TYPE_3G = 3; //3G网络
    public static final int NETWORK_TYPE_4G = 4; //4G网络
    private final Context mContext;
    private ConnectivityManager mConnectivityMgr;
    private NetworkStateReceiver mNetStateReceiver;
    private NetworkStateCallback mNetStateCallback;

    private INetworkStateCallback mStateCallback;

    private Network mAvailableNetwork;

    public NetworkMonitor(@NonNull Context context) {
        mContext = context;
        mConnectivityMgr = (ConnectivityManager) mContext.getSystemService(
                Context.CONNECTIVITY_SERVICE);
    }

    // ====================== 网络状态监听-start =========================

    /**
     * 注册监听
     * @param callback
     */
    public void registerNetMonitor(INetworkStateCallback callback) {
        mStateCallback = callback;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            if (mNetStateReceiver == null) {
                mNetStateReceiver = new NetworkStateReceiver();
                IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                mContext.registerReceiver(mNetStateReceiver, filter);
            }
        } else {
            if (mNetStateCallback == null) {
                mNetStateCallback = new NetworkStateCallback();
                NetworkRequest request = new NetworkRequest.Builder().build();
                mConnectivityMgr.registerNetworkCallback(request, mNetStateCallback);
            }
        }
    }

    /**
     * 注销网络监听
     */
    public void unregisterNetMonitor() {
        mStateCallback = null;
        mAvailableNetwork = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            if (mNetStateReceiver != null) {
                mContext.unregisterReceiver(mNetStateReceiver);
                mNetStateReceiver = null;
            }
        } else {
            if (mNetStateCallback != null) {
                mConnectivityMgr.unregisterNetworkCallback(mNetStateCallback);
                mNetStateCallback = null;
            }
        }
    }

    public interface INetworkStateCallback {
        void onAvailable();

        void onDisconnect();
    }

    private class NetworkStateReceiver extends BroadcastReceiver {
        private final String TAG = NetworkStateReceiver.class.getSimpleName();

        @Override
        public void onReceive(Context context, Intent intent) {
            if (checkNetworkAvailable()) {
                if (mStateCallback != null)
                    mStateCallback.onAvailable();
            } else {
                if (mStateCallback != null)
                    mStateCallback.onDisconnect();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class NetworkStateCallback extends ConnectivityManager.NetworkCallback {
        @Override
        public void onAvailable(Network network) {
            super.onAvailable(network);
            if (!isEqualNetwork(network, mAvailableNetwork)) {
                mAvailableNetwork = network;
                if (mStateCallback != null)
                    mStateCallback.onAvailable();
            }
        }

        @Override
        public void onLost(Network network) {
            super.onLost(network);
            boolean checkNetId = true;
            try {
                int availableNetId = Integer.parseInt(mAvailableNetwork.toString());
                int lostNetId = Integer.parseInt(network.toString());
                checkNetId = lostNetId >= availableNetId;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            if (!checkNetworkAvailable() && checkNetId) {
                if (mStateCallback != null)
                    mStateCallback.onDisconnect();
            }
        }

        @Override
        public void onUnavailable() {
            super.onUnavailable();
        }
    }

    private boolean isEqualNetwork(Network network1, Network network2) {
        return network1 != null && network2 != null && network1.equals(network2);
    }

    public boolean checkNetworkAvailable() {
        NetworkInfo activeNetInfo = mConnectivityMgr.getActiveNetworkInfo();
        if (activeNetInfo == null) {
            return false;
        } else {
            //LogUtils.i(TAG, "<checkNetworkAvailable> netInfo=" + activeNetInfo);
            return activeNetInfo.isConnected();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private boolean checkNetwork21() {
        Network[] networks = mConnectivityMgr.getAllNetworks();
        boolean result = false;
        for (Network network : networks) {
            NetworkInfo info = mConnectivityMgr.getNetworkInfo(network);
            result = result || info.isConnected();
        }
        return result;
    }

    public static boolean isNetworkAvailable(Context context) {
        if (context == null) return false;
        return isNetworkConnected(context) && isNetworkOnline();
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private static boolean isNetworkOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("ping -c 3 www.baidu.com");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static boolean isNetworkOnline23(Context context) {
        ConnectivityManager connectMgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities networkCapabilities = connectMgr.getNetworkCapabilities(connectMgr.getActiveNetwork());
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
    }

    public int getCurrentNetType() {
        int type = NETWORK_TYPE_UNKNOWN;
        NetworkInfo info = mConnectivityMgr.getActiveNetworkInfo();
        if (info == null || !info.isConnected()) {
            return type;
        }
        if (info.getType() == ConnectivityManager.TYPE_WIFI) {
            type = NETWORK_TYPE_WIFI;
        } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
            type = getNetworkClass(info.getSubtype());
        }
        return type;
    }

    private int getNetworkClass(int networkType) {
        final int MAX_NETWORK_TYPE = 19;
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_GSM:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN://api<8 : replace by 11
                return NETWORK_TYPE_2G;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
            case TelephonyManager.NETWORK_TYPE_EHRPD: //api<11 : replace by 12
            case TelephonyManager.NETWORK_TYPE_HSPAP: //api<13 : replace by 15
            case TelephonyManager.NETWORK_TYPE_TD_SCDMA: //api<25 : replace by 17
                return NETWORK_TYPE_3G;
            case TelephonyManager.NETWORK_TYPE_LTE: //api<11 : replace by 13
            case TelephonyManager.NETWORK_TYPE_IWLAN: //api<25 : replace by 18
            case MAX_NETWORK_TYPE: //TelephonyManager.NETWORK_TYPE_LTE_CA
                return NETWORK_TYPE_4G;
            default:
                //Max network type number. Update as new types are added. Don't add negative types
                //MAX_NETWORK_TYPE = NETWORK_TYPE_LTE_CA;
                if(networkType > MAX_NETWORK_TYPE){
                    return NETWORK_TYPE_4G;
                } else {
                    return NETWORK_TYPE_UNKNOWN;
                }
        }
    }
}
