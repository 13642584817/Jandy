package com.jandy.jwidget.turntable.plugins

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

class TurnRecyclerView : RecyclerView {

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attr: AttributeSet?) : super(context, attr) {
    }

    constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attr,
        defStyleAttr
    )

    var listener: OnTouchEventView? = null

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val dispatch = super.dispatchTouchEvent(ev)
        listener?.onTouchEvent(ev)
        return dispatch
    }

    interface OnTouchEventView {
        fun onTouchEvent(ev: MotionEvent?)
    }
}