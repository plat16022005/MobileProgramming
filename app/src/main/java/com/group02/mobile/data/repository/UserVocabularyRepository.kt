package com.group02.mobile.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.group02.mobile.data.local.dao.UserVocabularyDao
import com.group02.mobile.data.model.vocabulary.UserVocabulary
import com.group02.mobile.utils.CsvParser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class UserVocabularyRepository(
    private val dao: UserVocabularyDao,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    private val currentUserId: String?
        get() = auth.currentUser?.uid

    /**
     * Get all vocabularies from Room database (realtime updates)
     */
    fun getAllVocabularies(): Flow<List<UserVocabulary>> {
        return dao.getAllVocabularies()
    }

    /**
     * Add a single vocabulary to Room and sync to Firestore
     */
    suspend fun addVocabulary(item: UserVocabulary): Result<Unit> {
        return try {
            // Local DB
            dao.insertVocabulary(item)
            
            // Sync to Firestore if logged in
            val uid = currentUserId
            if (uid != null) {
                firestore.collection("UserVocabularies")
                    .document(uid)
                    .collection("Vocabularies")
                    .document(item.id)
                    .set(item)
                    .await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update vocabulary in Room and sync to Firestore
     */
    suspend fun updateVocabulary(item: UserVocabulary): Result<Unit> {
        return try {
            val updatedItem = item.copy(updatedAt = System.currentTimeMillis())
            // Local DB
            dao.updateVocabulary(updatedItem)
            
            // Sync to Firestore if logged in
            val uid = currentUserId
            if (uid != null) {
                firestore.collection("UserVocabularies")
                    .document(uid)
                    .collection("Vocabularies")
                    .document(item.id)
                    .set(updatedItem)
                    .await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Delete vocabulary from Room and Firestore
     */
    suspend fun deleteVocabulary(id: String): Result<Unit> {
        return try {
            // Local DB
            dao.deleteVocabulary(id)
            
            // Sync to Firestore if logged in
            val uid = currentUserId
            if (uid != null) {
                firestore.collection("UserVocabularies")
                    .document(uid)
                    .collection("Vocabularies")
                    .document(id)
                    .delete()
                    .await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Parse CSV, add batch to Room and sync to Firestore.
     * Returns the number of successfully imported items.
     */
    suspend fun importFromCsv(csvContent: String): Result<Int> {
        return try {
            val vocabularies = CsvParser.parse(csvContent)
            if (vocabularies.isEmpty()) {
                return Result.success(0)
            }

            // Local DB
            dao.insertVocabularies(vocabularies)

            // Sync to Firestore if logged in
            val uid = currentUserId
            if (uid != null) {
                val batch = firestore.batch()
                val collectionRef = firestore.collection("UserVocabularies").document(uid).collection("Vocabularies")
                
                vocabularies.forEach { item ->
                    val docRef = collectionRef.document(item.id)
                    batch.set(docRef, item)
                }
                
                batch.commit().await()
            }

            Result.success(vocabularies.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Optional: Fetch from Firestore and save to Room (e.g. on fresh login)
     */
    suspend fun syncFromFirestore() {
        val uid = currentUserId ?: return
        try {
            val snapshot = firestore.collection("UserVocabularies")
                .document(uid)
                .collection("Vocabularies")
                .get()
                .await()
                
            val vocabularies = snapshot.toObjects(UserVocabulary::class.java)
            if (vocabularies.isNotEmpty()) {
                dao.insertVocabularies(vocabularies)
            }
        } catch (e: Exception) {
            // Log error
        }
    }
}
