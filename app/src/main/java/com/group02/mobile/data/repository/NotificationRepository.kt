package com.group02.mobile.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

class NotificationRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val messaging = FirebaseMessaging.getInstance()

    suspend fun updateStatusAndToken() {
        val uid = auth.currentUser?.uid ?: return

        try {
            val fcmToken = messaging.token.await()
            val currentTime = System.currentTimeMillis()


            val data = mapOf(
                "uid" to uid,
                "fcmToken" to fcmToken,
                "lastTimeOpenApp" to currentTime
            )

            firestore.collection("MessagingNotification")
                .document(uid)
                .set(data, SetOptions.merge())
                .await()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}