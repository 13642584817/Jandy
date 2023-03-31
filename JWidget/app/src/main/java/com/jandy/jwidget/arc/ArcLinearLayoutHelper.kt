package com.jandy.jwidget.arc

import androidx.recyclerview.widget.RecyclerView
import com.jandy.jwidget.globalscroll.entity.ScrollItemEntity
import com.jandy.jwidget.arc.plugins.AdapterAppListMode
import com.jandy.jwidget.arc.plugins.ArcLinearLayoutManager

class ArcLinearLayoutHelper {

    lateinit var adapter: AdapterAppListMode
    private var isCreateSuccess = false
    private lateinit var mRv: RecyclerView

    constructor(rv: RecyclerView?) {
        if (rv == null) return
        mRv = rv
        adapter = AdapterAppListMode()
        rv.adapter = adapter
        val layoutManager = ArcLinearLayoutManager()
        layoutManager.listener = createViewListener
        rv.layoutManager = layoutManager
        rv.itemAnimator = null
    }

    constructor(rv: RecyclerView?, list: MutableList<ScrollItemEntity>) {
        if (rv == null) return
        mRv = rv
        adapter = AdapterAppListMode()
        adapter.data = list
        rv.adapter = adapter
        val layoutManager = ArcLinearLayoutManager()
        layoutManager.listener = createViewListener
        rv.layoutManager = layoutManager
    }

    private val createViewListener = object : ArcLinearLayoutManager.IcallbackListener {
        override fun createViewSuccess() {
            isCreateSuccess = true
        }

    }

    fun setArcItemOnClickListener(listener: AdapterAppListMode.ArcItemOnClickListener?) {
        adapter.click = listener
    }

    fun setArcItemOnLongClickListener(listener: AdapterAppListMode.ArcItemOnLongClickListener?) {
        adapter.longClick = listener
    }

    fun setList(list: MutableList<ScrollItemEntity>) {
        (mRv.layoutManager as ArcLinearLayoutManager).reloadView()
        adapter.setList(list)
    }

    //无内存泄漏问题
    fun notifyDataSetChanged() {
        (mRv.layoutManager as ArcLinearLayoutManager).reloadView()
        adapter.notifyDataSetChanged()
    }

    //有内存泄漏问题
    fun notifyItemChanged(position: Int) {
        (mRv.layoutManager as ArcLinearLayoutManager).reloadView()
        adapter.notifyItemChanged(position)
    }

    fun scrollToPosition(position: Int) {
        if (!isCreateSuccess) {

            return
        }

    }
}