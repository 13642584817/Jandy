package com.benew.ntt.jreading.arch.widget.lyric

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import com.benew.ntt.jreading.arch.widget.lyric.entity.LyricEntity
import com.benew.ntt.jreading.arch.widget.lyric.intereface.LyricListener
import com.benew.ntt.jreading.arch.widget.lyric.child.SentenceView
import com.benew.ntt.jreading.arch.widget.lyric.intereface.OnLyricItemClickListener
import com.ntt.core.nlogger.NLogger

open class LyricView(context : Context, attrs : AttributeSet?) : ScrollView(context, attrs) {
	companion object {

		private const val TAG = "LyricView"
	}

	private val mContentLayout = LinearLayout(context)
	private var mPosition = -1
	private var mCurrView : SentenceView? = null
	private var mSkipHead = -1.0
	private var mSkipTail = -1.0
	private var mList : MutableList<LyricEntity>? = null
	private var mListener : LyricListener? = null
	private var mOffset : Float = 0.0f

	//+++
	var mItemClickListener : OnLyricItemClickListener? = null

	init {
		generateContentLayout()
	}

	fun setListener(listener : LyricListener?) {
		mListener = listener
	}

	fun getSkipHead() : Double {
		return mSkipHead
	}

	fun getData() = mList

	fun resetData() {
		mPosition = -1
		mCurrView = null
		mSkipHead = -1.0
		mSkipTail = -1.0
		mList = null
//		mListener = null
//		mOffset = 0.0f
		mContentLayout.removeAllViews()
	}

	//指定某个position
	fun touchPosition(position : Int) {
		if (position != mPosition) {
			mSkipTail = -1.0
			mSkipTail = -1.0
		}
		mPosition = position-1
	}

	/**
	 * 设置数据
	 */
	fun setData(list : MutableList<LyricEntity>?) {
		resetData()
		mList = list
		val measureListener = object : SentenceView.MeasureListener {
			var mH = -1f
			override fun measure(view : View?, h : Float) {
				//计算到滑到最后一行距离底部距离
				if (mH != h) {
					mContentLayout.post {
						mContentLayout.setPadding(
								0,
								mOffset.toInt(),
								0,
								(height-h-mOffset).toInt()
												 )
					}
				}
			}
		}
		list?.forEachIndexed { index, item ->
			val view = generateSentenceView(index, item)
			if (index == list.size-1) {
				view.mMeasureListener = measureListener
			}
		}

	}

	fun getPosition() = mPosition

	fun getItemCnt() = if (mList.isNullOrEmpty()) {
		0
	} else {
		mList!!.size
	}

	fun getData(position : Int) : LyricEntity? {
		return if (!mList.isNullOrEmpty() && position>=0 && position<mList!!.size) {
			mList?.get(position)
		} else {
			null
		}
	}

	fun getCurrData() : LyricEntity? {
		return if (!mList.isNullOrEmpty() && mPosition>=0 && mPosition<mList!!.size) {
			mList?.get(mPosition)
		} else {
			null
		}
	}

	fun setTopOffset(offset : Float) {
		mOffset = offset
	}

	/**
	 * 设置当前播放时间
	 */
	fun setCurrTime(currTime : Double) {
		if (mContentLayout.childCount == 0) {
			//没有子view说明没有数据
			return
		}

		if (currTime<=mSkipTail) {
			//小于mSkipTail，说明还在当前句子，刷新当前的view
			if (currTime>=mSkipHead) {
				mCurrView?.progress(currTime)
			}
		} else {
			val position = if (mPosition<-1) {
				0
			} else {
				mPosition+1
			}
			(position until mList!!.size).forEach { i ->
				val item = mList!![i]
				if (!item.text.isNullOrBlank()) {
					if (i != mPosition && currTime<=(item.skipTail
							?: 0.0)
					) {
						if (mPosition != -1) {
							NLogger.d(TAG, "上一句,mPosition=$mPosition")
							mListener?.onSentenceEnd(mPosition)
						}
						mSkipHead = item.skipHead
							?: 0.0
						mSkipTail = item.skipTail
							?: 0.0
						mPosition = i
						switchNext()
						mListener?.onSentenceChange(mPosition)
						NLogger.d(
								TAG, "下一句,mPosition=$mPosition",
								"mSkipHead=$mSkipHead",
								"mSkipTail=$mSkipTail",
								"currTime=$currTime"
								 )
						return
					}
				}
			}
		}
	}

	/**
	 * 切换下一个
	 */
	private fun switchNext() {
		(0 until mContentLayout.childCount).forEach { i ->
			val preView = mContentLayout.getChildAt(i) as SentenceView?
			preView?.commonStyle()
			preView?.mListener = null
		}
		mCurrView = mContentLayout.getChildAt(mPosition) as SentenceView?
		mCurrView?.prepareStyle()
		mCurrView?.post { smoothScrollTo(0, (mCurrView!!.top-mOffset).toInt()) }
		mCurrView?.mListener = object : SentenceView.WordListener {
			override fun wordLocation(view : View?, y : Int) {
				view?.let {
					//滚动到该句子的某一行
					smoothScrollTo(0, (it.top+y-mOffset).toInt())
				}
			}
		}
	}

	/**
	 * 生成歌词容器
	 */
	private fun generateContentLayout() {
		val layoutParams = ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT)
		mContentLayout.orientation = LinearLayout.VERTICAL
		mContentLayout.layoutParams = layoutParams
		addView(mContentLayout)
	}

	/**
	 * 生成歌词句子
	 */
	private fun generateSentenceView(index : Int, entity : LyricEntity) : SentenceView {
		val view = SentenceView(context)
		val layoutParams = LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT)
		view.setData(entity)
		view.setOnClickListener { v ->
			touchPosition(index)
			mItemClickListener?.onItemClick(index, entity, v)
		}
		view.setOnLongClickListener {
			mItemClickListener?.onItemLongClick(index, entity, it)
			true
		}
		mContentLayout.addView(view, layoutParams)
		return view
	}

}