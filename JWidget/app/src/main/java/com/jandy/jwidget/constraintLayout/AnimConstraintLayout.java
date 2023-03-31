/*
 * @Author: tangbing
 *
 *     Copyright (C), 2015 - 2030, ShenZhen Benew Technology Co.,Ltd.
 *
 * @Date: 2021/12/23 下午8:16
 */

package com.jandy.jwidget.constraintLayout;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.jandy.jwidget.R;


public class AnimConstraintLayout extends ConstraintLayout {

    private static final float ALPHA_1_0 = 1.0f;
    private static final float ALPHA_0_8 = 0.8f;
    private static final float SCALE_1_0 = 1.0f;
    private static final float SCALE_0_9_5 = 0.95f;

    private AnimatorSet mZoomAnimatorSet;
    private AnimatorSet mResetAnimatorSet;
    private boolean mAnimStart = false;
    private boolean mUseAlpha, mUseZoom;


    public AnimConstraintLayout(Context context) {
        this(context, null);
    }

    public AnimConstraintLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AnimConstraintLayout);
        mUseAlpha = typedArray.getBoolean(R.styleable.AnimConstraintLayout_cl_alpha_enable, true);
        mUseZoom = typedArray.getBoolean(R.styleable.AnimConstraintLayout_cl_zoom_enable, true);
        typedArray.recycle();
        setClickable(true);
        initAnimation();
    }


    private void initAnimation() {

        ObjectAnimator zoomAlpha = ObjectAnimator.ofFloat(this, "alpha", ALPHA_1_0, ALPHA_0_8);
        ObjectAnimator zoomScaleX = ObjectAnimator.ofFloat(this, "scaleX", SCALE_1_0, SCALE_0_9_5);
        ObjectAnimator zoomScaleY = ObjectAnimator.ofFloat(this, "scaleY", SCALE_1_0, SCALE_0_9_5);
        mZoomAnimatorSet = new AnimatorSet();
        mZoomAnimatorSet.setDuration(100);
        mZoomAnimatorSet.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator resetAlpha = ObjectAnimator.ofFloat(this, "alpha", ALPHA_0_8, ALPHA_1_0);
        ObjectAnimator resetScaleX = ObjectAnimator.ofFloat(this, "scaleX", SCALE_0_9_5, SCALE_1_0);
        ObjectAnimator resetScaleY = ObjectAnimator.ofFloat(this, "scaleY", SCALE_0_9_5, SCALE_1_0);
        mResetAnimatorSet = new AnimatorSet();
        mResetAnimatorSet.setDuration(100);
        mResetAnimatorSet.setInterpolator(new AccelerateDecelerateInterpolator());

        if (mUseZoom && mUseAlpha) {
            mZoomAnimatorSet.playTogether(zoomAlpha, zoomScaleX, zoomScaleY);
            mResetAnimatorSet.playTogether(resetAlpha, resetScaleX, resetScaleY);
        } else if (mUseAlpha) {
            mZoomAnimatorSet.play(zoomAlpha);
            mResetAnimatorSet.play(resetAlpha);
        } else if (mUseZoom) {
            mZoomAnimatorSet.playTogether(zoomScaleX, zoomScaleY);
            mResetAnimatorSet.playTogether(resetScaleX, resetScaleY);
        }


    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startZoomAnim();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                startResetAnim();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    public void startResetAnim() {
        if (mZoomAnimatorSet == null || mResetAnimatorSet == null) return;
        if (!mAnimStart) return;
        mAnimStart = false;
        if (mZoomAnimatorSet.isRunning()) {
            mZoomAnimatorSet.cancel();
        }
        if (!mResetAnimatorSet.isRunning()) {
            mResetAnimatorSet.start();
        }
    }

    public void startZoomAnim() {
        if (mZoomAnimatorSet == null || mResetAnimatorSet == null) return;
        mAnimStart = true;
        if (mResetAnimatorSet.isRunning()) {
            mResetAnimatorSet.cancel();
        }
        if (!mZoomAnimatorSet.isRunning()) {
            mZoomAnimatorSet.start();
        }
    }


}
