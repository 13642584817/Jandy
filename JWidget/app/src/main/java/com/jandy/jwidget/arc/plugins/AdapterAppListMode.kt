package com.jandy.jwidget.arc.plugins

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Priority
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jandy.jwidget.ImageLoader.ImageLoader
import com.jandy.jwidget.R
import com.jandy.jwidget.globalscroll.entity.ScrollItemEntity

class AdapterAppListMode :
		BaseQuickAdapter<ScrollItemEntity, BaseViewHolder>(
				R.layout.item_layout_app_list_view
														  ) {

	private val TAG = this.javaClass.simpleName
	var click : ArcItemOnClickListener? = null
	var longClick : ArcItemOnLongClickListener? = null

	@SuppressLint("ResourceType")
	override fun convert(holder : BaseViewHolder, item : ScrollItemEntity) {
		holder.apply {
			getView<ConstraintLayout>(R.id.cl_head).apply {
				setOnClickListener {
					click?.onItemClick(recyclerView, this@AdapterAppListMode, holder.layoutPosition)
				}

				setOnLongClickListener {
					longClick?.onItemLongClick(
							recyclerView,
							this@AdapterAppListMode,
							holder.layoutPosition
											  )
					true
				}
			}

			getView<TextView>(R.id.tv_app_name)?.text = item.name

			getView<ImageView>(R.id.iv_app_icon).apply {
				if (!item.iconUrl.isNullOrEmpty()) {
					ImageLoader.urlLoader(context, item.iconUrl, Priority.LOW, this)
				} else {
					setImageDrawable(item.icon)
				}
			}

//			getView<ImageView>(R.id.v_bg).apply {
//				when (item.appActive) {
//					1    -> {
//						//未安装
//						setImageResource(R.drawable.icon_uninstall_app)
//						return@apply
//					}
//					2    -> {
//						//下载中
//						setImageResource(R.drawable.icon_downloading_app)
//						return@apply
//					}
//					3    -> {
//						//安装中
//						setImageResource(R.drawable.icon_installing_app)
//						return@apply
//					}
//					else -> {//正常
//						setImageDrawable(null)
//					}
//				}
//
//				when (item.type) {
//					0    -> {
//						//锁住
//						setImageResource(R.drawable.icon_app_lock_view)
//					}
//					1    -> {
//						//定时
//						setImageResource(R.drawable.icon_app_timer_view)
//					}
//					else -> {
//						setImageDrawable(null)
//					}
//				}
//			}

			getView<ImageView>(R.id.iv_select).apply {
				setImageResource(if (item.isCollect) R.drawable.icon_collect else R.drawable.icon_no_collect)
				setOnClickListener {
					click?.onItemCollectClick(recyclerView, this@AdapterAppListMode, holder.layoutPosition)
				}
			}

			getView<ImageView>(R.id.iv_cancel_select).apply {
				visibility = if (item.isCollect) View.VISIBLE else View.GONE
				setOnClickListener {
					click?.onItemCancelCollectClick(recyclerView, this@AdapterAppListMode, holder.layoutPosition)
				}
			}
		}
	}

	interface ArcItemOnClickListener {

		fun onItemClick(rv : RecyclerView, adapter : BaseQuickAdapter<*, *>, position : Int)

		fun onItemCollectClick(rv : RecyclerView, adapter : BaseQuickAdapter<*, *>, position : Int)

		fun onItemCancelCollectClick(rv : RecyclerView, adapter : BaseQuickAdapter<*, *>, position : Int)
	}

	interface ArcItemOnLongClickListener {

		fun onItemLongClick(rv : RecyclerView, adapter : BaseQuickAdapter<*, *>, position : Int)
	}

}