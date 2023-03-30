package com.benew.nttl.launcher.arch.boradcast.abase.entity

import com.benew.nttl.launcher.arch.boradcast.abase.BaseManager

data class AsynMsg(val registerName : Class<*>, val onCall : BaseManager.OnCallBack)