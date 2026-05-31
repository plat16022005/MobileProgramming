package com.group02.mobile.data.model.srs

data class SrsData(
    val wordId: String = "",
    val word: String = "",
    val hiragana: String = "",
    val romaji: String = "",
    val meanings: String = "",
    val learned: Boolean = true,
    val timestamp: Long = 0L,
    val repetition: Int = 0,
    val interval: Int = 0,
    val easeFactor: Float = 2.5f,
    val nextReviewTime: Long = 0L,
    val isMastered: Boolean = false
)
