package com.jandy.jwidget.utils;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

import androidx.annotation.ColorRes;

import com.blankj.utilcode.util.ColorUtils;
import com.blankj.utilcode.util.SizeUtils;

public class UtDrawable {

    /**
     * 获取ListItem的圆角背景
     */
    public static GradientDrawable getListItemDrawable() {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(Color.WHITE);
        gradientDrawable.setCornerRadius(SizeUtils.dp2px(7));
        gradientDrawable.setAlpha(255 * 3 / 16);
        return gradientDrawable;
    }

    /**
     * 获取圆角背景
     *
     * @param color    颜色(Res)
     * @param radiusDp 四个角弧度(dp)
     */
    public static GradientDrawable getDrawable(@ColorRes int color, float radiusDp) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(ColorUtils.getColor(color));
        gradientDrawable.setCornerRadius(SizeUtils.dp2px(radiusDp));
        return gradientDrawable;
    }

    /**
     * 获取圆角背景
     *
     * @param color 颜色(Res)：资源ID
     * @param radii 四个角弧度(px)
     */
    public static GradientDrawable getDrawable(@ColorRes int color, float[] radii) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(ColorUtils.getColor(color));
        gradientDrawable.setCornerRadii(radii);
        return gradientDrawable;
    }

    public static GradientDrawable get360GradientDrawable(int color) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(360);
        gradientDrawable.setColor(color);
        return gradientDrawable;
    }

    /**
     * 获取圆角背景
     *
     * @param color 颜色值
     * @param radii 四个角弧度(px)
     */
    public static GradientDrawable getDrawable(String color, float[] radii) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        try {
            gradientDrawable.setColor(Color.parseColor(color));
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        gradientDrawable.setCornerRadii(radii);
        return gradientDrawable;
    }

    /**
     * 获取只有边框的圆角背景
     *
     * @param strokeWidthDp 边框宽度(dp)
     * @param color         边框颜色
     * @param radiusDp      四个角弧度(dp)
     */
    public static GradientDrawable getDrawableOnlyStroke(int strokeWidthDp, int color, float radiusDp) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setStroke(SizeUtils.dp2px(strokeWidthDp), color);
        gradientDrawable.setCornerRadius(SizeUtils.dp2px(radiusDp));
        return gradientDrawable;
    }

    /**
     * 获取只有边框的圆角背景
     *
     * @param strokeWidthDp 边框宽度(dp)
     * @param color         边框颜色
     * @param radii         四个角弧度(px)
     */
    public static GradientDrawable getDrawableOnlyStroke(int strokeWidthDp, int color, float[] radii) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setStroke(SizeUtils.dp2px(strokeWidthDp), color);
        gradientDrawable.setCornerRadii(radii);
        return gradientDrawable;
    }

    /**
     * 设置渐变背景
     *
     * @param colors
     * @param radius
     * @return
     */
    public static GradientDrawable getDrawableBackground(int[] colors, float radius) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(radius);
        gradientDrawable.setColors(colors);
        gradientDrawable.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        return gradientDrawable;
    }

    /**
     * 设置背景渐变、方向
     * @param colors
     * @param radiusDp
     * @param orientation
     * @return
     */
    public static GradientDrawable getDrawableBackground(int[] colors, float radiusDp, GradientDrawable.Orientation orientation) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(radiusDp);
        gradientDrawable.setColors(colors);
        gradientDrawable.setOrientation(orientation);
        gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        return gradientDrawable;
    }


}
