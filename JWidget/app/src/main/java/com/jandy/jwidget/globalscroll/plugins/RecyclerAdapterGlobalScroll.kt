package com.jandy.jwidget.globalscroll.plugins

import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jandy.jwidget.ImageLoader.ImageLoader
import com.jandy.jwidget.R
import com.jandy.jwidget.globalscroll.entity.ScrollItemEntity

class RecyclerAdapterGlobalScroll : BaseQuickAdapter<ScrollItemEntity, BaseViewHolder> {

    constructor(layoutId: Int) : super(layoutId) {
    }

    var click: GlobalItemOnClickListener? = null
    var longClick: GlobalItemOnLongClickListener? = null
    var dia = 128

    override fun convert(holder: BaseViewHolder, itemEntity: ScrollItemEntity) {
        if (dia >= 0) {
            holder.getView<ConstraintLayout>(R.id.cl).apply {
                val cp = layoutParams as RecyclerView.LayoutParams
                cp.width = dia
                cp.height = dia
                layoutParams = cp
            }
        }
        val iconImg = holder.getView<ZoomRoundImageView>(R.id.iv_app)
        if (!itemEntity.iconUrl.isNullOrEmpty()) {
            ImageLoader.urlLoader(context, itemEntity.iconUrl, iconImg)
        } else {
            iconImg?.setImageDrawable(itemEntity.icon)
        }

        iconImg.setOnClickListener {
            click?.onItemClick(recyclerView, this, holder.layoutPosition)
        }

        iconImg.setOnLongClickListener {
            longClick?.onItemLongClick(recyclerView, this, holder.layoutPosition)
            true
        }
        val iconBg = holder.getView<ImageView>(R.id.v_bg)
//        iconBg.apply {
//            when (itemEntity.appActive) {
//                1 -> {
//                    //未安装
//                    setImageResource(R.drawable.icon_uninstall_app)
//                    return@apply
//                }
//                2 -> {
//                    //下载中
//                    setImageResource(R.drawable.icon_downloading_app)
//                    return@apply
//                }
//                3 -> {
//                    //安装中
//                    setImageResource(R.drawable.icon_installing_app)
//                    return@apply
//                }
//                else->{//正常
//                    setImageDrawable(null)
//                }
//            }
//
//            when (itemEntity.type) {
//                0 -> {
//                    //锁住
//                    setImageResource(R.drawable.icon_app_lock_view)
//                }
//                1 -> {
//                    //定时
//                    setImageResource(R.drawable.icon_app_timer_view)
//                }
//                else -> {
//                    setImageDrawable(null)
//                }
//            }
//        }
    }

    fun setupRoundDiameter(dia: Int) {
        this.dia = dia
    }

    interface GlobalItemOnClickListener {
        fun onItemClick(rv: RecyclerView, adapter: BaseQuickAdapter<*, *>, position: Int)
    }

    interface GlobalItemOnLongClickListener {
        fun onItemLongClick(rv: RecyclerView, adapter: BaseQuickAdapter<*, *>, position: Int)
    }
}