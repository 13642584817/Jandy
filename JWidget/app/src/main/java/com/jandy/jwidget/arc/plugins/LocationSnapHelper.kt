package com.jandy.jwidget.arc.plugins

import android.view.View
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.jandy.jwidget.arc.plugins.ArcLinearLayoutManager

class LocationSnapHelper : LinearSnapHelper() {

    override fun calculateDistanceToFinalSnap(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View
    ): IntArray? {
        val out = IntArray(2)
        out[0] = 0
        out[1] = (layoutManager as ArcLinearLayoutManager).getSnapHeight()
        return out
    }

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager): View? {
        val custLayoutManager = layoutManager as ArcLinearLayoutManager
        return custLayoutManager.findSnapView()
    }

}