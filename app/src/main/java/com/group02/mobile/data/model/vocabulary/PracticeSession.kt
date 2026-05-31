package com.group02.mobile.data.model.vocabulary

import java.util.UUID

data class PracticeSession(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val vocabularyIds: List<String> = emptyList(),
    val temporaryVocabularies: List<UserVocabulary> = emptyList(),
    val mode: PracticeMode = PracticeMode.STUDY,
    val createdAt: Long = System.currentTimeMillis()
)

enum class PracticeMode {
    STUDY, FLASHCARD, QUIZ, CHALLENGE
}
