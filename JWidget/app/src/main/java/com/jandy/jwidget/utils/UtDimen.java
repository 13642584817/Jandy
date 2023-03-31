package com.jandy.jwidget.utils;

import android.content.Context;

public class UtDimen {
    public static float getDisplayDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static int dp2px(Context context, float dp) {
        return (int) ((getDisplayDensity(context) * dp) + 0.5f);
    }

    public static float dp2pxF(Context context, float dp) {
        return getDisplayDensity(context) * dp;
    }

    public static int px2dp(Context context, float px) {
        return (int) (px / getDisplayDensity(context));
    }

    public static int sp2px(Context context, float sp) {
        return (int) ((sp * context.getResources().getDisplayMetrics().scaledDensity) + 0.5f);
    }

    public static int px2sp(Context context, float px) {
        return (int) ((px / context.getResources().getDisplayMetrics().scaledDensity) + 0.5f);
    }
}
