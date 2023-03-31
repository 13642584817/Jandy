package com.jandy.jwidget.globalscroll

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.jandy.jwidget.R

class GlobalScrollView : ConstraintLayout {

    constructor(context: Context) : this(context, null) {
    }

    constructor(context: Context, attr: AttributeSet?) : this(context, attr, 0) {
    }

    constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attr,
        defStyleAttr
    )

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_global_recycler_view, this)
    }

}