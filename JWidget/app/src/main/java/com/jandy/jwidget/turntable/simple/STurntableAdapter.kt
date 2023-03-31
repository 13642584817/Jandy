package com.jandy.jwidget.turntable.simple

import android.graphics.Color
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.TextView
import com.blankj.utilcode.util.SizeUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jandy.jwidget.R

class STurntableAdapter :
    BaseQuickAdapter<STurntableEntity, BaseViewHolder>(R.layout.layout_item_simple_turnable) {

    private var mFontSize: Float = SizeUtils.dp2px(24f).toFloat()
    private var mPaddingEnd = 0
    private var mHeight = ViewGroup.LayoutParams.WRAP_CONTENT
    private var mItem: STurntableEntity? = null
    private var mSelectedColor: Int? = null


    override fun convert(holder: BaseViewHolder, item: STurntableEntity) {
        val textView = holder.itemView as TextView
        textView.layoutParams.height = mHeight
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mFontSize)
        textView.setPadding(0, 0, mPaddingEnd, 0)
        textView.text = item.text
        mSelectedColor?.let {
            if (mItem == item) {
                textView.setTextColor(it)
            } else {
                textView.setTextColor(Color.WHITE)
            }
        }
    }

    /**
     * 字体配置
     * @param fontSize 字体大小
     * @param paddingEnd 文本尾部空格
     * @param height 高度
     */
    fun setTextConfig(fontSize: Float, paddingEnd: Int, height: Int) {
        mFontSize = fontSize
        mPaddingEnd = paddingEnd
        mHeight = height
        notifyDataSetChanged()
    }

    /**
     * 设置选中的子项
     * @param item 选中项
     * @param selectedColor 选中项的字体颜色
     */
    fun selectedItem(item: STurntableEntity, selectedColor: Int) {
        this.mItem = item
        this.mSelectedColor = selectedColor
        notifyDataSetChanged()
    }

}