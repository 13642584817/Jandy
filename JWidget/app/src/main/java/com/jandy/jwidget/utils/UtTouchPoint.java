package com.jandy.jwidget.utils;

import android.graphics.Point;
import android.view.View;

public class UtTouchPoint {

    /**
     * 点是否在View中
     * @param view
     * @param point
     * @return
     */
    public static boolean isTouchPointInView(View view, Point point){
        if (view == null || view.getVisibility() != View.VISIBLE) return false;
        int [] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        return point.x >= left && point.x <= left + view.getMeasuredWidth() && point.y >= top && point.y <= top + view.getMeasuredHeight();
    }
}
