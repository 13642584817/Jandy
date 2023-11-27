package com.benew.ntt.jreading.arch.widget.lyric.intereface

import android.view.View
import com.benew.ntt.jreading.arch.widget.lyric.entity.LyricEntity

interface OnLyricItemClickListener {

	fun onItemClick(position : Int, entity : LyricEntity, view : View)
	fun onItemLongClick(position : Int, entity : LyricEntity, view : View)
}