package com.jandy.jwidget.batteryview;

import android.content.Context;
import android.graphics.*
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.res.ResourcesCompat;
import com.jandy.jwidget.R
import com.jandy.jwidget.utils.UtDimen


class BatteryView : View {

    constructor(context: Context) : this(context, null) {
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
    }

    private lateinit var mStrokePaint: Paint
    private lateinit var mFillPaint: Paint
    private val mRectF = RectF()
    private val mFillRectF = RectF()
    private val mDotRectF = RectF()
    private lateinit var mDotPaint: Paint
    private lateinit var mChargePaint: Paint
    private lateinit var mTvPaint: Paint //显示电量多少
    private var mRetractSize = 0f //圆角边框缩进距离
    private var mCurPercent = 0f

    private var mLowBatteryColor = 0
    private var mChargeColor = 0
    private var mFullColor = 0
    private var mFillColor = 0
    private var mIsCharge = false //是否充电状态
    private var mChargeBitmap: Bitmap? = null
    private var mChargeWidth = 0
    private var mChargeHeight = 0


    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BatteryView)
        val borderColor = typedArray.getColor(
                R.styleable.BatteryView_battery_border_color,
                Color.parseColor("#ffffff")
        )
        val borderWidth = typedArray.getDimension(
            R.styleable.BatteryView_battery_border_width,
            UtDimen.dp2px(getContext(), 1f).toFloat()
        )
        mFillColor = typedArray.getColor(R.styleable.BatteryView_battery_inner_color, Color.WHITE)
        mLowBatteryColor = typedArray.getColor(
            R.styleable.BatteryView_battery_low_color,
            Color.parseColor("#ff411b")
        )
        mFullColor = typedArray.getColor(R.styleable.BatteryView_battery_full_color, Color.WHITE)
        mChargeColor = typedArray.getColor(
            R.styleable.BatteryView_battery_charge_color,
            Color.parseColor("#23b83c")
        )
        mCurPercent = typedArray.getFraction(R.styleable.BatteryView_battery_percent, 1, 1, 0f)
        typedArray.recycle()
        mStrokePaint = Paint()
        mStrokePaint.isAntiAlias = true
        mStrokePaint.style = Paint.Style.STROKE
        mStrokePaint.color = borderColor
        mStrokePaint.strokeWidth = borderWidth

        mFillPaint = Paint()
        mFillPaint.style = Paint.Style.FILL
        mFillPaint.color = mFillColor
        mFillPaint.isAntiAlias = true //处理图像抖动

        mDotPaint = Paint()
        mDotPaint.isAntiAlias = true
        mDotPaint.style = Paint.Style.FILL
        mDotPaint.color = borderColor

        mChargePaint = Paint()
        mChargePaint.isAntiAlias = true
//        mChargePaint.isDither = true

        mTvPaint = Paint()
//        mTvPaint.strokeWidth = 3f
        mTvPaint.style = Paint.Style.FILL
        mTvPaint.isAntiAlias = true
//        mTvPaint.isDither = true     //处理图像抖动
        mTvPaint.textSize = UtDimen.sp2px(context, 8f).toFloat()
        mTvPaint.color = Color.WHITE
        mTvPaint.isFakeBoldText = true  //字体变粗
        mTvPaint.textScaleX = 1.1f
//        mTvPaint.textSkewX
//        mTvPaint.letterSpacing=2f
        mTvPaint.textAlign = Paint.Align.LEFT
        mTvPaint.isSubpixelText = true

        (ResourcesCompat.getDrawable(
            resources,
            R.drawable.icon_charging,
            null
        ) as BitmapDrawable).apply {
            mChargeBitmap = bitmap
            mChargeWidth = bitmap.width
            mChargeHeight = bitmap.height
        }

        mRetractSize = borderWidth * 0.8f;
    }


    private fun setPercent(percent: Float) {
        if (percent < 0) {
            mCurPercent = 0f
        } else if (percent > 1) {
            mCurPercent = 1f
        } else {
            mCurPercent = percent
        }
        postInvalidate()
    }

    /**
     * 电量消耗或充电，更新电量信息（断电和上电不调用）
     *
     * @param percent
     */
    fun updatePowerPercent(percent: Float) {
        //充电，消耗改变
        setPercent(percent)
    }


    //充电状态发生改变
    fun isCharging(isCharge: Boolean){
        mIsCharge = isCharge
        postInvalidate()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
//        mChargeBitmap?.recycle()
//        mChargeBitmap = null
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val leftWidth = width / 3
        val dotDiameter = height * 0.2f

        showBatteryBorad(canvas, leftWidth, dotDiameter)

        showBatterySize(canvas, leftWidth, dotDiameter)

        showBatteryIcon(canvas)

        //显示电量大小
        showBatteryText(canvas, leftWidth, dotDiameter)

        showBatteryHead(canvas, dotDiameter)
    }

    //显示电量边框
    private fun showBatteryBorad(canvas: Canvas, leftWidth: Int, dotDiameter: Float) {
        val batteryLeft = mRetractSize + leftWidth
        mRectF.setEmpty()
        mRectF.set(batteryLeft, mRetractSize, width - dotDiameter, height - mRetractSize)
        canvas.drawRoundRect(mRectF, 6f, 6f, mStrokePaint)
    }

    //显示电量填充物
    private fun showBatterySize(canvas: Canvas, leftWidth: Int, dotDiameter: Float) {
        if (mIsCharge) return
        val fillLeft = leftWidth + mRetractSize * 4
        val percent = mCurPercent.coerceAtLeast(0f)
        val fillWidth = ((width - mRetractSize * 3 - dotDiameter - fillLeft) * percent)
        val fillRight = fillWidth + fillLeft
        // mFillPaint.color = mChargeColor
        if (percent <= 0.2f) {
            mFillPaint.color = mLowBatteryColor
        } else if (percent >= 1.0f) {
            mFillPaint.color = mFullColor
        } else {
            mFillPaint.color = mFillColor
        }
        mFillRectF.setEmpty()
        mFillRectF.set(fillLeft, mRetractSize * 4, fillRight, height - mRetractSize * 4)
        canvas.drawRoundRect(mFillRectF, 4f, 4f, mFillPaint)
    }

    //显示充电图标
    private fun showBatteryIcon(canvas: Canvas) {
        if (!mIsCharge || mChargeBitmap == null) return
        canvas.drawBitmap(
            mChargeBitmap!!,
            mRetractSize,
            height * 0.5f - mChargeHeight * 0.5f,
            mChargePaint
        )
    }

    //显示电量大小（文字）
    private fun showBatteryText(canvas: Canvas, leftWidth: Int, dotDiameter: Float) {
        val fillLeft = leftWidth + mRetractSize * 4
        val fillWidth = (width - mRetractSize * 3 - dotDiameter - fillLeft)
        val fillRight = fillWidth + fillLeft
        val curCharging = (mCurPercent * 100).toInt().toString()
        var textBound = Rect()
        mTvPaint.getTextBounds(curCharging, 0, curCharging.length, textBound)
        val baseLineX = (fillRight + fillLeft) / 2f - (textBound.right + textBound.left) / 2f
        val baseLineY = height / 2f - (textBound.top + textBound.bottom) / 2f - 1
        canvas.drawText(
            curCharging,
            baseLineX,
            baseLineY,
            mTvPaint
        )
    }

    //显示电池头部
    private fun showBatteryHead(canvas: Canvas, dotDiameter: Float) {
        mDotRectF.setEmpty()
        mDotRectF.set(
            width - dotDiameter,
            (height * 0.5f) - dotDiameter,
            width.toFloat(),
            height * 0.5f + dotDiameter
        )
        canvas.drawArc(mDotRectF, -90f, 180f, true, mDotPaint)
    }
}
