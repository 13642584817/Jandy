package com.benew.nttl.launcher.arch.boradcast.abase

import android.util.Log
import com.benew.nttl.launcher.arch.boradcast.abase.BaseManagerMap.listenerAsynQueue
import com.benew.nttl.launcher.arch.boradcast.abase.BaseManagerMap.listenerMps
import com.benew.nttl.launcher.arch.boradcast.abase.BaseManagerMap.mAsynJob
import com.benew.nttl.launcher.arch.boradcast.abase.BaseManagerMap.maxListenerQueueSize
import com.benew.nttl.launcher.arch.boradcast.abase.entity.AsynMsg
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList

abstract class BaseManager {

	private val TAG = this.javaClass.simpleName

	fun register(listener : Any) {
		val className = registerClassName()
		if (!listenerMps.containsKey(className)) {
			val listeners = CopyOnWriteArrayList<Any>()
			listeners.add(listener)
			listenerMps[className] = listeners
			return
		}
		listenerMps[className]?.apply {
			if (contains(listener)) return
			if (this !is CopyOnWriteArrayList<Any>) return@apply
			add(listener)
		}
	}

	fun unRegister(listener : Any) {
		val className = registerClassName()
		if (!listenerMps.containsKey(className)) return
		listenerMps[className]?.apply {
			if (!contains(listener)) return
			if (this !is CopyOnWriteArrayList<Any>) return@apply
			remove(listener)
		}
	}

	//同步回调
	fun onRun(run : OnCallBack) {
		GlobalScope.launch {
			val className = registerClassName()
			if (!listenerMps.containsKey(className)) return@launch
			val listeners = listenerMps[className]
				?: return@launch
			for (listener in listeners) {
				try {
					run.onRun(listener)
				} catch (e : Exception) {
					Log.d(TAG, "error = ${e.toString()}")
				}
			}
		}
	}

	//需要以最后操作为准的监听回调模式
	fun onAsynRun(run : OnCallBack) {
//		Log.d(TAG, "正在增加任务,个数为: ${listenerAsynQueue.size}")
		if (listenerAsynQueue.size>=maxListenerQueueSize) {
			Log.d(TAG, "超过最大任务数 $maxListenerQueueSize ，会进行移除第一个元素模式")
			listenerAsynQueue.poll()
		}
		listenerAsynQueue.offer(AsynMsg(registerClassName(), run))
		if (mAsynJob?.isActive == true) {
//			Log.d(TAG, "<-----异步回调线程正在进行 正在执行${listenerAsynList.size}个任务--->")
			return
		}
		mAsynJob = GlobalScope.launch {
			lxListenerList()
		}
		mAsynJob?.start()
	}

	private fun lxListenerList() {
		if (listenerAsynQueue.isNullOrEmpty()) return
		var entity : AsynMsg? = null
		while (listenerAsynQueue.poll()?.also { entity = it } != null) {
			entity?.apply {
				if (!listenerMps.containsKey(registerName)) return@apply
				val listeners = listenerMps[registerName]
					?: return@apply
				for (listener in listeners) {
					try {
						onCall.onRun(listener)
					} catch (e : Exception) {
						Log.d(TAG, "error = ${e.toString()}")
					}
				}
			}
		}
	}

	protected abstract fun registerClassName() : Class<*>

	interface OnCallBack {

		fun onRun(listener : Any)
	}
}

