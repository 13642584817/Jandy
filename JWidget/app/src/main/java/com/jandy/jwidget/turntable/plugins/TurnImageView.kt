package com.jandy.jwidget.turntable.plugins

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.widget.AppCompatImageView
import java.lang.Math.abs

class TurnImageView : AppCompatImageView {

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attr: AttributeSet?) : super(context, attr) {
    }

    constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attr,
        defStyleAttr
    )

    private val ALPHA_0_9 = 0.8f
    private val ALPHA_0_8 = 0.5f
    private val SCALE_1_0 = 1.0f
    private val SCALE_0_9_5 = 0.95f

    private var mZoomAnimatorSet: AnimatorSet? = null
    private var mResetAnimatorSet: AnimatorSet? = null
    private var mAnimStart = false
    private var touchClick = 20;//点击触发阀门

    var location = IntArray(2)

    init {
        isClickable = true
        alpha = ALPHA_0_9
        initAnimation()
    }

    private var isOnclick = false
    private var startY = 0
    private var scroll = 0
    var listener: OnTouchEventListener? = null


    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (!hasWindowFocus) return
        getLocationInWindow(location)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val dispatch = super.dispatchTouchEvent(ev)

        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                isOnclick = true
                startY = ev.rawY.toInt()
                scroll = startY
                startZoomAnim()
            }
            MotionEvent.ACTION_MOVE -> {
                if (abs(ev.rawY - startY) >= touchClick) {
                    isOnclick = false
                }
                listener?.onScrollY(ev.rawY - scroll)  //下滑数值增加
                scroll = ev.rawY.toInt()
            }
            MotionEvent.ACTION_UP -> {
                startResetAnim()
                if (isOnclick)
                    listener?.onClick()
            }
        }

        return dispatch
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
            ALPHA_0_9,
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
            ALPHA_0_9
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

    interface OnTouchEventListener {
        fun onClick()

        fun onScrollY(dy: Float)
    }

}