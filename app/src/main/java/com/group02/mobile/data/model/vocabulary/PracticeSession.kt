package com.group02.mobile.data.model.vocabulary

import java.util.UUID

enum class PracticeMode {
    STUDY, FLASHCARD, QUIZ, CHALLENGE
}

data class PracticeSession(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val selectedVocabularies: List<UserVocabulary>,
    val mode: PracticeMode,
    val createdAt: Long = System.currentTimeMillis()
)
