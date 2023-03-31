package com.jandy.jwidget.turntable

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.jandy.jwidget.R

open class TurntableView : ConstraintLayout {

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attr: AttributeSet?) : super(context, attr) {
    }

    constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attr,
        defStyleAttr
    )


    init {
        inflate(context, R.layout.layout_turnable_view, this)

    }



}