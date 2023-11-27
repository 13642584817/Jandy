package com.benew.ntt.notes.mvp.model.entity

import androidx.annotation.Keep

@Keep
data class DictWordEntity(
    val word: String? = null,
    var skipHead: Double?,
    var skipTail: Double?,
)