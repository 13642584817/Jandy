package com.jandy.jwidget.utils;

import static com.billy.android.swipe.SmartSwipeBack.activityBack;
import static com.billy.android.swipe.SwipeConsumer.DIRECTION_LEFT;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;

import com.billy.android.swipe.SmartSwipe;
import com.billy.android.swipe.SmartSwipeBack;
import com.billy.android.swipe.SmartSwipeWrapper;
import com.billy.android.swipe.SwipeConsumer;
import com.billy.android.swipe.consumer.BezierBackConsumer;
import com.billy.android.swipe.consumer.TranslucentSlidingConsumer;
import com.billy.android.swipe.listener.SimpleSwipeListener;

public class SwipeUtil {


    public interface SwipeCallback {
        /**
         * 全部滑出要不要退出
         */
        default boolean swipeFinishEnable() {
            return true;
        }

        void onSwipeRelease(SmartSwipeWrapper wrapper, SwipeConsumer consumer, int direction1, float progress);
    }

    public interface DialogSwipeListener {
        /**
         * 全部滑出要不要退出
         */
        default boolean swipeDismissEnable() {
            return true;
        }

        void onSwipeRelease(Dialog dialog, SmartSwipeWrapper wrapper, SwipeConsumer consumer, int direction1);
    }

    /**
     * Activity初始化
     * 所有页面都使用侧滑返回
     */
    public static void init(Application application) {
        init(application, activity -> true);
    }

    /**
     * 初始化
     *
     * @param filter 过滤不使用侧滑返回的页面
     */
    public static void init(Application application, SmartSwipeBack.ActivitySwipeBackFilter filter) {
        //侧滑
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //use bezier back before LOLLIPOP
            activityBezierBack(application, filter);
        } else {
            //add relative moving slide back
            activitySlidingBack(application, filter);
        }
    }


    ////////////////////////////////////////
    //
    //  swipe back with BezierBackConsumer
    //
    ////////////////////////////////////////

    public static void activityBezierBack(Application application, SmartSwipeBack.ActivitySwipeBackFilter filter) {
        final int edgeSize = SmartSwipe.dp2px(20, application);
        activityBezierBack(application, filter, edgeSize);
    }

    public static void activityBezierBack(Application application, SmartSwipeBack.ActivitySwipeBackFilter filter, int edgeSize) {
        final int thickness = SmartSwipe.dp2px(30, application);
        final int size = SmartSwipe.dp2px(200, application);
        final int direction = DIRECTION_LEFT;
        activityBezierBack(application, filter, edgeSize, size, thickness, Color.BLACK, Color.WHITE, direction);
    }

    public static void activityBezierBack(Application application, SmartSwipeBack.ActivitySwipeBackFilter filter
            , final int edgeSize, final int size, final int thickness, final int color, final int arrowColor, final int direction) {
        activityBack(application, activity -> new BezierBackConsumer()
                .setColor(color)
                .setArrowColor(arrowColor)
                .setSize(size)
                .setOpenDistance(thickness)
                .addListener(new SimpleSwipeListener() {
                    @Override
                    public void onSwipeRelease(SmartSwipeWrapper wrapper, SwipeConsumer consumer, int direction1, float progress, float xVelocity, float yVelocity) {
                        if (progress >= 1) {
                            if (activity instanceof SwipeCallback) {
                                SwipeCallback callback = (SwipeCallback) activity;
                                //是否直接退出,是的就直接退出
                                if (callback.swipeFinishEnable()) {
                                    activity.finish();
                                }
                                callback.onSwipeRelease(wrapper, consumer, direction1, progress);
                            } else {
                                activity.finish();
                            }
                        }
                    }
                })
                .setEdgeSize(edgeSize)
                .enableDirection(direction), filter);
    }


    ////////////////////////////////////////////
    //
    //  swipe back with ActivitySlidingBackConsumer
    //
    ////////////////////////////////////////////

    public static void activitySlidingBack(Application application, SmartSwipeBack.ActivitySwipeBackFilter filter) {
        final float factor = 0.5f;
        // with default scrimColor: transparent
        activitySlidingBack(application, filter, factor);
    }

    public static void activitySlidingBack(Application application, SmartSwipeBack.ActivitySwipeBackFilter filter, float factor) {
        //default edge size
        final int edgeSize = SmartSwipe.dp2px(20, application);
        final int shadowColor = 0x80000000;
        final int shadowSize = SmartSwipe.dp2px(10, application);
        final int direction = DIRECTION_LEFT;
//        activitySlidingBack(application, filter, edgeSize, Color.TRANSPARENT, shadowColor, shadowSize, factor, direction);
    }

//    public static void activitySlidingBack(Application application, SmartSwipeBack.ActivitySwipeBackFilter filter
//            , final int edgeSize, final int scrimColor, final int shadowColor, final int shadowSize
//            , final float factor, final int direction) {
//        activityBack(application, activity -> new ActivitySlidingBackConsumer(activity)
//                .setRelativeMoveFactor(factor)
//                .setScrimColor(scrimColor)
//                .setShadowColor(shadowColor)
//                .setShadowSize(shadowSize)
//                .setEdgeSize(edgeSize)
//                .enableDirection(direction)
//                .addListener(new SimpleSwipeListener() {
//                    @Override
//                    public void onSwipeOpened(SmartSwipeWrapper wrapper, SwipeConsumer consumer, int direction1) {
//                        if (activity instanceof SwipeCallback) {
//                            SwipeCallback callback = (SwipeCallback) activity;
//                            //是否直接退出,是的就直接退出
//                            if (callback.swipeFinishEnable()) {
//                                activity.finish();
//                                activity.overridePendingTransition(R.anim.anim_none, R.anim.anim_none);
//                            }
//                            callback.onSwipeRelease(wrapper, consumer, direction1, 1);
//                        } else if (activity != null) {
//                            activity.finish();
//                            activity.overridePendingTransition(R.anim.anim_none, R.anim.anim_none);
//                        }
//                    }
//                }), filter);
//    }
//

    /**
     * Dialog的初始化
     */
    public static void init(Dialog dialog, View view, DialogSwipeListener listener) {
        if (view == null) {
            return;
        }
        Context context = view.getContext();

        SmartSwipeWrapper wrap = SmartSwipe.wrap(view);

        //侧滑
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //use bezier back before LOLLIPOP
            final int edgeSize = SmartSwipe.dp2px(20, context);
            final int thickness = SmartSwipe.dp2px(30, context);
            final int size = SmartSwipe.dp2px(200, context);
            wrap.addConsumer(new BezierBackConsumer()
                    .setColor(Color.BLACK)
                    .setArrowColor(Color.WHITE)
                    .setSize(size)
                    .setOpenDistance(thickness)
                    .setEdgeSize(edgeSize)
                    .enableDirection(DIRECTION_LEFT))
                    .addListener(
                            new SimpleSwipeListener() {
                                @Override
                                public void onSwipeRelease(SmartSwipeWrapper wrapper, SwipeConsumer consumer,
                                                           int direction, float progress, float xVelocity, float yVelocity) {
                                    if (progress >= 1) {
                                        if (dialog != null) {
                                            if (listener == null) {
                                                dialog.dismiss();
                                            }else {
                                                if (listener.swipeDismissEnable()) {
                                                    dialog.dismiss();
                                                }
                                                listener.onSwipeRelease(dialog, wrapper, consumer, direction);
                                            }
                                        }
                                    }
                                }
                            });

        } else {
            //add relative moving slide back
            final float factor = 0.5f;
            final int edgeSize = SmartSwipe.dp2px(20, context);
            final int shadowColor = 0x80000000;
            final int shadowSize = SmartSwipe.dp2px(10, context);
            wrap.addConsumer(new TranslucentSlidingConsumer()
                    .setRelativeMoveFactor(factor)
                    .setScrimColor(Color.TRANSPARENT)
                    .setShadowColor(shadowColor)
                    .setShadowSize(shadowSize)
                    .setEdgeSize(edgeSize)
                    .enableDirection(DIRECTION_LEFT)
                    .addListener(
                            new SimpleSwipeListener() {
                                @Override
                                public void onSwipeOpened(SmartSwipeWrapper wrapper, SwipeConsumer consumer, int direction) {
                                    if (dialog != null) {
                                        if (listener == null) {
                                            dialog.dismiss();
                                        }else {
                                            if (listener.swipeDismissEnable()) {
                                                dialog.dismiss();
                                            }
                                            listener.onSwipeRelease(dialog, wrapper, consumer, direction);
                                        }
                                    }
                                }
                            }
                    ));
        }
    }

    /**
     * 能否右滑退出
     *
     * @param activity activity
     * @param enable   能否右滑退出
     */
    public static View enableLeft(Activity activity, boolean enable) {
        return SmartSwipe.wrap(activity).enableDirection(SwipeConsumer.DIRECTION_LEFT, enable);
    }

    /**
     * 能否右滑退出
     *
     * @param view   view
     * @param enable 能否右滑退出
     */
    public static void enableLeft(View view, boolean enable) {
        SmartSwipe.wrap(view).enableDirection(SwipeConsumer.DIRECTION_LEFT, enable);
    }




}
