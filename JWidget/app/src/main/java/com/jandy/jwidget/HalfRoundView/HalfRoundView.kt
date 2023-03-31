package com.jandy.jwidget.HalfRoundView

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.blankj.utilcode.util.ScreenUtils
import com.jandy.jwidget.R
import com.jandy.jwidget.utils.UtDimen

class HalfRoundView : View {

	constructor(context : Context) : this(context, null) {
	}

	constructor(context : Context, attrs : AttributeSet?) : this(context, attrs, 0) {
	}

	private lateinit var mBgPaint : Paint //背景
	private lateinit var mScrollPaint : Paint //实际电量
	private lateinit var mTextPaint : Paint  //文字
	private lateinit var mFillPaint : Paint
	private var textBound = Rect()
	private var paddingVertical = 0f  //上下缩距
	private var mRoundRect = RectF()
	private var mScrollRoundRect = RectF()
	private var mScrollRadius = 0f  //小圆的半径
	private var mStartAngle = 0f  //画弧线起点位置
	private var mSweepAngle = 0f  //弧线经过角度
	private var mOneWordAngle = 1f  //跟两边的间距的角度
	private var mWidth = 0
	private var mHeight = 0
	private var leftText = 0
	private var rightText = 0
	private var showText = 0
	private var mTextSize = 0f
	private var isRoundCenterTop = false //圆的中心是否在上，否则是在下方
	private var needBoradText = false  //是否双边需要展示文字
	private var paddingAngle = 0f //划线粗细占用的角度
	private var mColorInts : IntArray = intArrayOf(R.color.ntt_color_ff8585, R.color.ntt_color_4dd68b)
	private var type = 0 //0 有底色，1 无底色有小圈
	private var screenWidth : Int = 0
	private var screenHeight : Int = 0
	private val mStrokeWidth = 10f

	constructor(context : Context, attrs : AttributeSet?, defStyleAttr : Int) : super(
			context,
			attrs,
			defStyleAttr
																					 ) {
		val typedArray = context.obtainStyledAttributes(attrs, R.styleable.HalfRoundView)

		paddingVertical = typedArray.getDimension(
				R.styleable.HalfRoundView_padding_top_and_bottom,
				UtDimen.dp2px(context, 1f).toFloat()
												 )
		val bgColor = typedArray.getColor(
				R.styleable.HalfRoundView_bg_color,
				resources.getColor(R.color.ntt_color_757575))
		val scrollColor =
			typedArray.getColor(R.styleable.HalfRoundView_scroll_color, Color.BLUE)

		if (mTextSize == 0f)
			mTextSize = UtDimen.sp2px(context, 8f).toFloat()

		if (screenWidth == 0)
			screenWidth = ScreenUtils.getScreenWidth()
		if (screenHeight == 0)
			screenHeight = ScreenUtils.getScreenHeight()

		mBgPaint = Paint().apply {
			isAntiAlias = true
			color = bgColor
			style = Paint.Style.STROKE
			strokeCap = Paint.Cap.ROUND  //圆角
			strokeWidth = mStrokeWidth
		}

		mScrollPaint = Paint().apply {
			isAntiAlias = true
			color = scrollColor
			style = Paint.Style.STROKE
			strokeCap = Paint.Cap.ROUND //圆角
			strokeWidth = mStrokeWidth
		}

		mTextPaint = Paint().apply {
			isAntiAlias = true
			color = Color.WHITE
			style = Paint.Style.FILL
//            strokeCap=Paint.Cap.ROUND
			textAlign = Paint.Align.LEFT
			textSize = mTextSize
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

	@SuppressLint("DrawAllocation")
	override fun onMeasure(widthMeasureSpec : Int, heightMeasureSpec : Int) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)
		// measuredHeight - 2 * paddingVertical
		mHeight = measuredHeight
		mWidth = measuredWidth
		val roundRadius = Math.min(screenWidth, screenHeight)/2
		//左右边文字占用大小
		val text = "9"
		mTextPaint.getTextBounds(text, 0, text.length, textBound)
		var roundTop = 0f
		var roundBottom = 0f

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

		mOneWordAngle =
			textBound.width()/(2f*Math.PI.toFloat()*(mRoundRect.width()/2f))*360
		paddingAngle = mStrokeWidth/2f/(2f*Math.PI.toFloat()*(mRoundRect.width()/2f))*360
//		NLogger.d("jandy halfRoundView paddingAngle = $paddingAngle")
		mStartAngle = 90f-LAOB.toFloat()   //+mPaddingAngle
		if (!isRoundCenterTop)
			mStartAngle += 180

		mSweepAngle = 2*LAOB.toFloat()
		//-2*mPaddingAngle
//        NLogger.d("jandy bgPaint mPaddingAngle = $mPaddingAngle $mStartAngle $mSweepAngle")
		val wBgGradient = LinearGradient(
				0f, 0f, mWidth.toFloat(), mHeight.toFloat(),
				intArrayOf(resources.getColor(mColorInts[0]), resources.getColor(mColorInts[1])), floatArrayOf(0.2f, 0.5f),
				Shader.TileMode.CLAMP)
		mScrollPaint.shader = wBgGradient
	}

	override fun onDraw(canvas : Canvas) {
		super.onDraw(canvas)
//		canvas.drawRect(Rect(0, 0, width, height), mFillPaint)
		//渐变背景
		val mRoundStartAngle = showLeftText(canvas)
		val mRoundEndAngle = showRightText(canvas)
		val sweepFillAngle = Math.abs(mRoundEndAngle-mRoundStartAngle)
		if (type == 0) {
			canvas.drawArc(mRoundRect, mRoundStartAngle, sweepFillAngle, false, mBgPaint)
			val allValue = Math.abs(leftText-rightText)
			val lastMultiple = showText/allValue.toFloat()
			val scrollSweepAngle = lastMultiple*sweepFillAngle
			canvas.drawArc(mRoundRect, mRoundStartAngle, scrollSweepAngle, false, mScrollPaint)
		} else {
			canvas.drawArc(mRoundRect, mRoundStartAngle, sweepFillAngle, false, mScrollPaint)
//			val minRoundX = mWidth/3f*2f
//			val minRoundY =
//				mRoundRect.centerY()+Math.sqrt(roundRadius*roundRadius.toDouble()-(minRoundX-mRoundRect.centerX())*(minRoundX-mRoundRect.centerX()))
//					.toFloat()
//			mScrollRadius = mStrokeWidth/2f
//
//		mScrollRoundRect = RectF(
//				minRoundX-mScrollRadius,
//				minRoundY-mScrollRadius,
//				minRoundX+mScrollRadius,
//				minRoundY+mScrollRadius
//								)
		}
//		showCurLocation(canvas)
//		NLogger.d("jandy onDraw $leftText $rightText $showText $needBoradText")
	}

	//显示中间那个小圈
	private fun showCurLocation(canvas : Canvas) {
		canvas.drawArc(mScrollRoundRect, 0f, 360f, false, mScrollPaint)
	}

	//显示左边的文字
	private fun showLeftText(canvas : Canvas) : Float {
		if (needBoradText) {
			val path = Path()
			mTextPaint?.getTextBounds(leftText.toString(), 0, leftText.toString().length, textBound)
			val textWidthAngle = textBound.width()/(2f*Math.PI.toFloat()*(mRoundRect.width()/2f))*360
			val textHeightAngle = textBound.height()/2f/(2f*Math.PI.toFloat()*(mRoundRect.width()/2f))*360
//			NLogger.d("jandy 显示左边的文字 = $leftText $textWidthAngle")
			val startAngle = if (isRoundCenterTop) mStartAngle+textWidthAngle+textHeightAngle else mStartAngle
			val sweepAngle = if (isRoundCenterTop) -textWidthAngle else textWidthAngle
			path.addArc(mRoundRect, startAngle, sweepAngle)
			canvas.drawTextOnPath(
					leftText.toString(),
					path,
					0f,
					textBound.height()/2f,
					mTextPaint)
			return if (isRoundCenterTop) mStartAngle+paddingAngle+textWidthAngle+textHeightAngle else mStartAngle+paddingAngle+textWidthAngle+textHeightAngle
		}
		return if (isRoundCenterTop) mStartAngle+paddingAngle else mStartAngle+paddingAngle
	}

	//显示右边的文字
	private fun showRightText(canvas : Canvas) : Float {
		if (needBoradText) {
			val path = Path()
			mTextPaint?.getTextBounds(rightText.toString(), 0, rightText.toString().length, textBound)
			val textWidthAngle = textBound.width()/(2f*Math.PI.toFloat()*(mRoundRect.width()/2f))*360
			val textHeightAngle = textBound.height()/2f/(2f*Math.PI.toFloat()*(mRoundRect.width()/2f))*360
//			NLogger.d("jandy 显示右边的文字 = $rightText $textWidthAngle")
			val startAngle = if (isRoundCenterTop) mStartAngle+mSweepAngle else mStartAngle+mSweepAngle-textWidthAngle-textHeightAngle
			val sweepAngle = if (isRoundCenterTop) -textWidthAngle else textWidthAngle
			path.addArc(mRoundRect, startAngle, sweepAngle)
			canvas.drawTextOnPath(
					rightText.toString(),
					path,
					5f,
					textBound.height()/2f,
					mTextPaint)
			return if (isRoundCenterTop) mStartAngle+mSweepAngle-paddingAngle-textWidthAngle-textHeightAngle else mStartAngle+mSweepAngle-paddingAngle-textWidthAngle-textHeightAngle
		}
		return if (isRoundCenterTop) mStartAngle+mSweepAngle-paddingAngle else mStartAngle+mSweepAngle-paddingAngle
	}
	//天气的渐变
	/**
	 * Create a shader that draws a linear gradient along a line.   创建一个沿着"一条线"绘制线性渐变的着色器？
	 *
	 * @param x0           "一条线"开始位置的X   - 这个坐标是以 左上角为原点
	 * @param y0           "一条线"开始位置的Y
	 * @param x1           "一条线"结束为止的X
	 * @param y1           "一条线"结束为止的Y
	 * @param colors       颜色数组，可以是3个以上
	 * @param positions    颜色分段权重：比如说，有3种颜色，红绿蓝渐变，这里的positions的值是new float[]{0, 0.75f, 1f};
	 *                     则，0到0.75这一段，是红色渐变为绿色，0.75到1这一段是 绿色渐变为蓝色；
	 * @param tile         填充模式？
	 *                     详解：
	 *                     CLAMP : 重复最后一种颜色直到View结束（当你的起始结束的坐标并没有覆盖整个View，那么这种模式将会用最后一种颜色填充剩余的部分）
	 *                     REPEAT: 当你的起始结束的坐标并没有覆盖整个View，这种模式将会进行颜色的重新渐变；
	 *                     MIRROR: 镜像模式绘制,当你的起始结束的坐标并没有覆盖整个View，剩余的部分将会尽量和已经绘制的部分颜色对称;
	 */
	fun initTextAndProgress(startTemp : Int, endTemp : Int, curTemp : Int) {
		leftText = startTemp
		rightText = endTemp
		showText = curTemp
	}

	fun setProgress(progress : Int) {
		showText = progress
		draw()
	}

	fun setTextSize(size : Float) {
		mTextSize = size
		mTextPaint.textSize = size
	}

	fun setColorIntArray(colorIntArray : IntArray) {
		if (colorIntArray.isNotEmpty() && colorIntArray.size>=2)
			mColorInts = intArrayOf(colorIntArray[0], colorIntArray[1])
		val wBgGradient = LinearGradient(
				0f, 0f, mWidth.toFloat(), mHeight.toFloat(),
				intArrayOf(resources.getColor(mColorInts[0]), resources.getColor(mColorInts[1])), floatArrayOf(0.2f, 0.5f),
				Shader.TileMode.CLAMP)
		mScrollPaint?.shader = wBgGradient
	}

	fun setNeedBoradText(isNeed : Boolean) {
		needBoradText = isNeed
	}

	fun setType(type : Int) {
		this.type = type
	}

	fun draw() {
		postInvalidate()
	}

	fun showGradientColor(leftColor : Int, rightColor : Int) {
		val wBgGradient = LinearGradient(
				0f, 0f, mWidth.toFloat(), mHeight.toFloat(),
				intArrayOf(
						resources.getColor(leftColor),
						resources.getColor(leftColor),
						resources.getColor(rightColor)),
				floatArrayOf(0f, 0.45f, 1f),
				Shader.TileMode.CLAMP)
		mScrollPaint.shader = wBgGradient
	}

	fun setStrokeMultiple(m : Float) {
		if (m<=0f || m>1f) return
		mBgPaint?.strokeWidth = mStrokeWidth*m
		mScrollPaint?.strokeWidth = mStrokeWidth*m
	}
}