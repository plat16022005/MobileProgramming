package com.group02.mobile.model


data class UserProgress(
    val accuracy: Double = 0.0,
    val correctAnswers: Long = 0,
    val totalAnswers: Long = 0,
    val learnedWordsCount: Long = 0,
    val learnedN1Count: Long = 0,
    val learnedN2Count: Long = 0,
    val learnedN3Count: Long = 0,
    val learnedN4Count: Long = 0,
    val learnedN5Count: Long = 0,
    val streakDays: Long = 0
)