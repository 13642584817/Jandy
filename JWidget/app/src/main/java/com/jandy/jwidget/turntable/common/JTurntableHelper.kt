package com.jandy.jwidget.turntable.common

import android.view.View
import com.jandy.jwidget.R
import com.jandy.jwidget.turntable.TurntableHelper
import com.jandy.jwidget.turntable.TurntableView
import com.jandy.jwidget.turntable.entity.TurnItemEntity
import com.jandy.jwidget.turntable.plugins.TurnImageView
import com.jandy.jwidget.turntable.plugins.TurnableLayoutManager

class JTurntableHelper : TurntableHelper {

    private lateinit var adapter: TurntableAdapter

    var jListener: OnJItemClickCallback? = null

    constructor(turntableView: TurntableView) : super(turntableView) {
        adapter = TurntableAdapter()
//        adapter.setTextStyle(20, R.color.ntt_color_ffffffff,0)  //默认
        setAdapterData(adapter)
        val mTurnButton = turntableView.findViewById<TurnImageView>(R.id.tiv_turn)
        mTurnButton.listener = object : TurnImageView.OnTouchEventListener {
            override fun onClick() {
                val layoutManager = rv.layoutManager as TurnableLayoutManager
                val position = layoutManager?.getCurrentSelectPosition() ?: return
                jListener?.onItemClick(rv, adapter, position)
            }

            override fun onScrollY(dy: Float) {
            }
        }
    }

    //item,文字大小和颜色
    fun setTextStyle(textSize: Int, textColor: Int, paddingRight: Int) {
        adapter.setTextStyle(textSize, textColor, paddingRight)
    }

    fun setPaddingEnd(paddingRight: Int) {
        adapter.setPaddingEnd(paddingRight)
    }

    fun setMarginEnd(marginEnd : Int) {
        adapter.setMarginEnd(marginEnd)
    }
    //可以设置点击回调
    //    var listener: OnItemOnClickCallback? = null

    //滑动到position
    //fun scrollPosition(position :Int)

    //重新设置显示的数据
    fun setDatas(datas: List<TurnItemEntity>) {
        if (datas == null || datas.size == 0) return
        adapter.setList(datas)
    }

    //增加数据
    fun addData(data: TurnItemEntity) {
        if (data == null) return
        adapter.addData(data)
    }

    //删除数据
    fun removeData(data: TurnItemEntity) {
        if (data == null) return
        adapter.remove(data)
    }

    //获取数据
    fun getDatas(): List<TurnItemEntity> {
        if (adapter.data == null || adapter.data.size == 0) return ArrayList<TurnItemEntity>()
        return adapter.data
    }

    fun getDataForPosition(position: Int): TurnItemEntity? {
        if (adapter.data == null || adapter.data.size == 0) return null
        if (position < 0 || position >= adapter.data.size) return null
        try {
            return adapter.data[position]
        } catch (e: Exception) {
            return null
        }
    }

    interface OnJItemClickCallback {
        fun onItemClick(
            view: View,
            adapter: TurntableAdapter,
            position: Int
        )
    }

}