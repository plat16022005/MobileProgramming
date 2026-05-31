package com.group02.mobile.data.model.vocabulary

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "user_vocabularies")
data class UserVocabulary(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val kanji: String = "",
    val hiragana: String = "",
    val romaji: String = "",
    val meaning: String = "",
    val studyCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
