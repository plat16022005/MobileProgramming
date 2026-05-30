package com.group02.mobile.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.group02.mobile.data.model.vocabulary.UserVocabulary
import kotlinx.coroutines.flow.Flow

@Dao
interface UserVocabularyDao {
    @Query("SELECT * FROM user_vocabularies ORDER BY createdAt DESC")
    fun getAllVocabularies(): Flow<List<UserVocabulary>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVocabulary(vocabulary: UserVocabulary)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVocabularies(vocabularies: List<UserVocabulary>)

    @Update
    suspend fun updateVocabulary(vocabulary: UserVocabulary)

    @Query("DELETE FROM user_vocabularies WHERE id = :id")
    suspend fun deleteVocabularyById(id: String)
}
