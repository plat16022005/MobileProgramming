package com.group02.mobile.data.model.alphabet

data class KanjiDetail(
    val kanji: String,
    val grade: Int?,
    val strokeCount: Int?,
    val meanings: List<String>,
    val kunReadings: List<String>,
    val onReadings: List<String>,
    val jlpt: Int?,
    val unicode: String?,
    val heisigEn: String?
)
