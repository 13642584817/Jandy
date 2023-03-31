package com.jandy.jwidget.turntable

import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jandy.jwidget.R
import com.jandy.jwidget.turntable.plugins.ButtonTextView
import com.jandy.jwidget.turntable.plugins.TurnImageView
import com.jandy.jwidget.turntable.plugins.TurnRecyclerView
import com.jandy.jwidget.turntable.plugins.TurnableLayoutManager

open class TurntableHelper : TurnableLayoutManager.IcallbackListener {

    private lateinit var turntableView: TurntableView

    protected lateinit var rv: TurnRecyclerView

    private lateinit var mTurnText: ButtonTextView
    private lateinit var mTurnButton: TurnImageView
    private var maxScrollY = 0
    private var canTurnAngle = 160
    private var lastAngle = 0f
    private lateinit var animation: RotateAnimation
    private var location = IntArray(2)
    private var totalMoveY = 0
    private var touchTurnButton = false

    private var isCreateSuccess = false

    private var isNeedScroll = false
    private var needScrollPosition = 0

    var listener: OnItemOnClickCallback? = null

    var mOnScrollCallback: OnScrollCallback? = null

    var scrollPosition:Int=0//当前滚动的位置


    constructor(turntableView: TurntableView) {
        initView(turntableView)
    }

    fun initView(turntableView: TurntableView) {
        this.turntableView = turntableView
        rv = turntableView.findViewById(R.id.rv_screen)
        mTurnText = turntableView.findViewById(R.id.tv_commit)
        mTurnButton = turntableView.findViewById(R.id.tiv_turn)
        setupRecyclerView()
        mTurnButton.listener = object : TurnImageView.OnTouchEventListener {
            override fun onClick() {
                val layoutManager = rv.layoutManager as TurnableLayoutManager
                val position = layoutManager?.getCurrentSelectPosition() ?: return
                listener?.onItemClick(rv, position)
            }

            override fun onScrollY(dy: Float) {
            }
        }
    }

    fun setAdapterData(adapter: RecyclerView.Adapter<BaseViewHolder>) {
        rv.adapter = adapter
    }

    fun scrollToPosition(position: Int) {
        if (!isCreateSuccess) {
            isNeedScroll = true
            needScrollPosition = position
            return
        }
        try {
            rv.scrollToPosition(position)
            Log.i("TurntableHelper--->", "scrollY:" + rv.y)
            initTurnButtonPosition(position)
        } catch (e: Exception) {
             Log.e("jandy", "超出范围，或者适配器未初始化")
        }
    }

    /**
     * 初始化旋转按钮滑块位置
     */
    private fun initTurnButtonPosition(position: Int) {
        mTurnButton.post(object : Runnable {
            override fun run() {
                totalMoveY = -90 * position
                val total = rv.adapter?.itemCount ?: 0
                lastAngle = -(canTurnAngle.toFloat() / (total - 1) * position)
                val afterAngle = lastAngle
                mTurnButton.clearAnimation()
                animation = RotateAnimation(
                    lastAngle,
                    afterAngle,
                    Animation.RELATIVE_TO_SELF,
                    0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f
                )
                lastAngle = afterAngle
                Log.i(
                    "TurntableHelper--->",
                    "lastAngle:" + lastAngle + "   " + "afterAngle:" + afterAngle
                );
                animation.fillAfter = true
                animation.repeatCount = 0
                mTurnButton.startAnimation(animation)
            }
        })
    }

    private fun setupRecyclerView() {
        rv.layoutManager = TurnableLayoutManager(this)

        rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                onTurnScroll(dy)
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {//已经停止滚动
                    //获取当前选择的位置
                    val layoutManager = rv.layoutManager as TurnableLayoutManager
                    val position = layoutManager?.getCurrentSelectPosition() ?: 0
                    if (scrollPosition!=position || (scrollPosition==0 && position==0)){
                        scrollPosition=position
                        mOnScrollCallback?.onScroll(scrollPosition)
                    }
                }
            }
        })
        rv.listener = object : TurnRecyclerView.OnTouchEventView {
            override fun onTouchEvent(ev: MotionEvent?) {
                if (location[0] == 0 && location[1] == 0) {
                    location = mTurnButton.location
                    return
                }
                if (ev?.action == MotionEvent.ACTION_DOWN) {
                    touchTurnButton = false
                }
                val touchX = ev?.rawX?.toInt() ?: 0
                val touchY = ev?.rawY?.toInt() ?: 0
                if (touchX >= location[0] && touchX <= location[0] + mTurnButton.measuredWidth
                    && touchY >= location[1] && touchY <= location[1] + mTurnButton.measuredHeight
                ) {
                    touchTurnButton = true
                    mTurnButton.dispatchTouchEvent(ev)
                    mTurnText.dispatchTouchEvent(ev)
                } else {
                    if (!touchTurnButton) return
                    touchTurnButton = false
//                    ev?.action = MotionEvent.ACTION_UP
                    mTurnButton.dispatchTouchEvent(ev)
                    mTurnText.dispatchTouchEvent(ev)
                }
            }

        }

    }

    private fun onTurnScroll(dy: Int) {
        if (maxScrollY == 0) return
        totalMoveY -= dy
        if (Math.abs(totalMoveY) > maxScrollY) {
            val maxScroll = maxScrollY
            totalMoveY = if (totalMoveY < 0) -maxScroll else maxScroll
        }
        var afterAngle = totalMoveY.toFloat() / maxScrollY * canTurnAngle
        if (lastAngle == afterAngle) return
        mTurnButton.clearAnimation()
        animation = RotateAnimation(
            lastAngle,
            afterAngle,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        lastAngle = afterAngle
        animation.fillAfter = true
        animation.repeatCount = 0
        mTurnButton.startAnimation(animation)
    }

    override fun createViewSuccess() {
        isCreateSuccess = true
        //加载完需要做的事
        if (isNeedScroll) {
            isNeedScroll = false
            scrollToPosition(needScrollPosition)
        }
    }

    override fun getTotalScrollY(scrollY: Int) {
        maxScrollY = scrollY
    }

    interface OnItemOnClickCallback {
        fun onItemClick(
            view: View,
            position: Int
        )
    }

    interface OnScrollCallback {
        fun onScroll(
            position: Int
        )
    }

}