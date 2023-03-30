package com.benew.nttl.launcher.arch.boradcast.abase

import com.benew.nttl.launcher.arch.boradcast.abase.entity.AsynMsg
import kotlinx.coroutines.Job
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

object BaseManagerMap {

	val listenerMps = ConcurrentHashMap<Class<*>, List<Any>>()
	val listenerAsynQueue = ConcurrentLinkedQueue<AsynMsg>()
	val maxListenerQueueSize = 30 //最大监听存储量
	var mAsynJob : Job? = null //异步线程
}