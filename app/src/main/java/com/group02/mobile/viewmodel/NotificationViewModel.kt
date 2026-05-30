package com.group02.mobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.group02.mobile.data.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class NotificationViewModel : ViewModel() {
    private val repository = NotificationRepository()
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _scheduleState = MutableStateFlow<Map<String, List<String>>>(emptyMap())
    val scheduleState: StateFlow<Map<String, List<String>>> = _scheduleState


    fun asyncStateSystem() {
        viewModelScope.launch {
            repository.updateStatusAndToken()
        }
    }

    fun loadSchedules() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val document = firestore.collection("MessagingNotification")
                    .document(uid)
                    .get()
                    .await()

                if (document.exists()) {
                    val rawData = document.get("scheduledHours") as? Map<String, List<String>>
                    if (rawData != null) {
                        _scheduleState.value = rawData
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveSchedules(scheduledHours: Map<String, List<String>>, onResult: (Boolean) -> Unit) {
        val currentUser = auth.currentUser
        val uid = currentUser?.uid ?: run {
            onResult(false)
            return
        }

        viewModelScope.launch {
            try {
                firestore.collection("MessagingNotification")
                    .document(uid)
                    .set(mapOf("scheduledHours" to scheduledHours), SetOptions.merge())
                    .await()
                _scheduleState.value = scheduledHours
                onResult(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }
}
