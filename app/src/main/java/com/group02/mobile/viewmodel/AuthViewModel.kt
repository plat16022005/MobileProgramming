package com.group02.mobile.viewmodel

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.group02.mobile.data.repository.AuthRepository
import com.group02.mobile.data.repository.AuthResult
import com.group02.mobile.data.repository.UserProfile
import com.group02.mobile.data.repository.UserAccount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.async

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false,
    val userAccount: UserAccount? = null,
    val userProfile: UserProfile? = null
)

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = AuthUiState(isLoggedIn = repository.isLoggedIn)
    }

    fun signInWithEmail(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Vui lòng điền đầy đủ thông tin")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val result = repository.signInWithEmail(email, password)) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        isLoggedIn = true
                    )
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                else -> {}
            }
        }
    }

    fun registerWithEmail(email: String, password: String, confirmPassword: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Vui lòng điền đầy đủ thông tin")
            return
        }
        if (password != confirmPassword) {
            _uiState.value = _uiState.value.copy(errorMessage = "Mật khẩu xác nhận không khớp")
            return
        }
        if (password.length < 6) {
            _uiState.value = _uiState.value.copy(errorMessage = "Mật khẩu phải có ít nhất 6 ký tự")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val result = repository.registerWithEmail(email, password)) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        isLoggedIn = true
                    )
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                else -> {}
            }
        }
    }

    fun signInWithGoogle(context: Context, webClientId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val credentialManager = CredentialManager.create(context)
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(webClientId)
                    .build()
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()
                val result = credentialManager.getCredential(context = context, request = request)
                val credential = result.credential
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken

                when (val authResult = repository.signInWithGoogle(idToken)) {
                    is AuthResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isSuccess = true,
                            isLoggedIn = true
                        )
                    }
                    is AuthResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = authResult.message
                        )
                    }
                    else -> {}
                }
            } catch (e: GetCredentialException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Đăng nhập Google bị hủy"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Lỗi Google Sign-In: ${e.localizedMessage}"
                )
            }
        }
    }

    fun sendPasswordReset(email: String) {
        if (email.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Vui lòng nhập email")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val result = repository.sendPasswordResetEmail(email)) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                else -> {}
            }
        }
    }

    fun fetchUserProfile() {
        val currentUser = repository.currentUser
        if (currentUser == null) {
            _uiState.value = _uiState.value.copy(isLoggedIn = false, userAccount = null, userProfile = null)
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            val accountDeferred = async { repository.getUserAccount(currentUser.uid) }
            val profileDeferred = async { repository.getUserProfile(currentUser.uid) }
            
            var account = accountDeferred.await()
            var profile = profileDeferred.await()
            
            if (account == null) {
                account = UserAccount(
                    uid = currentUser.uid,
                    displayName = currentUser.displayName ?: "",
                    email = currentUser.email ?: ""
                )
                repository.saveUserAccount(account)
            }
            
            if (profile == null) {
                profile = UserProfile(
                    uid = currentUser.uid,
                    displayName = currentUser.displayName ?: "",
                    photoUrl = "🦊" // default emoji avatar
                )
                repository.saveUserProfile(profile)
            }
            
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                userAccount = account,
                userProfile = profile
            )
        }
    }

    fun updateUserProfile(
        displayName: String,
        phoneNumber: String,
        birthDate: String,
        gender: String,
        studyLevel: String,
        address: String = "",
        photoUrl: String = "",
        onSuccess: () -> Unit = {}
    ) {
        val currentAccount = _uiState.value.userAccount ?: return
        val currentProfile = _uiState.value.userProfile ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            val updatedAccount = currentAccount.copy(
                displayName = displayName,
                profileCompleted = true
            )
            val updatedProfile = currentProfile.copy(
                displayName = displayName,
                phoneNumber = phoneNumber,
                birthDate = birthDate,
                gender = gender,
                studyLevel = studyLevel,
                address = address,
                photoUrl = photoUrl
            )
            
            val accResult = repository.saveUserAccount(updatedAccount)
            val profResult = repository.saveUserProfile(updatedProfile)
            
            if (accResult is AuthResult.Success && profResult is AuthResult.Success) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    userAccount = updatedAccount,
                    userProfile = updatedProfile,
                    isSuccess = true
                )
                onSuccess()
            } else {
                val errorMsg = when {
                    accResult is AuthResult.Error -> accResult.message
                    profResult is AuthResult.Error -> profResult.message
                    else -> "Đã xảy ra lỗi khi lưu thông tin"
                }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = errorMsg
                )
            }
        }
    }

    fun signOut() {
        repository.signOut()
        _uiState.value = AuthUiState(isLoggedIn = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun resetSuccess() {
        _uiState.value = _uiState.value.copy(isSuccess = false)
    }
}
