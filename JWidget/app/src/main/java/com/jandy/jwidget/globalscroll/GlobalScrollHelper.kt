package com.jandy.jwidget.globalscroll

import androidx.recyclerview.widget.RecyclerView
import com.jandy.jwidget.R
import com.jandy.jwidget.globalscroll.entity.ScrollItemEntity
import com.jandy.jwidget.globalscroll.layoutmanager.GlobalScrollLayoutManager_75
import com.jandy.jwidget.globalscroll.plugins.GenerateUtils
import com.jandy.jwidget.globalscroll.plugins.RecyclerAdapterGlobalScroll
import java.util.*
import kotlin.collections.ArrayList

class GlobalScrollHelper {

    private lateinit var rv: RecyclerView
    lateinit var adapter: RecyclerAdapterGlobalScroll

    constructor(rootView: GlobalScrollView) {
        rv = rootView.findViewById(R.id.rv_view)
        rv.layoutManager = GlobalScrollLayoutManager_75()
        adapter = RecyclerAdapterGlobalScroll(R.layout.layout_global_item_view)
        rv.adapter = adapter
        rv.itemAnimator=null
    }

    constructor(rootView: GlobalScrollView, data: MutableList<ScrollItemEntity>) {
        rv = rootView.findViewById(R.id.rv_view)
        rv.layoutManager = GlobalScrollLayoutManager_75()
        adapter = RecyclerAdapterGlobalScroll(R.layout.layout_global_item_view)
        adapter.data = data
        rv.adapter = adapter
    }

    fun setUpRoundDiameter(dia: Int) {
        adapter.setupRoundDiameter(dia)
    }

    fun setData(data: MutableList<ScrollItemEntity>) {
        (rv.layoutManager as GlobalScrollLayoutManager_75).reloadView()
        adapter.setList(data)
    }

    fun setGlobalOnItemClickListener(listener: RecyclerAdapterGlobalScroll.GlobalItemOnClickListener?) {
        adapter.click = listener
    }

    fun setGlobalOnItemLongClickListener(listener: RecyclerAdapterGlobalScroll.GlobalItemOnLongClickListener?) {
        adapter.longClick = listener
    }

    fun notifyDataSetChanged() {
        (rv.layoutManager as GlobalScrollLayoutManager_75).reloadView()
        adapter?.notifyDataSetChanged()
    }

    fun notifyDataSetChanged(position: Int) {
        (rv.layoutManager as GlobalScrollLayoutManager_75).reloadView()
        adapter?.notifyItemChanged(position)
    }

    fun flashReview() {
        (rv.layoutManager as GlobalScrollLayoutManager_75)?.review()
    }

    fun getSortList(sizeValue : Int) : List<Int> {
        val sortList = ArrayList<Int>()
        val intArrays = GenerateUtils.generateNums(sizeValue)
        if (intArrays == null || intArrays.isEmpty()) return sortList
        val intList = ArrayList<Int>()
        for (intValue in intArrays) {
            intList.add(intValue)
        }
//        NLogger.d("jandy intList", GsonUtils.toJson(intList))
        val wholeList = getWholeList(intList)
//        NLogger.d("jandy wholeList", GsonUtils.toJson(wholeList))
        val centerSizeIndex = wholeList.size/2   //拿到index
        val sizeIndex = wholeList.size-1
        //偶数和奇数不一样
        val maxLine = if (wholeList[centerSizeIndex]%2 == 0) wholeList[centerSizeIndex]/2-1
        else (wholeList[centerSizeIndex]+1)/2-1  //最多有多少层
//        NLogger.d("jandy maxLine", maxLine)
        val changeList = ArrayList(intList)   //能被取走的值
        val allList = ArrayList<ArrayList<Int>>()
//        val perLine = 0 //代表第几层
        for (perLine in 0..maxLine) {
            val perList = ArrayList<Int>()
            //逆时针，9-12点方向--不会出现算错的情况
            for (i in centerSizeIndex downTo 0) {
                //判断当前是否还有值
                val remainValue = changeList[i]
                if (remainValue<=0) continue
                val beforePosition = getBeforePosition(intList, i)
                val position = beforePosition+perLine
                perList.add(position)
                //被取走的值
                changeList[i] = remainValue-1
            }
            //逆时针，12-6点方向。。情况多一点
            for (i in 0..sizeIndex) {
                //判断当前是否还有值
                val remainValue = changeList[i]
                if (remainValue<=0) continue
                //左侧的被取走的值
                val leftIndex = perLine
                var rightIndex = intList[i]-1-perLine
                val rightIndexCompare = wholeList[i]-1-perLine
                if (leftIndex == rightIndexCompare) {
                    continue
                }
                if (rightIndex != rightIndexCompare) {
                    //说明不存在
                    val maxRightIndex = intList[i]-1
                    if (rightIndexCompare>maxRightIndex)
                        continue
                    else
                        rightIndex = rightIndexCompare
                }
                val beforePosition = getBeforePosition(intList, i)
                val position = rightIndex+beforePosition
                perList.add(position)
                //被取走的值
                changeList[i] = remainValue-1
            }
            //逆时针，3-6点方向
//        for (i in centerSizeIndex..size){
//            val beforePosition = getBeforePosition(intList, i)
//            val position = (intList[i] - 1) - perLine + beforePosition
//            perList.add(position)
//        }
            //逆时针，6-9点方向
            for (i in sizeIndex downTo centerSizeIndex) {
                //判断当前是否还有值
                val remainValue = changeList[i]
                if (remainValue<=0) continue
                if (i == centerSizeIndex) break
                val beforePosition = getBeforePosition(intList, i)
                val position = beforePosition+perLine
                perList.add(position)
                //被取走的值
                changeList[i] = remainValue-1
            }
            allList.add(perList)
        }
        allList.reverse()
//        NLogger.d("jandy roundList ", GsonUtils.toJson(allList))
        for (l in allList) {
            sortList.addAll(l)
        }
//        NLogger.d("jandy list ", GsonUtils.toJson(sortList))
        return sortList
    }

    fun getWholeList(inList : List<Int>) : List<Int> {
        val centerIndex = inList.size/2
        val sizeIndex = inList.size-1
        val newList = ArrayList<Int>()
        val maxValue = inList[centerIndex]
        //往上排查
        for (i in centerIndex downTo 0) {
            val valueIndex = centerIndex-i
            newList.add(maxValue-valueIndex)
        }
        newList.reverse()
        //往下遍历
        for (i in centerIndex..sizeIndex) {
            if (i == centerIndex) continue
            val valueIndex = i-centerIndex
            newList.add(maxValue-valueIndex)
        }
        return newList
    }

    private fun getBeforePosition(inList : List<Int>, lineIndex : Int) : Int {
        val beforeIndex = lineIndex-1
        if (beforeIndex<0) return 0
        var position = 0
        try {
            for (i in beforeIndex downTo 0) {
                position += inList[i]
            }
        } catch (e : Exception) {
        }
        return position
    }


    fun getScrollSortList(apps : List<ScrollItemEntity>) : List<ScrollItemEntity> {
        Collections.sort(apps, object : Comparator<ScrollItemEntity> {
            override fun compare(o1 : ScrollItemEntity, o2 : ScrollItemEntity) : Int {
                return if (o1.sortId>o2.sortId) 1 else -1
            }
        })
        return apps
    }
}