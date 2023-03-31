package com.jandy.jwidget.globalscroll.layoutmanager

import android.graphics.Rect
import android.util.Log
import android.util.SparseArray
import android.util.SparseBooleanArray
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ScreenUtils
import com.jandy.jwidget.globalscroll.plugins.GenerateUtils
import java.lang.Math.pow
import java.lang.StrictMath.abs
import kotlin.math.sqrt

class GlobalScrollLayoutManager_75 : RecyclerView.LayoutManager() {

    private lateinit var boxArrange: IntArray
    private var saveLocationRects = SparseArray<Rect>()  //保持不变
    private var attachedItems = SparseBooleanArray()

    private lateinit var adapter: RecyclerView.Adapter<*>
    private var recycler: RecyclerView.Recycler? = null

    private var maxScrollY = 0
    private var maxScrollX = 0
    private var lastDy = 0
    private var lastDx = 0
    private var needSnap = false

    private var displayScrollX = 0
    private var displayScrollY = 0

    private var wallHeight = 0
    private var wallWidth = 0

    private var maxColumn = 0
    private var maxBothSize = 0

    private var itemWidth = 0
    private var itemHeight = 0

    private var radiusPointX = 0
    private var radiusPointY = 0
    private val minRadiusArrange = 0

    private val breakRadiusArray = SparseArray<Int>()
    private val scaleArray = SparseArray<Float>()
    private var differScale = 0.13f
    private val minScale = 0.1f
    private var preview = false
    private var dataIsChanging = true

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
        adapter = newAdapter!!
    }


    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State?) {
        this.recycler = recycler
        if (preview) return
        preview = true
        dataIsChanging = true
        addRectsAndcomputeMaxScoll()
        //wallwidth、wallheight 已初始化
        iniData()
        // 先回收放到缓存，后面会再次统一layout
        detachAndScrapAttachedViews(recycler)
        addViewAndlocationView(recycler)
        dataIsChanging = false
    }

    private fun iniData() {
        //居中显示
        var defaultX = wallWidth
        var defaultY = wallHeight
        try {
            val windowWidth = ScreenUtils.getAppScreenWidth()
            val windowHeight = ScreenUtils.getAppScreenHeight()
            if (boxArrange.isNotEmpty()) {
                defaultX += (itemWidth * (boxArrange.maxOrNull()!! / 2f)).toInt() - windowWidth / 2
                defaultY += (itemHeight * (boxArrange.size.toFloat() / 2)).toInt() - windowHeight / 2
            }
        } catch (e: Exception) {

        }
        displayScrollX = defaultX
        displayScrollY = defaultY

        radiusPointX = width / 2
        radiusPointY = height / 2

    }

    private fun addRectsAndcomputeMaxScoll() {
        val itemCount = itemCount
        if (itemCount == 0) {
            maxScrollY = 0
            maxScrollX = 0
            return
        }

        boxArrange = GenerateUtils.generateNums(itemCount)
        saveLocationRects.clear()
        attachedItems.clear()
        breakRadiusArray.clear()
        scaleArray.clear()

        val itemView = recycler!!.getViewForPosition(0)
        addView(itemView)
        measureChildWithMargins(
            itemView,
            View.MeasureSpec.UNSPECIFIED,
            View.MeasureSpec.UNSPECIFIED
        )
        itemHeight = getDecoratedMeasuredHeight(itemView)
        itemWidth = getDecoratedMeasuredWidth(itemView)


        val size = getArraySize()
        var bei = 1
        var zoomHeight = 0
        for (i in 0 until size - 1) {
            if (i != 0) {
                bei = 2
            }
            val breakRadius = i * itemHeight
            val scale = 1f - (differScale * i)
            zoomHeight += (bei * scale * (itemHeight / 2f)).toInt()
            breakRadiusArray.put(i, breakRadius)
            scaleArray.put(i, scale)
        }
        val remainScale = (height / 2 - zoomHeight).toFloat() / itemHeight
        scaleArray.put(size - 1, remainScale)
        breakRadiusArray.put(size - 1, breakRadiusArray[size - 2] + itemHeight)
        breakRadiusArray.put(size, getMaxRadius())
        scaleArray.put(size, minScale)

        //Log.e("jandy", "scaleArray = $scaleArray breakRadiusArray = $breakRadiusArray")


        wallHeight = height / 4
        wallWidth = width / 6

        maxColumn = boxArrange.maxOrNull()!!
        var count = 0
        var hopeColumn = 0
        var centerLine = boxArrange.size / 2
        for (i in boxArrange.indices) {
            val column = boxArrange[i]
            hopeColumn = maxColumn - Math.abs(i - centerLine)
            val nextStartX = (maxColumn - hopeColumn) * itemWidth / 2 + wallWidth
            val nextTop = i * itemHeight + wallHeight
            for (j in 0 until column) {
                // 2. 组装Rect并保存
                val rect = Rect()
                rect.left = nextStartX + j * itemWidth
                rect.top = nextTop
                rect.right = rect.left + itemWidth
                rect.bottom = rect.top + itemHeight
                saveLocationRects.put(count, rect)
                attachedItems.put(count, false)
                count++
            }
        }

        computeMaxScroll()

        maxBothSize = getMaxHorizontalBothSize(itemWidth)
    }

    /**
     * 计算可滑动的最大值
     */
    private fun computeMaxScroll() {
        maxScrollY =
            saveLocationRects[saveLocationRects.size() - 1].bottom + wallHeight - height
        maxScrollX = getLastRightRect().right + wallWidth - width
        if (maxScrollY < 0) {
            maxScrollY = 0
        }
        if (maxScrollX < 0) {
            maxScrollX = 0
        }
    }

    private fun getMaxRadius(): Int {
        return height / 2 + ((1f - minScale) * itemHeight / 2).toInt()
    }

    private fun getArraySize(): Int {
        return (height / 2 - itemHeight / 2) / itemHeight + 2
    }

    private fun getMaxHorizontalBothSize(itemWidth: Int): Int {
        return maxScrollX / 2 / itemWidth
    }

    private fun getLastRightRect(): Rect {
        val maxColumn = boxArrange.maxOrNull()!!
        var index = -1
        for (i in 0 until boxArrange.size!!) {
            val column = boxArrange[i]
            index += column
            if (column == maxColumn) {
                break
            }
        }
        if (index == -1) {
            index = 0
        }

        return saveLocationRects[index]
    }


    /**
     * 初始化的时候，layout子View
     */
    private fun addViewAndlocationView(recycler: RecyclerView.Recycler) {
        if (itemCount != saveLocationRects.size()) return
        val displayRect =
            Rect(displayScrollX, displayScrollY, displayScrollX + width, displayScrollY + height)
        try {
            for (i in 0 until saveLocationRects.size()) {
                val thisRect = saveLocationRects[i]
                if (Rect.intersects(displayRect, thisRect)) {
                    attachedItems.put(i, true)
                    val childView = recycler.getViewForPosition(i)
                    measureChildWithMargins(
                        childView,
                        View.MeasureSpec.UNSPECIFIED,
                        View.MeasureSpec.UNSPECIFIED
                    )
                    childView.pivotX = childView.measuredWidth / 2f
                    childView.pivotY = childView.measuredHeight / 2f
                    addView(childView)
                    layoutItem(childView, saveLocationRects[i])

                }
            }
        } catch (e: Exception) {
            Log.d("jandy","  addViewAndlocationView " + e?.toString())
        }
    }

    private fun layoutItem(childView: View, thisRect: Rect) {
        var layoutTop = 0
        var layoutLeft = 0
        var layoutBottom = 0
        var layoutRight = 0
        layoutTop = thisRect.top - displayScrollY
        layoutBottom = thisRect.bottom - displayScrollY
        layoutLeft = thisRect.left - displayScrollX
        layoutRight = thisRect.right - displayScrollX
        var layoutCenterX = (layoutLeft + layoutRight) / 2
        var layoutCenterY = (layoutTop + layoutBottom) / 2
        val disRadius =
            sqrt(
                pow(
                    layoutCenterX.toDouble() - radiusPointX,
                    2.0
                ) + pow(layoutCenterY.toDouble() - radiusPointY, 2.0)
            ).toInt()

        var scale = getScaleFromRadiusDis(disRadius)

        val layoutVerticalY = abs(layoutCenterY - radiusPointY)
        var layoutScrollY = 0  //大于0 下移
        layoutScrollY += if (abs(layoutCenterY - radiusPointY) < breakRadiusArray[1] / 2) 0
        else {
            getScrollYFromDisRadius(
                layoutVerticalY,
                disRadius
            ) + ((1f - scale) * itemHeight / 2).toInt()
        }

        layoutScrollY = if (radiusPointY - layoutCenterY > minRadiusArrange) {
            //VIEW_ABOVE
            layoutScrollY
        } else if (layoutCenterY - radiusPointY > minRadiusArrange) {
            //VIEW_BELOW
            -layoutScrollY
        } else {
            //VIEW_CENTER
            0
        }
        var layoutScrollTop = layoutTop + layoutScrollY
        var layoutScrollBottom = layoutBottom + layoutScrollY

        //左右边界判断
        val rect = Rect(layoutLeft, 0, layoutRight, 0)
        var layoutScrollX = 0 //大于0右移
        val scaleRadius = (scale * itemWidth / 2f).toInt()
        val scaleWidth = 2 * scaleRadius
        if (intersectVerticalTwoPoint(0, rect)) { //最左边
            val disLeft = rect.centerX() - scaleRadius
            if (disLeft > 0) {
            } else if (rect.right >= scaleWidth) {
                layoutScrollX = Math.abs(disLeft)
            } else {
                val resetScale = rect.right.toFloat() / itemWidth
                val resetRadius = rect.right / 2
                if (resetRadius < scaleRadius) {
                    scale = resetScale
                    layoutScrollX = Math.abs(rect.centerX() - resetRadius)
                }
            }
        } else if (intersectVerticalTwoPoint(width, rect)) {//最右边
            val disRight = width - rect.centerX() - scaleRadius
            if (disRight > 0) {

            } else if (width - rect.left >= scaleWidth) {
                layoutScrollX = -Math.abs(disRight)
            } else {
                val resetScale = (width - rect.left).toFloat() / itemWidth
                val resetRadius = (width - rect.left) / 2
                if (resetRadius < scaleRadius) {
                    scale = resetScale
                    layoutScrollX = -Math.abs(rect.centerX() - width + resetRadius)
                }
            }
        }
        val layoutScrollLeft = layoutLeft + layoutScrollX
        val layoutScrollRight = layoutRight + layoutScrollX

        childView.scaleX = scale
        childView.scaleY = scale

        layoutDecorated(
            childView,
            layoutScrollLeft,
            layoutScrollTop,
            layoutScrollRight,
            layoutScrollBottom
        )
    }

    private fun getScrollYFromDisRadius(layoutVertical: Int, disRadius: Int): Int {
        var layoutScrollY = 0
        val moveDis = itemHeight / (2 * breakRadiusArray.size())
        val startIndex = 1  //必须>=0
        for (i in startIndex until breakRadiusArray.size()) {
            val curBreakRadius = breakRadiusArray[i]
            if (i == startIndex && curBreakRadius - layoutVertical >= -minRadiusArrange) {
                break
            } else if (i + 1 < breakRadiusArray.size() && curBreakRadius - layoutVertical >= -minRadiusArrange) {
                layoutScrollY = getNextScrollY(i, startIndex, layoutVertical)
                break
            } else if (i + 1 >= breakRadiusArray.size()) {
                val lastBreakRadius = breakRadiusArray[i - 1]
                var moveHeight =
                    (moveDis * ((disRadius - lastBreakRadius - minRadiusArrange).toFloat() / (curBreakRadius - lastBreakRadius - minRadiusArrange))).toInt()
                if (moveHeight > moveDis) {
                    moveHeight = moveDis
                }
                layoutScrollY = getNextScrollY(i, startIndex, disRadius) + moveHeight
                break
            }
        }
        return layoutScrollY
    }

    private fun getNextScrollY(start: Int, downTo: Int, disRadius: Int): Int {
        var scrollY = 0
        var bei = 2
        if (disRadius <= 0) return scrollY
        for (i in start downTo (downTo + 1)) {
            if (start == downTo + 1)
                bei = 1
            val disR = disRadius - (start - i + 1) * itemHeight
            scrollY += bei * getNextScrollY(disR)
        }
        return scrollY
    }

    private fun getNextScrollY(disRadius: Int): Int {
        val disOneScale = getScaleFromRadiusDis(disRadius)
        return ((1f - disOneScale) * itemHeight / 2).toInt()
    }

    private fun getScaleFromRadiusDis(disRadius: Int): Float {
        var scaleA = 0f
        val centerArrange = minRadiusArrange
        for (i in 0 until breakRadiusArray.size()) {
            val curBreakRadius = breakRadiusArray[i]
            if (i != 0 && i + 1 < breakRadiusArray.size() && curBreakRadius - disRadius > centerArrange) {
                val lastBreakRadius = breakRadiusArray[i - 1]
                val curScale = scaleArray[i]
                val lastScale = scaleArray[i - 1]

                val dis = disRadius - lastBreakRadius - centerArrange
                val total = curBreakRadius - lastBreakRadius - 2 * centerArrange
                val scale = lastScale - (lastScale - curScale) * dis.toFloat() / total
                scaleA = scale
                break
            } else if (abs(disRadius - breakRadiusArray[i]) <= centerArrange) {
                scaleA = scaleArray[i]
                break
            } else if (i + 1 >= breakRadiusArray.size()) {
                if (curBreakRadius - disRadius > 0) {
                    val lastBreakRadius = breakRadiusArray[i - 1]
                    val curScale = scaleArray[i]
                    val lastScale = scaleArray[i - 1]

                    val dis = disRadius - lastBreakRadius
                    val total = curBreakRadius - lastBreakRadius
                    val scale = lastScale - (lastScale - curScale) * dis.toFloat() / total
                    scaleA = scale
                } else {
                    scaleA = minScale
                }
                break
            }
        }

        return scaleA
    }

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        if (dataIsChanging || dx == 0) return 0
        if (childCount == 0 || itemCount != saveLocationRects.size()) {
            preview = false
            dataIsChanging = true
            onLayoutChildren(recycler, state)
            return 0
        }
        var travel = dx
        if (dx + displayScrollX < 0) {
            travel = -displayScrollX
        } else if (dx + displayScrollX > maxScrollX) {
            travel = maxScrollX - displayScrollX
        }
        displayScrollX += travel //累计偏移量
        lastDx = dx
        if (childCount > 0) {
            layoutItemsOnScroll()
        }
        return travel
    }


    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        if (dataIsChanging || dy == 0) return 0
        if (childCount == 0 || itemCount != saveLocationRects.size()) {
            preview = false
            dataIsChanging = true
            onLayoutChildren(recycler, state)
            return 0
        }
        var travel = dy
        if (dy + displayScrollY < 0) {
            travel = -displayScrollY
        } else if (dy + displayScrollY > maxScrollY) {
            travel = maxScrollY - displayScrollY
        }
        displayScrollY += travel //累计偏移量
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
        val displayRect =
            Rect(displayScrollX, displayScrollY, displayScrollX + width, displayScrollY + height)
        var firstVerticalLine = -1
        var lastVerticalLine = -1

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
                if (lastVerticalLine < 0) {
                    lastVerticalLine = getLineFromPosition(position)
                }
                firstVerticalLine = if (firstVerticalLine < 0) {
                    getLineFromPosition(position)
                } else {
                    Math.min(firstVerticalLine, getLineFromPosition(position))
                }
                layoutItem(child, thisRect)  //更新Item位置
            }
        }

        // 2. 复用View处理
        // 往前搜索复用
        if (firstVerticalLine != -1) {
            val firstVerticalPosition =
                getPerLineStartPosition(firstVerticalLine) + boxArrange[firstVerticalLine] - 1
            val firstArrayCount = getFirstTwoLineCount(firstVerticalLine)
            var firstposition = 0
            for (i in 0 until firstArrayCount) {
                firstposition = firstVerticalPosition - i
                if (Rect.intersects(displayRect, saveLocationRects[firstposition]) &&
                    !attachedItems[firstposition]
                ) {
                    reuseItemOnSroll(firstposition, true)
                }
            }
        }

        if (lastVerticalLine != -1) {
            // 往后搜索复用
            val lastVerticalPosition = getPerLineStartPosition(lastVerticalLine)
            val lastArrayCount = getLastTwoLineCount(lastVerticalLine)
            var lastposition = 0
            for (i in 0 until lastArrayCount) {
                lastposition = lastVerticalPosition + i
                if (Rect.intersects(displayRect, saveLocationRects[lastposition]) &&
                    !attachedItems[lastposition]
                ) {
                    reuseItemOnSroll(lastposition, false)
                }
            }
        }


        //左右搜素复用
        if (firstVerticalLine == -1 || lastVerticalLine == -1)
            return
        val startLine = firstVerticalLine + 1
        for (i in startLine until lastVerticalLine) {
            val column = boxArrange[i]
            //左搜索
            for (j in 0 until column) {
                val position = getPerLineStartPosition(i) + j
                val isShow = attachedItems[position]
                if (isShow) break
                val leftRect = saveLocationRects[position]
                if (intersectVerticalTwoPoint(displayScrollX, leftRect)) {
                    reuseItemOnSroll(position, true)
                }
            }
            //右搜索
            for (j in column - 1 downTo 0) {
                val position = getPerLineStartPosition(i) + j
                val isShow = attachedItems[position]
                if (isShow) break
                val rightRect = saveLocationRects[position]
                if (intersectVerticalTwoPoint(displayScrollX + width, rightRect)) {
                    reuseItemOnSroll(position, true)
                }
            }
        }
    }

    private fun intersectVerticalTwoPoint(pointX: Int, rect: Rect): Boolean {
        return pointX > rect.left && pointX < rect.right
    }

    private fun intersectHorizontalTwoPoint(pointY: Int, rect: Rect): Boolean {
        return pointY > rect.bottom && pointY < rect.top
    }

    /**
     * 复用position对应的View
     */
    private fun reuseItemOnSroll(position: Int, addViewFromTop: Boolean) {
        val scrap = recycler!!.getViewForPosition(position)
        measureChildWithMargins(scrap, 0, 0)
        scrap.pivotX = scrap.measuredWidth / 2f
        scrap.pivotY = scrap.measuredHeight / 2f
        if (addViewFromTop) {
            addView(scrap, 0)
        } else {
            addView(scrap)
        }
        // 将这个Item布局出来
        layoutItem(scrap, saveLocationRects[position])
        attachedItems.put(position, true)
    }

    private fun getLineFromPosition(position: Int): Int {
        var perLinePosition = saveLocationRects.size()
        for (i in boxArrange.size - 1 downTo 0) {
            val colum = boxArrange[i]
            perLinePosition -= colum
            if (perLinePosition <= position) {
                return i
            }
        }
        return 0
    }

    private fun getPerLineStartPosition(lineIndex: Int): Int {
        if (lineIndex == 0) return 0
        var perLineStartPosition = 0
        for (i in boxArrange.indices) {
            if (i >= lineIndex) {
                break
            }
            val column = boxArrange[i]
            perLineStartPosition += column
        }
        return perLineStartPosition
    }

    private fun getLastTwoLineCount(lineIndex: Int): Int {
        var index = 0
        val total = 2
        var count = 0
        for (i in lineIndex until boxArrange.size) {
            if (index >= total) break
            val column = boxArrange[i]
            count += column
            index++
        }
        return count
    }

    private fun getFirstTwoLineCount(lineIndex: Int): Int {
        var index = 0
        val total = 2
        var count = 0
        for (i in lineIndex downTo 0) {
            if (index >= total) break
            val column = boxArrange[i]
            count += column
            index++
        }
        return count
    }

    override fun canScrollHorizontally(): Boolean {
        return true
    }

    override fun canScrollVertically(): Boolean {
        return true
    }

    override fun onScrollStateChanged(state: Int) {
        if (state == RecyclerView.SCROLL_STATE_DRAGGING) {
            needSnap = true
        }
        super.onScrollStateChanged(state)
    }

    override fun onAttachedToWindow(view: RecyclerView?) {
        super.onAttachedToWindow(view)
    }

    fun reloadView() {
        preview = false
    }

    fun review() {
        if (dataIsChanging) return
        reloadView()
        onLayoutChildren(recycler!!, null)
    }
}