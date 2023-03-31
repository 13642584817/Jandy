package com.jandy.jwidget.ImageLoader;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;

public class ImageLoader {
    private static final String TAG = ImageLoader.class.getSimpleName();


    public static void urlLoader(Context context, String url, ImageView view) {
        urlLoader(context, url, view, 0);
    }

    public static void urlLoader(Context context, String url, Priority priority, ImageView view) {
        urlLoader(context, url, view, priority, 0);
    }

    /**
     * 加载网址图片
     *
     * @param context
     * @param url
     * @param view
     */
    public static void urlLoader(Context context, String url, ImageView view, int placeholder) {
//        if (view == null) {
//            NLogger.e(TAG, "加载失败，无视图可供显示");
//            return;
//        }
//        if (TextUtils.isEmpty(url)) {
//            NLogger.w(TAG, "无法加载，地址为空");
//            return;
//        }
        Glide.with(context).load(url).placeholder(placeholder).priority(Priority.HIGH).into(view);
    }


    /**
     * 加载网址图片
     *
     * @param context
     * @param url
     * @param view
     */
    public static void urlLoader(Context context, String url, ImageView view, int placeholder, int error) {
//        if (view == null) {
//            NLogger.e(TAG, "加载失败，无视图可供显示");
//            return;
//        }
//        if (TextUtils.isEmpty(url)) {
//            NLogger.w(TAG, "无法加载，地址为空");
//            return;
//        }
        Glide.with(context).load(url).placeholder(placeholder).error(error).into(view);
    }

    /**
     * 加载网址图片
     *
     * @param context
     * @param url
     * @param view
     */
    public static void urlLoader(Context context, String url, ImageView view, Priority priority, int placeholder) {
//        if (view == null) {
//            NLogger.e(TAG, "加载失败，无视图可供显示");
//            return;
//        }
//        if (TextUtils.isEmpty(url)) {
//            NLogger.w(TAG, "无法加载，地址为空");
//            return;
//        }
        Glide.with(context).load(url).placeholder(placeholder).priority(priority).into(view);
    }

    /**
     * 耗时操作
     */
    public static Drawable getDrawableGlide(Context context, String url) {
        try {
            return Glide.with(context).load(url).submit().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 耗时操作
     */
    public static Drawable getDrawableGlide(Context context, Priority priority, String url) {
        try {
            return Glide.with(context).load(url).priority(priority).submit().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void clearCache(Context context) {
        try {
//            Glide.get(context).clearDiskCache();
            Glide.get(context).clearMemory();
        } catch (Exception e) {
            Log.d(TAG," 清除Glide失败");
        }
    }


//    public static
}
