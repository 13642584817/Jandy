package com.jandy.jwidget.turntable.plugins

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.AccelerateDecelerateInterpolator


class ButtonTextView : androidx.appcompat.widget.AppCompatTextView {

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attr: AttributeSet?) : super(context, attr) {
    }

    constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attr,
        defStyleAttr
    )

    private val ALPHA_1_0 = 1.0f
    private val ALPHA_0_8 = 0.8f
    private val SCALE_1_0 = 1.0f
    private val SCALE_0_9_5 = 0.95f

    private var mZoomAnimatorSet: AnimatorSet? = null
    private var mResetAnimatorSet: AnimatorSet? = null
    private var mAnimStart = false

    init {
        isClickable = true
        initAnimation()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> startZoomAnim()
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> startResetAnim()
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun startResetAnim() {
        if (mZoomAnimatorSet == null || mResetAnimatorSet == null) return
        if (!mAnimStart) return
        mAnimStart = false
        if (mZoomAnimatorSet!!.isRunning) {
            mZoomAnimatorSet!!.cancel()
        }
        if (!mResetAnimatorSet!!.isRunning) {
            mResetAnimatorSet!!.start()
        }
    }

    private fun startZoomAnim() {
        if (mZoomAnimatorSet == null || mResetAnimatorSet == null) return
        mAnimStart = true
        if (mResetAnimatorSet!!.isRunning) {
            mResetAnimatorSet!!.cancel()
        }
        if (!mZoomAnimatorSet!!.isRunning) {
            mZoomAnimatorSet!!.start()
        }
    }


    private fun initAnimation() {
        val zoomAlpha = ObjectAnimator.ofFloat(
            this,
            "alpha",
            ALPHA_1_0,
            ALPHA_0_8
        )
        val zoomScaleX = ObjectAnimator.ofFloat(
            this,
            "scaleX",
            SCALE_1_0,
            SCALE_0_9_5
        )
        val zoomScaleY = ObjectAnimator.ofFloat(
            this,
            "scaleY",
            SCALE_1_0,
            SCALE_0_9_5
        )
        mZoomAnimatorSet = AnimatorSet()
        mZoomAnimatorSet?.duration = 100
        mZoomAnimatorSet?.interpolator = AccelerateDecelerateInterpolator()
        val resetAlpha = ObjectAnimator.ofFloat(
            this,
            "alpha",
            ALPHA_0_8,
            ALPHA_1_0
        )
        val resetScaleX = ObjectAnimator.ofFloat(
            this,
            "scaleX",
            SCALE_0_9_5,
            SCALE_1_0
        )
        val resetScaleY = ObjectAnimator.ofFloat(
            this,
            "scaleY",
            SCALE_0_9_5,
            SCALE_1_0
        )
        mResetAnimatorSet = AnimatorSet()
        mResetAnimatorSet?.duration = 100
        mResetAnimatorSet?.interpolator = AccelerateDecelerateInterpolator()
        mZoomAnimatorSet?.playTogether(zoomAlpha, zoomScaleX, zoomScaleY)
        mResetAnimatorSet?.playTogether(resetAlpha, resetScaleX, resetScaleY)
    }

}