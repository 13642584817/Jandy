package com.jandy.jwidget.globalscroll.plugins

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.jandy.jwidget.globalscroll.plugins.ZoomRoundImageView

class OverRoundImageView : ZoomRoundImageView {

    constructor(context: Context) : this(context, null) {
    }

    constructor(context: Context, attr: AttributeSet?) : this(context, attr, 0) {
    }

    constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attr,
        defStyleAttr
    ) {

    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return false
    }

}