package com.benew.ntt.jreading.arch.widget.kalaok

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import androidx.annotation.ColorRes
import com.benew.ntt.jreading.R
import com.benew.ntt.jreading.arch.module.vtsdk.factory.VtFactoryReadBook
import com.benew.ntt.jreading.arch.widget.kalaok.entity.KalaOkWord
import com.benew.ntt.jreading.mpv.scanbook.V.widget.center.view.ClickTextView
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.Utils
import com.ntt.common.utils.UtDimen
import com.ntt.core.nlogger.NLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class KalaOkView : ClickTextView {

	constructor(context : Context) : this(context, null) {
	}

	constructor(context : Context, attrs : AttributeSet?) : super(context, attrs) {
	}

	private val TAG = "KalaOkView"
	private var mKalaOkWords : MutableList<KalaOkWord> = ArrayList()
	private var mLastText : String? = ""
	private var mJob : Job? = null
	private var position : Int = 0
	private val lock = Any()

	//kalaOk的index 上一次读到哪个位置
	private var lastReadIndex = 0
	private var lastMills = 0L  //缩短延迟
	private var totalDelay = 0.0 //总共需要修复的延迟
	private val preDelay = 30L //每次延迟

	//定位屏幕位置
	var mViewStart = 0
	var mViewEnd = 0
	var mLastLine = 0
	var mListener : IKalaOkListener? = null
	var isShow : Boolean = false

	companion object {

		private const val DELAY_START = 0.0 //开始播放音频的延迟时间 ms
	}

	private fun test() {
		mKalaOkWords.takeIf { it is MutableList<KalaOkWord> }.apply {
			this as MutableList
			add(KalaOkWord().apply {
				word = "今天"
				skipHead = 0.0
				skipTail = 2000.0
			})
			add(KalaOkWord().apply {
				word = "我在"
				skipHead = 3000.0
				skipTail = 5000.0
			})
			add(KalaOkWord().apply {
				word = "地球打转了"
				skipHead = 6000.0
				skipTail = 11000.0
			})
			add(KalaOkWord().apply {
				word = "一千八百次"
				skipHead = 11000.0
				skipTail = 16000.0
			})
		}
		setWordsText("今天 我在 地球打转了一千八百次", mKalaOkWords)
		startRun()
	}

	fun setWordsText(words : String?, mKalaOkWords : MutableList<KalaOkWord>) {
		synchronized(lock) {
			mJob?.cancel()
			this.text = words
			this.mLastText = words
			this.mKalaOkWords = mKalaOkWords
		}
	}

	override fun onAttachedToWindow() {
		super.onAttachedToWindow()
		isShow = true
	}

	fun startRun() {
		synchronized(lock) {
			NLogger.d(TAG, "开始卡拉ok 模式 $mLastText")
			lastMills = System.currentTimeMillis()
			totalDelay = 0.0
			mViewStart = 0
			mViewEnd = 0
			mLastLine = 0
			mJob?.cancel()
			mJob = GlobalScope.launch(Dispatchers.Default) {
				if (mLastText.isNullOrEmpty()) return@launch
				twoLoop()
				NLogger.d(TAG, " 结束 展示 $mLastText ")
				withContext(Dispatchers.Main) {
					this@KalaOkView.text = mLastText
				}
			}
			mJob?.start()
		}
	}

	suspend fun twoLoop() {
		position = 0
		lastReadIndex = 0
		var needDelay = 0.0
		var progressDelay = 0.0
		var lastPosition = -1
		var lastIndex = -1
		while (position<mKalaOkWords.size) {
			if (lastPosition != position) {
				NLogger.d(TAG, "lastPosition = ${lastPosition} position = ${position} ")
				lastPosition = position
				needDelay = (mKalaOkWords[position].skipTail-mKalaOkWords[position].skipHead)*1000/VtFactoryReadBook.mRepeatSpeed
			}
			NLogger.d(TAG, "position = ${position} needDelay = ${needDelay}  word = ${GsonUtils.toJson(mKalaOkWords[position])}")
			val showWord = mKalaOkWords[position].word
			if (showWord.isNullOrEmpty()) {
				NLogger.d(TAG, " position = ${position} showWord 为null ${GsonUtils.toJson(mKalaOkWords)} ")
				position++
				continue
			}
			val startIndex = indexOf(mLastText!!, lastReadIndex, showWord)
			NLogger.d(TAG, "position = $position startIndex = ${startIndex}  ")
			withContext(Dispatchers.Main) {
				try {
					NLogger.d(TAG, "isShow =  $isShow ")
					val mRect = Rect()
					val line = layout.getLineForOffset(startIndex)
					if (mLastLine != line) {
						mLastLine = line
						layout.getLineBounds(line, mRect)
						val inScreenArray = IntArray(2)
						getLocationOnScreen(inScreenArray)
						mViewStart = (inScreenArray.takeIf { it.size == 2 }?.get(1)
							?: 0)+mRect.top
						mViewEnd = mViewStart+mRect.height()
						mListener?.onNextLine(mViewStart, mViewEnd)
					}
				} catch (e : Exception) {
					NLogger.d(TAG, "isShow =  $isShow error = ${e.toString()}")
					e.printStackTrace()
				}
			}
			if (needDelay<preDelay) {
				delay(needDelay.toLong())
				val endIndex = startIndex+showWord.length
				withContext(Dispatchers.Main) {
					setTextChangeColorAndSize(endIndex)
				}
				position++
				lastReadIndex = endIndex
				progressDelay = 0.0
				lastIndex = -1
				//show
				continue
			}
			delay(preDelay)
			//判断是否进行下一个单词
			progressDelay += preDelay
			//show
			val showWordLen = if (progressDelay>=needDelay) showWord.length.toDouble() else progressDelay/needDelay*showWord.length
			val endIndex = Math.ceil(startIndex+showWordLen).toInt()
			NLogger.d(TAG, "position = ${position} endIndex = ${endIndex} progressDelay = $progressDelay needDelay = ${needDelay}")
			if (lastIndex != endIndex) {
				withContext(Dispatchers.Main) {
					setTextChangeColorAndSize(endIndex)
				}
				lastIndex = endIndex
			}

			if (progressDelay>=needDelay) {
				position++
				lastReadIndex = endIndex
				progressDelay -= needDelay
				lastIndex = -1
			}
		}
	}

	private fun setTextChangeColorAndSize(endIndex : Int) {
		if (mLastText.isNullOrEmpty()) return
		var end = endIndex
		if (endIndex>=mLastText!!.length) {
			NLogger.e(TAG, "error endIndex>=mLastText!!.length")
			end = mLastText!!.length
		}
		val spannableString = SpannableString(mLastText)
		spannableString.setSpan(ForegroundColorSpan(context.getColor(R.color.teal_200)), 0, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
		this.text = spannableString
	}

	suspend fun oneLoop() {
		position = 0
		lastReadIndex = 0
		while (position<mKalaOkWords.size) {
			var needDelay =
				if (position == 0) DELAY_START/VtFactoryReadBook.mRepeatSpeed else ((mKalaOkWords[position].skipHead-mKalaOkWords[position-1].skipHead)*1000/VtFactoryReadBook.mRepeatSpeed)
			val showWord = mKalaOkWords[position].word
			if (showWord.isNullOrEmpty()) {
				NLogger.d(TAG, " position = ${position} showWord 为null ${GsonUtils.toJson(mKalaOkWords)} ")
				position++
				continue
			}
			val startIndex = indexOf(mLastText!!, lastReadIndex, showWord)
			withContext(Dispatchers.Main) {
				try {
					NLogger.d(TAG, "isShow =  $isShow ")
					val mRect = Rect()
					val line = layout.getLineForOffset(startIndex)
					if (mLastLine != line) {
						mLastLine = line
						layout.getLineBounds(line, mRect)
						val inScreenArray = IntArray(2)
						getLocationOnScreen(inScreenArray)
						mViewStart = (inScreenArray.takeIf { it.size == 2 }?.get(1)
							?: 0)+mRect.top
						mViewEnd = mViewStart+mRect.height()
						mListener?.onNextLine(mViewStart, mViewEnd)
//					NLogger.d(TAG, " position = $position 需要高亮部分 = {$showWord}  startIndex = $startIndex line = ${line} mRect = ${
//						GsonUtils.toJson(mRect)
//					} inScreenArray = ${GsonUtils.toJson(inScreenArray)}")
					}
				} catch (e : Exception) {
					NLogger.d(TAG, "isShow =  $isShow error = ${e.toString()}")
					e.printStackTrace()
				}
			}
			if (startIndex == -1) {
				NLogger.d(TAG, "position = ${position} startIndex == -1 ${GsonUtils.toJson(mKalaOkWords)} ")
				position++
				continue
			}
			val endIndex = startIndex+showWord.length
			if (endIndex>(mLastText?.length
					?: 0)) {
				position++
				continue
			}
			NLogger.d(TAG, " position = $position 需要高亮部分 = $showWord allLength =${mLastText?.length} $ startIndex = $startIndex  endIndex = $endIndex ")
			//缩短延迟
			val cutDownDelay = System.currentTimeMillis()-lastMills
			totalDelay += cutDownDelay
			if (needDelay>totalDelay) {
				needDelay -= totalDelay
				totalDelay = 0.0
			} else {
				totalDelay -= needDelay
				needDelay = 0.0
			}
			NLogger.d(TAG, " needDelay = $needDelay 缩短延迟 = $cutDownDelay 所有延迟 = $totalDelay")
			if (needDelay>0.0)
				delay((needDelay).toLong())
			withContext(Dispatchers.Main) {
				setTextChangeColorAndSize(R.color.teal_200, startIndex, endIndex)
			}
			lastMills = System.currentTimeMillis()
			lastReadIndex = endIndex
			position++
		}
		mKalaOkWords!!.last().apply {
			var endDelay = (skipTail-skipHead)*1000/VtFactoryReadBook.mRepeatSpeed
			delay((endDelay).toLong())
		}
	}

	private fun indexOf(original : String, start : Int, indexOfStr : String) : Int {
		if (original.length<=start) return -1
		return original.substring(start).indexOf(indexOfStr)+start
	}

	fun stopRun() {
		synchronized(lock) {
			mJob?.cancel()
			this@KalaOkView.text = mLastText
		}
	}

	fun resetText() {
		synchronized(lock) {
			mJob?.cancel()
			mKalaOkWords.clear()
			mLastText = null
			mListener = null
		}
	}

	private fun setTextChangeColorAndSize(@ColorRes
	colorId : Int, startIndex : Int, endIndex : Int) {
		if (mLastText.isNullOrEmpty()) return
		val spannableString = SpannableString(mLastText)
		spannableString.setSpan(ForegroundColorSpan(context.getColor(colorId)), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//		spannableString.setSpan(AbsoluteSizeSpan((this.textSize*1.2f).toInt()), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
		this.text = spannableString
	}

	override fun draw(canvas : Canvas?) {
		super.draw(canvas)
	}

	override fun onLayout(changed : Boolean, left : Int, top : Int, right : Int, bottom : Int) {
		super.onLayout(changed, left, top, right, bottom)
//		NLogger.d(TAG, "layout =  $layout")
	}

	override fun onDetachedFromWindow() {
		super.onDetachedFromWindow()
		NLogger.d(TAG, "onDetachedFromWindow")
		resetText()
	}

	interface IKalaOkListener {

		fun onNextLine(viewStart : Int, viewEnd : Int)
	}
}

