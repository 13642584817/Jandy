package com.jandy.jwidget.turntable.plugins

import android.graphics.Rect
import android.util.ArrayMap
import android.util.SparseArray
import android.util.SparseBooleanArray
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import java.lang.Math.*
import kotlin.math.min
import kotlin.math.sqrt

class TurnableLayoutManager : RecyclerView.LayoutManager {


    private var scroll = 0
    private var saveLocationRects = SparseArray<Rect>()  //保持不变
    private var attachedItems = SparseBooleanArray()
    private var viewTypeHeightMap = ArrayMap<Int, Int>()

    private var needSnap = false
    private var lastDy = 0
    private var maxScroll = -1
    private var adapter: RecyclerView.Adapter<*>? = null
    private var recycler: Recycler? = null
    private var recyclerState: RecyclerView.State? = null

    private var radius = 0
    private var roundCenterY = 0

    //向左偏移
    private var turnMinMoveLeft = 100

    private var minValueAngle = 60
    private var breakPointAngle = 71  //breakpointvalue-maxvalue alpha 从0.5-1.0f
    private var breakPointScope = 2  //[breakpoint-scope,breakpoint+scope]
    private var breakPointMinWidth = 0
    private var breakPointMaxWidth = 0
    private var maxValueAngle = 85  //maxvalue-90是 alpha 1.0f
    private var minValueAngleWidth = 0
    private var maxValueAngleWidth = 0
    private var totalMove = 0

    private val minAlpha = 0.2f
    private val breakPointAlpha = 0.5f
    private val minScale = 0.75f
    private var turnArray = SparseArray<Int>()
    private var listener: IcallbackListener? = null

    private var selectedPosition = 0

    private var preview = false
    private var dataIsChanging = true

    constructor(callback: IcallbackListener) {
        listener = callback
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }


    override fun onAdapterChanged(
        oldAdapter: RecyclerView.Adapter<*>?,
        newAdapter: RecyclerView.Adapter<*>?
    ) {
        super.onAdapterChanged(oldAdapter, newAdapter)
        adapter = newAdapter
    }


    override fun onLayoutChildren(recycler: Recycler?, state: RecyclerView.State) {
        this.recycler = recycler // 二话不说，先把recycler保存了
        this.recyclerState = state
        if (preview) return
        preview = true
        dataIsChanging = true

        initData()

        addRectsAndcomputeMaxScoll()
        listener?.getTotalScrollY(maxScroll)
        // 先回收放到缓存，后面会再次统一layout
        detachAndScrapAttachedViews(recycler!!)
        addViewAndlocationView(recycler)
        listener?.createViewSuccess()
        dataIsChanging = false
    }

    private fun initData() {
        val disY = -60
        turnArray.apply {
            append(0, disY)
            append(1, height - disY)
        }
        radius = (height - disY * 2) / 2
        roundCenterY = turnArray[0] + radius
        breakPointMinWidth =
            (radius * sin((breakPointAngle - breakPointScope) / 180.00 * PI)).toInt()
        breakPointMaxWidth =
            (radius * sin((breakPointAngle + breakPointScope) / 180.00 * PI)).toInt()
        minValueAngleWidth = (radius * sin(minValueAngle / 180.00 * PI)).toInt()
        maxValueAngleWidth = (radius * sin(maxValueAngle / 180.00 * PI)).toInt()
        totalMove = maxValueAngleWidth - minValueAngleWidth

    }

    private fun addRectsAndcomputeMaxScoll() {
        saveLocationRects.clear()
        attachedItems.clear()
        var tempPosition = getRealHeight() / 2
        val itemCount = itemCount
        for (i in 0 until itemCount) {
            // 1. 先计算出itemWidth和itemHeight
            val viewType = adapter!!.getItemViewType(i)
            var itemHeight: Int
            if (viewTypeHeightMap.containsKey(viewType)) {
                itemHeight = viewTypeHeightMap[viewType]!!
            } else {
                val itemView = recycler!!.getViewForPosition(i)
                addView(itemView)
                measureChildWithMargins(
                    itemView,
                    View.MeasureSpec.UNSPECIFIED,
                    View.MeasureSpec.UNSPECIFIED
                )
                itemHeight = getDecoratedMeasuredHeight(itemView)
                viewTypeHeightMap[viewType] = itemHeight
            }
            if (i == 0) {
                tempPosition -= itemHeight / 2
            }

            // 2. 组装Rect并保存
            val rect = Rect()
            rect.left = paddingLeft
            rect.top = tempPosition
            rect.right = width - paddingRight
            rect.bottom = rect.top + itemHeight
            saveLocationRects.put(i, rect)
            attachedItems.put(i, false)
            tempPosition += itemHeight
        }
        if (itemCount == 0) {
            maxScroll = 0
        } else {
            computeMaxScroll()
        }
    }

    /**
     * 计算可滑动的最大值
     */
    private fun computeMaxScroll() {
        maxScroll =
            (saveLocationRects[saveLocationRects.size() - 1].bottom + saveLocationRects[saveLocationRects.size() - 1].top) / 2 - getRealHeight() / 2
        if (maxScroll < 0) {
            maxScroll = 0
            return
        }
    }

    private fun getRealHeight(): Int {
        return paddingTop + height - paddingBottom
    }

    /**
     * 初始化的时候，layout子View
     */
    private fun addViewAndlocationView(recycler: Recycler) {
        val itemCount = recyclerState?.itemCount ?: return
        val displayRect = Rect(0, paddingTop + scroll, width, height - paddingBottom + scroll)
        var isIntersects = false
        var isUp = true
        for (i in 0 until itemCount) {
            val thisRect = saveLocationRects[i]
            if (!Rect.intersects(displayRect, thisRect)) {
                if (isIntersects) break
                continue
            }
            isIntersects = true
            attachedItems.put(i, true)
            val childView = recycler.getViewForPosition(i)
            measureChildWithMargins(
                childView,
                View.MeasureSpec.UNSPECIFIED,
                View.MeasureSpec.UNSPECIFIED
            )

            addView(childView)
            if (layoutItem(childView, thisRect, if (isUp) 1 else 3)) {
                selectedPosition = i
                isUp = false
            }
        }
    }

    //1上，2中，3下
    private fun layoutItem(childView: View, thisRect: Rect, location: Int): Boolean {
        var layoutTop = 0
        var layoutBottom = 0
        var layoutRight = 0
        val centerY = (thisRect.top + thisRect.bottom) / 2
        layoutRight = competeleWidthFromTurn(centerY)
        layoutTop = thisRect.top - scroll
        layoutBottom = thisRect.bottom - scroll
        var isSelect = false
        var lc = location

        if (layoutRight == 0) {
            childView.scaleX = minScale
            childView.scaleY = minScale
            childView.alpha = minAlpha
        } else if (layoutRight <= minValueAngleWidth) {
            childView.scaleX = minScale
            childView.scaleY = minScale
            childView.alpha = minAlpha
        } else if (layoutRight >= maxValueAngleWidth) {
            childView.scaleX = 1f
            childView.scaleY = 1f
            childView.alpha = 1f
            lc = 2
            isSelect = true
        } else {
            val moveDis = layoutRight - minValueAngleWidth
            val changeScale = (1f - minScale) * (moveDis.toFloat() / totalMove) + minScale
            childView.scaleX = changeScale
            childView.scaleY = changeScale
            var changeAlpha = breakPointAlpha
            if (layoutRight in (minValueAngleWidth + 1) until breakPointMinWidth) {
                val breakTotalMove = breakPointMinWidth - minValueAngleWidth
                val breakMoveDis = layoutRight - minValueAngleWidth
                changeAlpha =
                    (breakPointAlpha - minAlpha) * (breakMoveDis.toFloat() / breakTotalMove) + minAlpha
            } else if (layoutRight in breakPointMinWidth until breakPointMaxWidth + 1) {
                changeAlpha = breakPointAlpha
            } else if (layoutRight in breakPointMaxWidth + 1 until maxValueAngleWidth) {
                val breakTotalMove = maxValueAngleWidth - breakPointMaxWidth
                val breakMoveDis = layoutRight - breakPointMaxWidth
                changeAlpha =
                    (1f - breakPointAlpha) * (breakMoveDis.toFloat() / breakTotalMove) + breakPointAlpha
            }
            childView.alpha = changeAlpha
        }

        childView.apply {
            val itemHeight = thisRect.height()
            val itemWidth = thisRect.width()
            when (lc) {
                1 -> {
                    pivotX = itemWidth / 2f
                    pivotY = 0f
                }
                2 -> {
                    pivotX = itemWidth / 2f
                    pivotY = itemHeight / 2f
                }
                3 -> {
                    pivotX = itemWidth / 2f
                    pivotY = itemHeight.toFloat()
                }
            }
            scrollX = layoutRight - turnMinMoveLeft
        }
        layoutDecorated(childView, 0, layoutTop, thisRect.right, layoutBottom)
        return isSelect
    }

    /**
     *
     * 计算转盘范围
     */
    private fun isInTurn(locY: Int): Boolean {
        if (locY >= turnArray[0] && locY <= turnArray[1]) {
            return true
        }
        return false
    }

    /**
     *
     * 计算距离转盘的宽度
     */
    private fun competeleWidthFromTurn(locY: Int): Int {
        var displayY = locY - scroll
        if (!isInTurn(displayY)) return 0
        var disY = abs(roundCenterY - displayY)
        return sqrt(
            (Math.pow(radius.toDouble(), 2.0) - Math.pow(
                disY.toDouble(),
                2.0
            )).toDouble()
        ).toInt()
    }

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: Recycler?,
        state: RecyclerView.State?
    ): Int {
        return super.scrollHorizontallyBy(dx, recycler, state)
    }

    override fun scrollVerticallyBy(dy: Int, recycler: Recycler?, state: RecyclerView.State): Int {
        if (dataIsChanging) return 0
//        //解决快速滑动导致失真的问题
        if (childCount == 0) {
            preview = false;
            onLayoutChildren(recycler, state)
            return 0
        }
        if (dy == 0) {
            return 0
        }
        var travel = dy
        if (dy + scroll < 0) {
            travel = -scroll
        } else if (dy + scroll > maxScroll) {
            travel = maxScroll - scroll
        }
        scroll += travel //累计偏移量
        lastDy = dy
        if (childCount > 0) {
            layoutItemsOnScroll()
        }
        return travel
    }

    /**
     * 初始化的时候，layout子View
     */
    private fun layoutItemsOnScroll() {
        val childCount = childCount
        // 1. 已经在屏幕上显示的child
        val itemCount = recyclerState?.itemCount ?: return
        val displayRect = Rect(0, scroll, width, height + scroll)
        var firstVisiblePosition = -1
        var lastVisiblePosition = -1
        var isDown = true
        for (i in childCount - 1 downTo 0) {
            val child = getChildAt(i) ?: continue
            val position = getPosition(child)
            val thisRect = saveLocationRects[position]
            if (!Rect.intersects(displayRect, thisRect)) {
                // 回收滑出屏幕的View
                removeAndRecycleView(child, recycler!!)
                attachedItems.put(position, false)
            } else {
                // Item还在显示区域内，更新滑动后Item的位置
                if (lastVisiblePosition < 0) {
                    lastVisiblePosition = position
                }
                firstVisiblePosition = if (firstVisiblePosition < 0) {
                    position
                } else {
                    min(firstVisiblePosition, position)
                }
                if (layoutItem(child, thisRect, if (isDown) 3 else 1)) {
                    selectedPosition = position
                    isDown = false
                }
            }
        }
        // 2. 复用View处理
        if (firstVisiblePosition > 0) {
            // 往前搜索复用
            for (i in firstVisiblePosition - 1 downTo 0) {
                if (Rect.intersects(displayRect, saveLocationRects[i]) &&
                    !attachedItems.get(i)
                ) {
                    reuseItemOnSroll(i, true);
                } else {
                    break;
                }
            }
        }
        // 往后搜索复用
        if (lastVisiblePosition + 1 < itemCount) {
            for (i in lastVisiblePosition + 1 until itemCount) {
                if (Rect.intersects(displayRect, saveLocationRects[i]) &&
                    !attachedItems[i]
                ) {
                    reuseItemOnSroll(i, false)
                } else {
                    break
                }
            }
        }
    }

    /**
     * 复用position对应的View
     */
    private fun reuseItemOnSroll(position: Int, addViewFromTop: Boolean) {
        val scrap = recycler!!.getViewForPosition(position)
        measureChildWithMargins(scrap, 0, 0)
        if (addViewFromTop) {
            addView(scrap, 0)
        } else {
            addView(scrap)
        }
        // 将这个Item布局出来
        layoutItem(scrap, saveLocationRects[position], if (addViewFromTop) 1 else 3)
        attachedItems.put(position, true)
    }


    override fun canScrollVertically(): Boolean {
        return true
    }

    override fun canScrollHorizontally(): Boolean {
        return false
    }


    override fun onAttachedToWindow(view: RecyclerView) {
        super.onAttachedToWindow(view)
        if (view.onFlingListener == null)
            StartSnapHelper().attachToRecyclerView(view)
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            needSnap = true
        }
    }

    fun getCurrentSelectPosition(): Int {
        return selectedPosition
    }

    fun getSnapHeight(): Int {
        if (!needSnap) {
            return 0
        }
        needSnap = false
        return getFixIndex()
    }

    private fun getFixIndex(): Int {
        val dis = 1
        val maxMoveAngle = 0.25f
        val realHeight = paddingTop + height - paddingBottom
        val displayRect =
            Rect(0, scroll + realHeight / 2 - dis, width, scroll + realHeight / 2 + dis)
        val displayCenterY = (displayRect.top + displayRect.bottom) / 2
        for (position in childCount - 1 downTo 0) {
            val child = getChildAt(position) ?: continue
            val i = getPosition(child)
            val itemRect = saveLocationRects[i]
            if (!displayRect.intersect(itemRect)) continue
            val thisCenterY = (itemRect.top + itemRect.bottom) / 2
            if (lastDy > 0) {
                // scroll变大，属于列表往下走，往下找下一个为snapView
                if (thisCenterY > displayCenterY) {
                    return thisCenterY - displayCenterY //大于0，往上
                }
                val thisMaxMoveHeight = (itemRect.height() * maxMoveAngle).toInt()
                val disY = displayCenterY - thisCenterY
                if (disY < thisMaxMoveHeight) {
                    return thisCenterY - displayCenterY  //小于0，往下
                }
                if (i >= saveLocationRects.size() - 1) {
                    return thisCenterY - displayCenterY  //小于0，往下
                }
                return itemRect.height() - (displayCenterY - thisCenterY)  //大于零上滑
            }
            if (displayCenterY > thisCenterY) {
                return thisCenterY - displayCenterY
            }
            val thisMaxMoveHeight = (itemRect.height() * maxMoveAngle).toInt()
            val disY = thisCenterY - displayCenterY
            if (disY < thisMaxMoveHeight) {
                return thisCenterY - displayCenterY
            }
            if (i - 1 < 0) {
                return thisCenterY - displayCenterY
            }
            return (thisCenterY - displayCenterY) - itemRect.height()
        }
        return 0
    }

    fun findSnapView(): View? {
        return if (childCount > 0) {
            getChildAt(0)
        } else null
    }

    override fun scrollToPosition(position: Int) {
        if (saveLocationRects.size() == 0 || selectedPosition == position) return
        if (!preview) return
        preview = false
        var moveScroll = 0
        try {
            val displayHeight = height - paddingBottom - paddingTop
            moveScroll = saveLocationRects[position].centerY() - displayHeight / 2
        } catch (e: Exception) {
        }
        if (moveScroll <= 0) return
        scroll = moveScroll
        detachAndScrapAttachedViews(recycler!!)
        onLayoutChildren(recycler, recyclerState!!)
    }

    interface IcallbackListener {
        fun createViewSuccess()

        fun getTotalScrollY(scrollY: Int)
    }
}