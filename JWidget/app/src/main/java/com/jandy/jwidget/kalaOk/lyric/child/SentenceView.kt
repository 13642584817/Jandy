package com.benew.ntt.jreading.arch.widget.lyric.child

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.view.MotionEvent
import android.view.View
import com.benew.ntt.jreading.BuildConfig
import com.benew.ntt.jreading.arch.widget.lyric.entity.LyricEntity
import com.blankj.utilcode.util.SizeUtils
import com.ntt.core.nlogger.NLogger

/**
 * 领读View
 */
class SentenceView constructor(
		context : Context
							  ) : View(
		context
									  ) {

	companion object {

		private const val TAG = "SentenceView"
	}

	private val mCommonPaint = TextPaint()
	private val mCurrPaint = TextPaint()
	private val mSelectPaint = TextPaint()
	private var mUsePaint = mCommonPaint
	private var mEntity : LyricEntity? = null
	private var mKalaok = false
	private lateinit var mStaticLayout : StaticLayout
	private var mText : String = ""
	private var mPreWordIndex = -1
	private var mPreWordEnd = -1
	private var mPosition = -1
	private var mSkipHead = 0.0
	private var mSkipTail = 0.0
	private var mCurrTime = 0.0
	private var mClipRect = Rect()

	//每行文本
	private var texts : MutableList<String>? = null

	//文本行数
	private var mLineCnt = 1

	//当前行
	private var mCurrLine = -1
	var mListener : WordListener? = null
	var mMeasureListener : MeasureListener? = null
	private var mWord: String? = null

	interface WordListener {

		fun wordLocation(view : View?, y : Int)
	}

	interface MeasureListener {

		fun measure(view : View?, h : Float)
	}

	init {
		mCommonPaint.isAntiAlias = true //抗锯齿
		mCommonPaint.isDither = true//防抖
		mCommonPaint.color = Color.parseColor("#757575")
		mCommonPaint.textSize = SizeUtils.dp2px(23f).toFloat()

		mCurrPaint.isAntiAlias = true //抗锯齿
		mCurrPaint.isDither = true//防抖
		mCurrPaint.color = Color.WHITE
		mCurrPaint.textSize = SizeUtils.dp2px(23f).toFloat()

		mSelectPaint.isAntiAlias = true //抗锯齿
		mSelectPaint.isDither = true//防抖
		mSelectPaint.color = Color.parseColor("#FFA800")
		mSelectPaint.textSize = SizeUtils.dp2px(23f).toFloat()

	}

	/**
	 * 设数据
	 */
	fun setData(entity : LyricEntity?) {
		this.mEntity = entity
		mText = mEntity?.text
			?: ""

		commonStyle()
	}

	/**
	 * 普通样式
	 */
	fun commonStyle() {
		//add start
		mPosition = -1
		mCurrLine = -1
		mSkipHead = -1.0
		mSkipTail = -1.0
		mPreWordIndex = -1
		mPreWordEnd = -1
		//add end
		mUsePaint = mCommonPaint
		mKalaok = false
		requestLayout()
	}

	/**
	 * 准备播的样式
	 */
	fun prepareStyle() {
		mUsePaint = mCurrPaint
		requestLayout()

		if (!mEntity?.words.isNullOrEmpty()) {
			mKalaok = true
		}
	}

	override fun onMeasure(widthMeasureSpec : Int, heightMeasureSpec : Int) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)
		val width = MeasureSpec.getSize(widthMeasureSpec)
		mStaticLayout = if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
			StaticLayout.Builder
				.obtain(mText, 0, mText.length, mUsePaint, width)
				.setAlignment(Layout.Alignment.ALIGN_CENTER).build()
		} else {
			StaticLayout(
					mText, mUsePaint, width, Layout.Alignment.ALIGN_CENTER,
					Layout.DEFAULT_LINESPACING_MULTIPLIER, Layout.DEFAULT_LINESPACING_ADDITION, true
						)
		}
		val height = mStaticLayout.height
		if (mKalaok) {
			if (texts == null) {
				mLineCnt = mStaticLayout.lineCount
				texts = mutableListOf()
				if (mLineCnt == 1) {
					texts!!.add(mText)
				} else {
					var preIndex = 0
					var substring : String
					for (i in 1 until mLineCnt) {
						val index = mStaticLayout.getLineStart(i)
						substring = mText.substring(preIndex, index)
						texts!!.add(substring)
						preIndex = index
					}
					substring = mText.substring(preIndex, mText.length)
					texts!!.add(substring)
				}
			}
		}
		if (mMeasureListener != null) {
			mMeasureListener?.measure(
					this,
					mUsePaint.fontMetrics.bottom-mUsePaint.fontMetrics.top
									 )
		}
		setMeasuredDimension(width, height)
	}

	/**
	 * 进度中
	 */
	fun progress(timeMs : Double) {
		mCurrTime = timeMs
		if (mText.isBlank()) {
			return
		}
		try {
			if (mEntity?.words.isNullOrEmpty()) {
				//没有单词的
				if (timeMs>=(mEntity?.skipHead
						?: 0.0) && timeMs<=(mEntity?.skipTail
						?: 0.0)
				) {
					if (mUsePaint != mSelectPaint) {
						mUsePaint = mSelectPaint
						requestLayout()
						invalidate()
						return
					} else {
						return
					}
				} else {
					if (mUsePaint != mCurrPaint) {
						mUsePaint = mCurrPaint
						requestLayout()
						invalidate()
						return
					} else {
						return
					}
				}
			} else {
				if (timeMs !in mSkipHead..mSkipTail) {
					//有单词的
					mKalaok = true
					val size = mEntity!!.words.size
					val position = if ((mPosition+1)<0) 0 else (mPosition+1)
					(position until size).forEach { i ->
						val item = mEntity!!.words[i]
						if (!item.word.isNullOrBlank()) {
							if (i != mPosition && timeMs>=(item.skipHead
									?: 0.0) && timeMs<=(item.skipTail
									?: 0.0)
							) {
								mPosition = i
								val indexOf = mText.indexOf(item.word, mPreWordEnd, false)
								if (indexOf>=0 && indexOf != mPreWordIndex) {
									mPreWordEnd = indexOf+item.word.length
									invalidate()
									mPreWordIndex = indexOf
									mSkipHead = item.skipHead
										?: 0.0
									mSkipTail = item.skipTail
										?: 0.0
									NLogger.d(
											TAG,
											"progress",
											item.word,
											mPosition,
											i,
											mPreWordIndex,
											indexOf,
											timeMs,
											mEntity?.skipHead,
											mEntity?.skipTail
											 )
									return
								}
							}
						}
					}
				} else {
					invalidate()
				}
			}
		} catch (e : Exception) {
			NLogger.d("progress 解析异常", e.message)
		}

	}

	override fun onDraw(canvas : Canvas) {
		if (!mKalaok) {
			mStaticLayout.draw(canvas)
		} else {
			drawKalaok(canvas)
		}
	}

	private fun drawKalaok(canvas : Canvas) {
		if (mPreWordIndex != -1) {
			val currLine = mStaticLayout.getLineForOffset(mPreWordIndex)
			var currProgress = -1f
			for (i in 0 until mLineCnt) {
				val itemText = texts!![i]
				val right = mStaticLayout.getLineRight(i)
				val left = mStaticLayout.getLineLeft(i)
				val baseLine = mStaticLayout.getLineBaseline(i).toFloat()
				if (i<currLine) {
					canvas.drawText(itemText, left, baseLine, mSelectPaint)
				} else {
					val lineProgress = if (i == currLine) {
						val start = mStaticLayout.getPrimaryHorizontal(mPreWordIndex)
						//测量字体的宽度
						val textW = mSelectPaint.measureText(
								mText,
								mPreWordIndex,
								mPreWordEnd
															)
						currProgress =
							(mCurrTime-mSkipHead).toFloat()/(mSkipTail-mSkipHead).toFloat()*textW+start
						if (BuildConfig.DEBUG) {
							NLogger.d(
									TAG,
									"mCurrTime=$mCurrTime",
									"mSkipHead=$mSkipHead",
									"mSkipTail=$mSkipTail",
									"mSkipTail=$mSkipTail",
									"mPreWordIndex=$mPreWordIndex",
									"currProgress=$currProgress",
									"currLine=$currLine",
									"i=$i",
									"start=$start",
									"textW=$textW",
									mText.subSequence(mPreWordIndex, mPreWordEnd),
									 )
						}

						currProgress
					} else {
						currProgress+left
					}

					if (currProgress != -1f) {
						if (currProgress>right) {
							canvas.drawText(itemText, left, baseLine, mSelectPaint)
							currProgress -= right
						} else {
							val top = mStaticLayout.getLineTop(i)
							val bottom = mStaticLayout.getLineBottom(i)

							drawText(
									canvas,
									mSelectPaint,
									itemText,
									left,
									left.toInt(),
									top,
									lineProgress,
									bottom,
									baseLine
									)

							drawText(
									canvas,
									mCurrPaint,
									itemText,
									left,
									lineProgress.toInt(),
									top,
									right,
									bottom,
									baseLine
									)
							currProgress = -1f
							if (mCurrLine != -1 && mCurrLine != i) {
								mListener?.wordLocation(this, mStaticLayout.getLineTop(i))
							}
							mCurrLine = i
						}
					} else {
						canvas.drawText(itemText, left, baseLine, mCurrPaint)
					}
				}
			}
		} else {
			mStaticLayout.draw(canvas)
		}

	}

	private fun drawText(
			canvas : Canvas,
			paint : Paint,
			text : String,
			x : Float,
			left : Int,
			top : Int,
			right : Float,
			bottom : Int,
			baseLine : Float
						) {
		canvas.save()
		mClipRect.set(left, top, right.toInt(), bottom)
		canvas.clipRect(mClipRect) //裁剪一部份绘制不同颜色
		canvas.drawText(text, x, baseLine, paint)
		canvas.restore()
	}



	override fun onTouchEvent(event: MotionEvent): Boolean {
		try {
			val line = mStaticLayout.getLineForVertical(event.y.toInt())
			val offset = mStaticLayout.getOffsetForHorizontal(line, event.x)
			if (mText == null || mText.length <= offset) {
				return super.onTouchEvent(event)
			}
			val char = mText[offset]
			//中文
			if (Character.isIdeographic(char.code)) {
				mWord = char.toString()
			} else if (char.isLetter()) {
				// 找到被长按的单词的起始位置
				var start = offset
				while (start > 0 && !mText[start - 1].isWhitespace()) {
					start--
				}

				// 找到被长按的单词的结束位置
				var end = offset
				while (end < mText.length && !mText[end].isWhitespace()) {
					end++
				}

				// 获取被长按的单词
				mWord = mText.substring(start, end)
			}

		} catch (e: Exception) {
			e.printStackTrace()
		}
		return super.onTouchEvent(event)
	}

	fun getTouchWord(): String? {
		return mWord
	}
}