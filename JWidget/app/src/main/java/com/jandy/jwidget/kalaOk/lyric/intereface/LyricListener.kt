package com.benew.ntt.jreading.arch.widget.lyric.intereface

interface LyricListener {

    /**
     * 句子结束
     */
    fun onSentenceEnd(position: Int)

    /**
     * 句子改变
     */
    fun onSentenceChange(position: Int)
}