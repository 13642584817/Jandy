package com.jandy.jwidget.imageView;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.Nullable;

public class ButtonImageView extends androidx.appcompat.widget.AppCompatImageView {

    private static final float ALPHA_1_0 = 1.0f;
    private static final float ALPHA_0_8 = 0.8f;
    private static final float SCALE_1_0 = 1.0f;
    private static final float SCALE_0_9_5 = 0.95f;

    private AnimatorSet mZoomAnimatorSet;
    private AnimatorSet mResetAnimatorSet;
    private boolean mAnimStart = false;

    public ButtonImageView(Context context) {
        this(context,null);
    }

    public ButtonImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ButtonImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClickable(true);
        initAnimation();
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
        mZoomAnimatorSet.playTogether(zoomAlpha, zoomScaleX, zoomScaleY);
        mResetAnimatorSet.playTogether(resetAlpha, resetScaleX, resetScaleY);


    }
}
