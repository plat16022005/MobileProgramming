package com.group02.mobile.data.repository

import com.group02.mobile.data.local.UserVocabularyDao
import com.group02.mobile.data.model.vocabulary.UserVocabulary
import com.group02.mobile.utils.CsvParser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserVocabularyRepository(private val dao: UserVocabularyDao) {

    fun getAllVocabularies(): Flow<List<UserVocabulary>> {
        return dao.getAllVocabularies()
    }

    suspend fun addVocabulary(item: UserVocabulary): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            dao.insertVocabulary(item)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateVocabulary(item: UserVocabulary): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val updatedItem = item.copy(updatedAt = System.currentTimeMillis())
            dao.updateVocabulary(updatedItem)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteVocabulary(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            dao.deleteVocabularyById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun importFromCsv(csvContent: String): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val parsedList = CsvParser.parseCsv(csvContent)
            if (parsedList.isNotEmpty()) {
                dao.insertVocabularies(parsedList)
            }
            Result.success(parsedList.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
