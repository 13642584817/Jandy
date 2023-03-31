package com.jandy.jwidget.HalfRoundTextView

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.blankj.utilcode.util.ScreenUtils
import com.jandy.jwidget.R
import com.jandy.jwidget.utils.UtDimen

class HalfRoundTextView : View {
	constructor(context : Context) : this(context, null) {
	}

	constructor(context : Context, attrs : AttributeSet?) : this(context, attrs, 0) {
	}

	private lateinit var mTextPaint : Paint  //文字
	private lateinit var mFillPaint : Paint
	private var textBound = Rect()
	private var mContent : String? = null //内容
	private var mRoundRect = RectF()
	private var mStartAngle = 0f  //画弧线起点位置
	private var mSweepAngle = 0f  //弧线经过角度
	private var mWordAngle = 1f  //文字占用的角度
	private var mWidth = 0
	private var mHeight = 0
	private var isRoundCenterTop = false //圆的中心是否在上，否则是在下方
	private var mSetTextColor : Int = 0
	private var screenWidth : Int = 0
	private var screenHeight : Int = 0
	private var mTextSize : Float = 0f

	constructor(context : Context, attrs : AttributeSet?, defStyleAttr : Int) : super(
			context,
			attrs,
			defStyleAttr
																					 ) {
		val typedArray = context.obtainStyledAttributes(attrs, R.styleable.HalfRoundTextView)

		mContent = typedArray.getString(R.styleable.HalfRoundTextView_hrtv_text)
			?: ""
		mTextSize = typedArray.getDimension(
				R.styleable.HalfRoundTextView_hrtv_textSize,
				UtDimen.sp2px(context, 8f).toFloat()
										   )
		val isTextBold = typedArray.getBoolean(R.styleable.HalfRoundTextView_hrtv_textBold, false)
		val mTextColor = typedArray.getColor(
				R.styleable.HalfRoundTextView_hrtv_textColor,
				resources.getColor(R.color.white))

		if (screenWidth == 0)
			screenWidth = ScreenUtils.getScreenWidth()
		if (screenHeight == 0)
			screenHeight = ScreenUtils.getScreenHeight()

		mTextPaint = Paint().apply {
			isAntiAlias = true
			color = if (mSetTextColor == 0) mTextColor else mSetTextColor
			style = Paint.Style.FILL
//            strokeCap=Paint.Cap.ROUND
			textSize = mTextSize
			isFakeBoldText = isTextBold
			isSubpixelText = true
		}

		mFillPaint = Paint().apply {
			isAntiAlias = true
			color = resources.getColor(R.color.white)
			style = Paint.Style.FILL
		}
	}

	constructor(context : Context, isRoundCenterTop : Boolean) : this(context, null) {
		this.isRoundCenterTop = isRoundCenterTop
	}

	override fun onMeasure(widthMeasureSpec : Int, heightMeasureSpec : Int) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)

		mHeight = measuredHeight
		mWidth = measuredWidth
		val roundRadius = Math.min(screenWidth, screenHeight)/2
		var roundTop = 0f
		var roundBottom = 0f
		val mStrokeWidth = mTextSize/3*2
		if (isRoundCenterTop) {
			roundBottom = mHeight-mStrokeWidth
			roundTop = roundBottom-2f*roundRadius
		} else {
			roundTop = mStrokeWidth
			roundBottom = roundTop+2f*roundRadius
		}

		mRoundRect = RectF(
				mWidth/2f-roundRadius,
				roundTop,
				mWidth/2f+roundRadius,
				roundBottom
						  )
		val AB = mWidth/2f
		val aSin = Math.asin(AB/roundRadius.toDouble())
		val LAOB = Math.toDegrees(aSin)

		mStartAngle = 90f-LAOB.toFloat()

		if (!isRoundCenterTop)
			mStartAngle += 180

		mSweepAngle = 2*LAOB.toFloat()

	}

	override fun onDraw(canvas : Canvas) {
		super.onDraw(canvas)

//		canvas.drawRect(Rect(0, 0, width, height), mFillPaint)

		showContent(canvas)
	}

	private fun showContent(canvas : Canvas) {
		val path = Path()
		var mPaddingAngle = 0f
//		NLogger.d("jandt halfrountext textcolor ${mTextPaint.color}")
		if (!mContent.isNullOrEmpty())
			mContent = getRightText(mContent!!)

		mTextPaint.getTextBounds(mContent, 0, mContent!!.length, textBound)

		mWordAngle =
			textBound.width()/(2f*Math.PI.toFloat()*(mRoundRect.width()/2f))*360

		if (mSweepAngle>mWordAngle) {
			mPaddingAngle = (mSweepAngle-mWordAngle)/2
		}

		path.addArc(mRoundRect,
					if (!isRoundCenterTop) mStartAngle+mPaddingAngle else mStartAngle+mSweepAngle-mPaddingAngle,
					if (!isRoundCenterTop) mSweepAngle else -mSweepAngle)

		if (!mContent.isNullOrEmpty())
			mContent = getRightText(mContent!!)
//		NLogger.d("jandy mContent = $mContent width = $width textBoundwidth = ${textBound.width()}")
		canvas.drawTextOnPath(
				mContent!!,
				path,
				0f,
				textBound.height()/2f,
				mTextPaint)
	}

	fun setText(text : String) {
		mContent = text
		val maxLength = 12
		if (text.length>=maxLength) {
			var newText = text.substring(0, maxLength-2)
			mContent = "$newText.."
		}
		mTextPaint.getTextBounds(mContent, 0, mContent!!.length, textBound)
	}

	private fun getRightText(text : String, isFirst : Boolean = true) : String {
		if (text.isNullOrEmpty()) return ""
		var checkText = text
		if (!isFirst) {
			checkText = "$text.."
		}
		mTextPaint.getTextBounds(checkText, 0, checkText.length, textBound)
		if (isFirst) {
			if (textBound.width()<width)
				return text
			val newText = text.substring(0, text.length-1)
			return getRightText(newText, false)
		}
		if (textBound.width()<width)
			return checkText
		val newText = text.substring(0, text.length-1)
		return getRightText(newText, false)
	}

	fun setTextSize(size : Float) {
		mTextSize = size
		mTextPaint?.textSize = size
	}

	fun setTextColor(color : Int) {
//		NLogger.d("jandt halfrountext setTextColor ${color} $mTextPaint")
		mSetTextColor = color
		mTextPaint?.color = color
	}

	fun setTextBold(isBold : Boolean) {
		mTextPaint?.isFakeBoldText = isBold
	}

	fun draw() {
		postInvalidate()
	}

	fun getText() : String {
		return mContent
			?: ""
	}
}