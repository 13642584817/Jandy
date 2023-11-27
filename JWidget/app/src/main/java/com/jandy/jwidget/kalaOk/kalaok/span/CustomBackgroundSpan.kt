package com.benew.ntt.jreading.arch.widget.kalaok.span

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.style.ReplacementSpan
import com.blankj.utilcode.util.Utils
import com.ntt.common.utils.UtDimen

class CustomBackgroundSpan(
		private val background: Drawable,
		private val dividerPercentage: Float
						  ) : ReplacementSpan() {

	override fun getSize(
			paint: Paint, text: CharSequence, start: Int, end: Int, fm: Paint.FontMetricsInt?
						): Int {
		return paint.measureText(text, start, end).toInt()
	}

	override fun draw(
			canvas: Canvas,
			text: CharSequence,
			start: Int,
			end: Int,
			x: Float,
			top: Int,
			y: Int,
			bottom: Int,
			paint: Paint
					 ) {
		val textWidth = paint.measureText(text, start, end).toInt()
		val cx = x + textWidth * dividerPercentage
		val cy = (top + bottom) / 2

		val bounds = Rect().apply {
			left = cx.toInt()
			right = (cx + UtDimen.dp2px(Utils.getApp(),2f)).toInt()
			this.top = top
			this.bottom = bottom
		}

		background.bounds = bounds
		background.draw(canvas)
	}

}