package com.jandy.jwidget.globalscroll

import androidx.recyclerview.widget.RecyclerView
import com.jandy.jwidget.R
import com.jandy.jwidget.globalscroll.entity.ScrollItemEntity
import com.jandy.jwidget.globalscroll.layoutmanager.GlobalScrollLayoutManager_75
import com.jandy.jwidget.globalscroll.plugins.RecyclerAdapterGlobalScroll

class GlobalScrollHelper {

    private lateinit var rv: RecyclerView
    lateinit var adapter: RecyclerAdapterGlobalScroll

    constructor(rootView: GlobalScrollView) {
        rv = rootView.findViewById(R.id.rv_view)
        rv.layoutManager = GlobalScrollLayoutManager_75()
        adapter = RecyclerAdapterGlobalScroll(R.layout.layout_global_item_view)
        rv.adapter = adapter
        rv.itemAnimator=null
    }

    constructor(rootView: GlobalScrollView, data: MutableList<ScrollItemEntity>) {
        rv = rootView.findViewById(R.id.rv_view)
        rv.layoutManager = GlobalScrollLayoutManager_75()
        adapter = RecyclerAdapterGlobalScroll(R.layout.layout_global_item_view)
        adapter.data = data
        rv.adapter = adapter
    }

    fun setUpRoundDiameter(dia: Int) {
        adapter.setupRoundDiameter(dia)
    }

    fun setData(data: MutableList<ScrollItemEntity>) {
        (rv.layoutManager as GlobalScrollLayoutManager_75).reloadView()
        adapter.setList(data)
    }

    fun setGlobalOnItemClickListener(listener: RecyclerAdapterGlobalScroll.GlobalItemOnClickListener?) {
        adapter.click = listener
    }

    fun setGlobalOnItemLongClickListener(listener: RecyclerAdapterGlobalScroll.GlobalItemOnLongClickListener?) {
        adapter.longClick = listener
    }

    fun notifyDataSetChanged() {
        (rv.layoutManager as GlobalScrollLayoutManager_75).reloadView()
        adapter?.notifyDataSetChanged()
    }

    fun notifyDataSetChanged(position: Int) {
        (rv.layoutManager as GlobalScrollLayoutManager_75).reloadView()
        adapter?.notifyItemChanged(position)
    }

    fun flashReview() {
        (rv.layoutManager as GlobalScrollLayoutManager_75)?.review()
    }
}