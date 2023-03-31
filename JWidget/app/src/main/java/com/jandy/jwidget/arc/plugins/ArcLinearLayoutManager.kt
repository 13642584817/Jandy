package com.jandy.jwidget.arc.plugins

import android.graphics.Rect
import android.util.ArrayMap
import android.util.SparseArray
import android.util.SparseBooleanArray
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import java.lang.Math.abs
import kotlin.math.min

class ArcLinearLayoutManager : RecyclerView.LayoutManager() {

	private val TAG = this.javaClass.simpleName
	private var scroll = 0
	private var saveLocationRects = SparseArray<Rect>()  //保持不变
	private var attachedItems = SparseBooleanArray()
	private var viewTypeHeightMap = ArrayMap<Int, Int>()
	private var needSnap = false
	private var lastDy = 0
	private var maxScroll = -1
	private var adapter : RecyclerView.Adapter<*>? = null
	private var recycler : RecyclerView.Recycler? = null
	private var recyclerState : RecyclerView.State? = null
	private val maxScale = 1f
	private val oneChangeScale = 0.03f
	private val twoChangeScale = 0.17f
	private val minScale = 1f-oneChangeScale-twoChangeScale
	private var selectedPosition = 0
	private var preview = false
	var listener : IcallbackListener? = null
	private var dataIsChanging = true

	override fun onAdapterChanged(
			oldAdapter : RecyclerView.Adapter<*>?,
			newAdapter : RecyclerView.Adapter<*>?
								 ) {
		super.onAdapterChanged(oldAdapter, newAdapter)
		adapter = newAdapter
	}

	override fun generateDefaultLayoutParams() : RecyclerView.LayoutParams {
		return RecyclerView.LayoutParams(
				RecyclerView.LayoutParams.WRAP_CONTENT,
				RecyclerView.LayoutParams.WRAP_CONTENT
										)
	}

	override fun onLayoutChildren(recycler : RecyclerView.Recycler?, state : RecyclerView.State?) {
		this.recycler = recycler // 二话不说，先把recycler保存了
		this.recyclerState = state
		if (preview) return
		preview = true
		dataIsChanging = true

		addRectsAndcomputeMaxScoll()
		// 先回收放到缓存，后面会再次统一layout
		detachAndScrapAttachedViews(recycler!!)
		addViewAndlocationView(recycler)
		listener?.createViewSuccess()
		dataIsChanging = false
	}

	fun reloadView() {
		preview = false
	}

	private fun addRectsAndcomputeMaxScoll() {
		saveLocationRects.clear()
		attachedItems.clear()
		var tempPosition = paddingTop
		val itemCount = recyclerState?.itemCount
			?: return
		for (i in 0 until itemCount) {
			// 1. 先计算出itemWidth和itemHeight
			var itemHeight : Int = 0
			val viewType = adapter?.getItemViewType(i)
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
				tempPosition += itemHeight/2
			}
			// 2. 组装Rect并保存
			val rect = Rect()
			rect.left = paddingLeft
			rect.top = tempPosition
			rect.right = width-paddingRight
			rect.bottom = rect.top+itemHeight
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
		val itemHeight = saveLocationRects[0].height()
		maxScroll =
			saveLocationRects[saveLocationRects.size()-1].bottom-(paddingTop+height-paddingBottom-itemHeight/2)
		if (maxScroll<0) {
			maxScroll = 0
			return
		}
	}

	/**
	 * 初始化的时候，layout子View
	 */
	private fun addViewAndlocationView(recycler : RecyclerView.Recycler) {
		val itemCount = recyclerState?.itemCount
			?: return
		val displayRect = Rect(0, paddingTop+scroll, width, height-paddingBottom+scroll)
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
			childView.requestLayout()
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
	private fun layoutItem(childView : View, thisRect : Rect, location : Int) : Boolean {
		val layoutLeft = paddingLeft
		val layoutTop = thisRect.top-scroll
		val layoutBottom = thisRect.bottom-scroll
		val layoutRight = width-paddingRight
		val layoutCenterY = (layoutTop+layoutBottom)/2
		val itemHeight = thisRect.height()
		val itemWidth = thisRect.width()
		var isSelect = false
		val screenCenterY = (height+paddingTop-paddingBottom)/2
		var lc = location
		val scrollToCenterY = abs(layoutCenterY-screenCenterY)
		var scale = minScale
		when (scrollToCenterY) {
			in 0..itemHeight             -> {
				if (scrollToCenterY<=itemHeight/2) {
					isSelect = true
					lc = 2
				}
				scale = 1f-scrollToCenterY.toFloat()/itemHeight*oneChangeScale

			}
			in itemHeight..screenCenterY -> {
				val wholeDis = abs(screenCenterY-itemHeight)
				scale =
					1f-oneChangeScale-(scrollToCenterY-itemHeight).toFloat()/wholeDis*twoChangeScale
			}
		}
		childView.apply {
			when (lc) {
				1 -> {
					pivotX = itemWidth/2f
					pivotY = 0f
				}
				2 -> {
					pivotX = itemWidth/2f
					pivotY = itemHeight/2f
				}
				3 -> {
					pivotX = itemWidth/2f
					pivotY = itemHeight.toFloat()
				}
			}
			scaleX = scale
			scaleY = scale
		}

		layoutDecorated(childView, layoutLeft, layoutTop, layoutRight, layoutBottom)
		return isSelect
	}

	override fun scrollVerticallyBy(
			dy : Int,
			recycler : RecyclerView.Recycler?,
			state : RecyclerView.State
								   ) : Int {
		if (dataIsChanging) return 0
//        //解决快速滑动导致失真的问题
		if (childCount == 0 || itemCount != saveLocationRects.size()) {
			preview = false
			dataIsChanging = true
			onLayoutChildren(recycler, state)
			return 0
		}
		if (dy == 0) {
			return 0
		}
		var travel = dy
		if (dy+scroll<0) {
			travel = -scroll
		} else if (dy+scroll>maxScroll) {
			travel = maxScroll-scroll
		}
		scroll += travel //累计偏移量
		lastDy = dy
		if (childCount>0) {
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
		val itemCount = recyclerState?.itemCount
			?: return
		val displayRect = Rect(0, paddingTop+scroll, width, height-paddingBottom+scroll)
		var firstVisiblePosition = -1
		var lastVisiblePosition = -1
		var isDown = true
		for (i in childCount-1 downTo 0) {
			val child = getChildAt(i)
				?: continue
			val position = getPosition(child)
			val thisRect = saveLocationRects[position]
			if (!Rect.intersects(displayRect, thisRect)) {
				// 回收滑出屏幕的View
				removeAndRecycleView(child, recycler!!)
				attachedItems.put(position, false)
				continue
			}
			// Item还在显示区域内，更新滑动后Item的位置
			if (lastVisiblePosition<0) {
				lastVisiblePosition = position
			}
			firstVisiblePosition = if (firstVisiblePosition<0) {
				position
			} else {
				min(firstVisiblePosition, position)
			}
			if (layoutItem(child, thisRect, if (isDown) 3 else 1)) {
				selectedPosition = position
				isDown = false
			}
		}
		// 2. 复用View处理
		if (firstVisiblePosition>0) {
			// 往前搜索复用
			for (i in firstVisiblePosition-1 downTo 0) {
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
		if (lastVisiblePosition+1<itemCount) {
			for (i in lastVisiblePosition+1 until itemCount) {
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
	private fun reuseItemOnSroll(position : Int, addViewFromTop : Boolean) {
		val scrap = recycler?.getViewForPosition(position)
			?: return
		scrap.requestLayout()
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

	override fun canScrollVertically() : Boolean {
		return true
	}

	override fun canScrollHorizontally() : Boolean {
		return false
	}

	override fun onScrollStateChanged(state : Int) {
		super.onScrollStateChanged(state)
		if (state == RecyclerView.SCROLL_STATE_IDLE) {
			needSnap = true
		}
	}

	override fun onAttachedToWindow(view : RecyclerView) {
		super.onAttachedToWindow(view)
		if (view.onFlingListener == null)
			LocationSnapHelper().attachToRecyclerView(view)
	}

	fun findSnapView() : View? {
		return if (childCount>0) {
			getChildAt(0)
		} else null
	}

	fun getSnapHeight() : Int {
		if (!needSnap) {
			return 0
		}
		needSnap = false
		return getFixIndex()
	}

	private fun getFixIndex() : Int {
		val dis = 1
		val maxMoveAngle = 0.25f
		val realHeight = paddingTop+height-paddingBottom
		val displayRect =
			Rect(0, scroll+realHeight/2-dis, width, scroll+realHeight/2+dis)
		val displayCenterY = (displayRect.top+displayRect.bottom)/2
		for (position in childCount-1 downTo 0) {
			val child = getChildAt(position)
				?: continue
			val i = getPosition(child)
			val itemRect = saveLocationRects[i]
			if (!displayRect.intersect(itemRect)) continue
			val thisCenterY = (itemRect.top+itemRect.bottom)/2
			if (lastDy>0) {
				// scroll变大，属于列表往下走，往下找下一个为snapView
				if (thisCenterY>displayCenterY) {
					return thisCenterY-displayCenterY //大于0，往上
				}
				val thisMaxMoveHeight = (itemRect.height()*maxMoveAngle).toInt()
				val disY = displayCenterY-thisCenterY
				if (disY<thisMaxMoveHeight) {
					return thisCenterY-displayCenterY  //小于0，往下
				}
				if (i>=saveLocationRects.size()-1) {
					return thisCenterY-displayCenterY  //小于0，往下
				}
				return itemRect.height()-(displayCenterY-thisCenterY)  //大于零上滑
			}
			if (displayCenterY>thisCenterY) {
				return thisCenterY-displayCenterY
			}
			val thisMaxMoveHeight = (itemRect.height()*maxMoveAngle).toInt()
			val disY = thisCenterY-displayCenterY
			if (disY<thisMaxMoveHeight) {
				return thisCenterY-displayCenterY
			}
			if (i-1<0) {
				return thisCenterY-displayCenterY
			}
			return (thisCenterY-displayCenterY)-itemRect.height()
		}
		return 0
	}

	override fun scrollToPosition(position : Int) {
		if (!preview) return
		preview = false
		var moveScroll = 0
		try {
			val displayHeight = height-paddingBottom-paddingTop
			moveScroll = saveLocationRects[position].centerY()-displayHeight/2
		} catch (e : Exception) {
		}
		if (moveScroll<=0) return
		scroll = moveScroll
		detachAndScrapAttachedViews(recycler!!)
		onLayoutChildren(recycler, recyclerState)
	}

	interface IcallbackListener {

		fun createViewSuccess()
	}
}