package com.benew.ntt.jreading.arch.widget.lyric.entity

import androidx.annotation.Keep
import com.benew.ntt.notes.mvp.model.entity.DictWordEntity

data class LyricEntity(
		val uid : Long,
		val audioLink : String?,
		val audioBGMLink : String?,
		val text : String?,
		val translate : String?,
		val language : String?,
		val skipHead : Double?,
		var skipTail : Double?,
		var taskId : Long?,
		val words : MutableList<DictWordEntity>,
					  ) {

	var volume : Int = 0
}