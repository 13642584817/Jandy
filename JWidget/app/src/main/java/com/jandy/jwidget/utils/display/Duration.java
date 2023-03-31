package com.jandy.jwidget.utils.display;

import androidx.annotation.IntDef;

@IntDef(value = {
        Duration.LIGHT_TIME_5_S,
        Duration.LIGHT_TIME_10_S,
        Duration.LIGHT_TIME_20_S,
        Duration.LIGHT_TIME_30_S,
        Duration.LIGHT_TIME_40_S,
        Duration.LIGHT_TIME_50_S,
        Duration.LIGHT_TIME_1_M,
        Duration.LIGHT_TIME_2_M,
        Duration.LIGHT_TIME_3_M,
        Duration.LIGHT_TIME_4_M,
        Duration.LIGHT_TIME_5_M,
        Duration.LIGHT_TIME_60_M})
public @interface Duration {
    int LIGHT_TIME_5_S = 5000;
    int LIGHT_TIME_10_S = 10000;
    int LIGHT_TIME_20_S = 20000;
    int LIGHT_TIME_30_S = 30000;
    int LIGHT_TIME_40_S = 40000;
    int LIGHT_TIME_50_S = 50000;
    int LIGHT_TIME_1_M = 60000;
    int LIGHT_TIME_2_M = 120000;
    int LIGHT_TIME_3_M = 180000;
    int LIGHT_TIME_4_M = 240000;
    int LIGHT_TIME_5_M = 300000;
    int LIGHT_TIME_60_M = 3600000;
}
