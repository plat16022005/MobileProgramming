package com.group02.mobile.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.group02.mobile.data.local.AppDatabase
import com.group02.mobile.data.repository.UserVocabularyRepository

class UserVocabularyViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserVocabularyViewModel::class.java)) {
            val database = AppDatabase.getDatabase(application)
            val repository = UserVocabularyRepository(database.userVocabularyDao())
            @Suppress("UNCHECKED_CAST")
            return UserVocabularyViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class CustomPracticeViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CustomPracticeViewModel::class.java)) {
            val database = AppDatabase.getDatabase(application)
            val repository = UserVocabularyRepository(database.userVocabularyDao())
            @Suppress("UNCHECKED_CAST")
            return CustomPracticeViewModel(repository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
