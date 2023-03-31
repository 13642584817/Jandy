package com.jandy.jwidget.turntable.common

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.SizeUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jandy.jwidget.R
import com.jandy.jwidget.turntable.entity.TurnItemEntity

class TurntableAdapter :
    BaseQuickAdapter<TurnItemEntity, BaseViewHolder>(R.layout.item_layout_turntable_view, null) {


    private var wordSize = 20f
    private var wordColor = ColorUtils.getColor(R.color.ntt_color_white)
    private var mPaddingEnd = 0
    private var mMarginEnd = -999

    @SuppressLint("ResourceAsColor")
    override fun convert(holder: BaseViewHolder, item: TurnItemEntity) {
        if (data == null) return
        try {
            holder.getView<TextView>(R.id.tv_title).apply {
                text = item.text
                textSize = wordSize
                setTextColor(wordColor)
                setPadding(0, 0, mPaddingEnd, 0)
                layoutParams = layoutParams.apply {
                    if (this !is ConstraintLayout.LayoutParams) return@apply
                    if (mMarginEnd == -999) return@apply
                    marginEnd = mMarginEnd
                }
            }
        } catch (e: Exception) {

        }
    }

    fun setTextStyle(textSize: Int, textColor: Int, paddingRight: Int) {
        this.wordSize = textSize.toFloat()
        this.wordColor = ColorUtils.getColor(textColor)
        this.mPaddingEnd = SizeUtils.sp2px(paddingRight.toFloat())
    }

    fun setPaddingEnd(paddingRight: Int) {
        this.mPaddingEnd = SizeUtils.sp2px(paddingRight.toFloat())
    }

    fun setMarginEnd(marginEnd : Int) {
        this.mMarginEnd = SizeUtils.dp2px(marginEnd.toFloat())
    }
}