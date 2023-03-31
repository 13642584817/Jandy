package com.jandy.jwidget.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Base64;

import androidx.annotation.ColorRes;

import com.blankj.utilcode.util.ColorUtils;
import com.blankj.utilcode.util.SizeUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class DrawableUtil {


    /**
     * 获取圆角背景
     *
     * @param colorRes 颜色(Res)
     * @param radiusDp 四个角弧度(dp)
     */
    public static GradientDrawable getDrawable(@ColorRes int colorRes, float radiusDp) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(ColorUtils.getColor(colorRes));
        gradientDrawable.setCornerRadius(SizeUtils.dp2px(radiusDp));
        return gradientDrawable;
    }

    /**
     * 获取圆角背景
     *
     * @param color    颜色(Res)
     * @param radiusDp 四个角弧度(dp)
     */
    public static GradientDrawable getDrawable(String color, float radiusDp) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        try {
            gradientDrawable.setColor(Color.parseColor(color));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        gradientDrawable.setCornerRadius(SizeUtils.dp2px(radiusDp));
        return gradientDrawable;
    }

    /**
     * 获取圆角背景
     *
     * @param colorRes 颜色(Res)：资源ID
     * @param radii    四个角弧度(px)
     */
    public static GradientDrawable getDrawable(@ColorRes int colorRes, float[] radii) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(ColorUtils.getColor(colorRes));
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
        } catch (IllegalArgumentException e) {
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
     * @param radiusDp      四个角弧度(dp)
     * @param bgColor       填充颜色
     */
    public static GradientDrawable getDrawableOnlyStroke(int strokeWidthDp, int color, float radiusDp, int bgColor) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setStroke(SizeUtils.dp2px(strokeWidthDp), color);
        gradientDrawable.setCornerRadius(SizeUtils.dp2px(radiusDp));
        gradientDrawable.setColor(bgColor);
        return gradientDrawable;
    }

    /**
     * 获取只有边框的圆角背景
     *
     * @param strokeWidthDp 边框宽度(dp)
     * @param color         边框颜色
     * @param radii         四个角弧度(px)
     * @param bgColor       填充颜色
     */
    public static GradientDrawable getDrawableOnlyStroke(int strokeWidthDp, int color, float[] radii, int bgColor) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setStroke(SizeUtils.dp2px(strokeWidthDp), color);
        gradientDrawable.setCornerRadii(radii);
        gradientDrawable.setColor(bgColor);
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
     *
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

    /**
     * 设置背景渐变、方向
     *
     * @param colors
     * @param radii
     * @param orientation
     * @return
     */
    public static GradientDrawable getDrawableBackground(int[] colors, float[] radii, GradientDrawable.Orientation orientation) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadii(radii);
        gradientDrawable.setColors(colors);
        gradientDrawable.setOrientation(orientation);
        gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        return gradientDrawable;
    }

    //base64的图片转drawable
    public synchronized static Drawable byteToDrawable(String icon) {
        byte[] img = Base64.decode(icon.getBytes(), Base64.DEFAULT);
        Bitmap bitmap;
        if (img != null) {
            bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
            @SuppressWarnings("deprecation")
            Drawable drawable = new BitmapDrawable(bitmap);
            return drawable;
        }
        return null;
    }

    //drawable转base64图片
    public synchronized static String drawableToByte(Drawable drawable) {
        if (drawable != null) {
            Bitmap bitmap = Bitmap
                    .createBitmap(
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(),
                            Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            int size = bitmap.getWidth() * bitmap.getHeight() * 4;

            // 创建一个字节数组输出流,流的大小为size
            ByteArrayOutputStream baos = new ByteArrayOutputStream(size);
            // 设置位图的压缩格式，质量为100%，并放入字节数组输出流中
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            // 将字节数组输出流转化为字节数组byte[]
            byte[] imagedata = baos.toByteArray();
            String icon = Base64.encodeToString(imagedata, Base64.DEFAULT);
            return icon;
        }
        return "";
    }

    public static void saveImage(String path, Bitmap bitmap) {
        FileOutputStream out = null;
        try {
            File f = new File(path);
            out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.WEBP, 100, out);
        } catch (Exception e) {

        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (Exception e) {
            }
        }
    }

}
