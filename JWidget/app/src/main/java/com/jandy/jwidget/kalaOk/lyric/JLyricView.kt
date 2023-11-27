package com.benew.ntt.jreading.arch.widget.lyric

import android.content.Context
import android.util.AttributeSet
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.benew.ntt.jreading.arch.module.vtsdk.factory.VtFactoryReadBook
import com.blankj.utilcode.util.ScreenUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class JLyricView(context : Context, attrs : AttributeSet?) : LyricView(context, attrs) {

	companion object {

		private const val TAG = "JLyricView"
	}

	private var mJob : Job? = null
	private val preDelay = 30L
	private var lastMillimTime = 0.0  //上一次定位的时间戳

	init {
		val screenHeight = ScreenUtils.getScreenHeight()
		setTopOffset(screenHeight*2/5f)
	}

	fun playKalaOk(index : Int = 0) {
		val headDelay = getData()?.takeIf { getItemCnt()>index }?.get(index)?.skipHead
			?: 0.0
		val endDelay = getData()?.takeIf { getItemCnt()>index }?.get(index)?.skipTail
			?: 0.0
		if ((headDelay == 0.0 && endDelay == 0.0) || headDelay>=endDelay) {
			return
		}
		resetKalaOk()

		mJob?.cancel()
		mJob = findViewTreeLifecycleOwner()?.lifecycleScope?.launch(Dispatchers.Main) {
			lastMillimTime = Math.max(headDelay, lastMillimTime)
			while (lastMillimTime<endDelay) {
				setCurrTime(lastMillimTime)
				delay(preDelay)
				lastMillimTime += preDelay*VtFactoryReadBook.mRepeatSpeed  //加速
			}
			//最后一次
			if (lastMillimTime>=endDelay)
				setCurrTime(endDelay)
		}
		mJob?.start()
	}

	fun pauseKalaOk() {
		mJob?.cancel()
	}

	private fun resetKalaOk() {
		lastMillimTime = 0.0
	}

	override fun onAttachedToWindow() {
		super.onAttachedToWindow()
	}

	override fun onDetachedFromWindow() {
		super.onDetachedFromWindow()
		pauseKalaOk()
	}
}