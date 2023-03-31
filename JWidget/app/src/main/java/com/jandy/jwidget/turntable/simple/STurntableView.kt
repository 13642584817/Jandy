package com.jandy.jwidget.turntable.simple

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import com.jandy.jwidget.R
import com.jandy.jwidget.turntable.TurntableHelper
import com.jandy.jwidget.turntable.TurntableView

@SuppressLint("CustomViewStyleable")
class STurntableView : TurntableView {

    private var mAdapter: STurntableAdapter? = null
    private var mTurntableHelper: TurntableHelper? = null
    private var mFontSize = 0f
    private var mPaddingEnd = 0
    private var mHeight = 0

    private var mCallback: TurntableHelper.OnItemOnClickCallback? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attr: AttributeSet?) : this(context, attr, 0)

    constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attr,
        defStyleAttr
    ) {
        val typedArray = context.obtainStyledAttributes(attr, R.styleable.SimpleTurntableView)
        mFontSize = typedArray.getDimension(
                R.styleable.SimpleTurntableView_turntableFontSize,
                context.resources.getDimension(R.dimen.ntt_font_size_24)
        )
        mPaddingEnd = typedArray.getDimensionPixelOffset(
            R.styleable.SimpleTurntableView_turntablePaddingEnd,
            0
        )
        mHeight = typedArray.getDimensionPixelOffset(
            R.styleable.SimpleTurntableView_turntableHeight,
            context.resources.getDimensionPixelOffset(R.dimen.ntt_dp_50)
        )
        typedArray.recycle()
        initAdapter()
    }


    /**
     * 初始化适配器
     */
    private fun initAdapter() {
        if (mAdapter == null) {
            mAdapter = STurntableAdapter()
            mAdapter!!.setTextConfig(mFontSize, mPaddingEnd, mHeight)

        }
    }


    /**
     * 设置选中的子项
     * @param item 选中项
     * @param selectedColor 选中项的字体颜色
     */
    fun selectedItem(item: STurntableEntity, selectedColor: Int) {
        mAdapter?.selectedItem(item, selectedColor)
    }

    fun getItem(position: Int) = mAdapter?.getItem(position)

    /**
     * 设置数据
     */
    fun setData(
        data: MutableList<STurntableEntity>?
    ) {
        mTurntableHelper = TurntableHelper(this)
        mTurntableHelper!!.setAdapterData(mAdapter!!)
        mTurntableHelper?.listener = mCallback
        mAdapter?.setNewInstance(data)
    }

    fun getItemCount() = mAdapter?.itemCount ?: 0

    fun setOnItemOnClickCallback(callBack: TurntableHelper.OnItemOnClickCallback?) {
        mCallback = callBack
        mTurntableHelper?.listener = mCallback
    }

    fun scrollToPosition(position: Int) {
        mTurntableHelper?.scrollToPosition(position)
    }

}