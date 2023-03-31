package com.jandy.jwidget.utils.network;


import androidx.annotation.IntDef;


@KeepNotProguard
@IntDef({NetworkType.NETWORK_NO, NetworkType.NETWORK_WIFI, NetworkType.NETWORK_5G, NetworkType.NETWORK_4G, NetworkType.NETWORK_3G, NetworkType.NETWORK_2G, NetworkType.NETWORK_UNKNOWN, NetworkType.NETWORK_ETHERNET})
public @interface NetworkType {
    int NETWORK_NO = 0;
    int NETWORK_WIFI = 1;
    int NETWORK_5G = 2;
    int NETWORK_4G = 3;
    int NETWORK_3G = 4;
    int NETWORK_2G = 5;
    int NETWORK_UNKNOWN = 6;
    int NETWORK_ETHERNET = 7;
}
