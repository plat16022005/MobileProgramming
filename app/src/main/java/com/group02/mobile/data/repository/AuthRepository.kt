package com.group02.mobile.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

sealed class AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error(val message: String) : AuthResult<Nothing>()
    object Loading : AuthResult<Nothing>()
}

data class UserProfile(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val studyLevel: String = "beginner",
    val totalPoints: Int = 0
)

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    val isLoggedIn: Boolean
        get() = auth.currentUser != null

    suspend fun signInWithEmail(email: String, password: String): AuthResult<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: return AuthResult.Error("Đăng nhập thất bại")
            AuthResult.Success(user)
        } catch (e: Exception) {
            AuthResult.Error(mapFirebaseError(e.message))
        }
    }

    suspend fun registerWithEmail(
        email: String,
        password: String,
        displayName: String
    ): AuthResult<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: return AuthResult.Error("Đăng ký thất bại")

            // Update display name
            val profileUpdates = com.google.firebase.auth.userProfileChangeRequest {
                this.displayName = displayName
            }
            user.updateProfile(profileUpdates).await()

            // Save user profile to Firestore
            val profile = UserProfile(
                uid = user.uid,
                displayName = displayName,
                email = email
            )
            saveUserProfile(profile)

            AuthResult.Success(user)
        } catch (e: Exception) {
            AuthResult.Error(mapFirebaseError(e.message))
        }
    }

    suspend fun signInWithGoogle(idToken: String): AuthResult<FirebaseUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user ?: return AuthResult.Error("Đăng nhập Google thất bại")

            // Check if new user → create profile
            if (result.additionalUserInfo?.isNewUser == true) {
                val profile = UserProfile(
                    uid = user.uid,
                    displayName = user.displayName ?: "",
                    email = user.email ?: "",
                    photoUrl = user.photoUrl?.toString() ?: ""
                )
                saveUserProfile(profile)
            }

            AuthResult.Success(user)
        } catch (e: Exception) {
            AuthResult.Error(mapFirebaseError(e.message))
        }
    }

    suspend fun sendPasswordResetEmail(email: String): AuthResult<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(mapFirebaseError(e.message))
        }
    }

    fun signOut() {
        auth.signOut()
    }

    private suspend fun saveUserProfile(profile: UserProfile) {
        firestore.collection("users")
            .document(profile.uid)
            .set(profile)
            .await()
    }

    private fun mapFirebaseError(message: String?): String {
        if (message == null) return "Đã xảy ra lỗi không xác định"
        
        val msg = message.lowercase()
        return when {
            msg.contains("email address is already in use") || msg.contains("email_already_in_use") -> 
                "Email này đã được sử dụng"
            msg.contains("email address is badly formatted") || msg.contains("invalid_email") -> 
                "Định dạng email không hợp lệ"
            msg.contains("password is invalid") || msg.contains("wrong-password") || msg.contains("invalid_credential") || msg.contains("invalid login credentials") ->
                "Tài khoản hoặc mật khẩu không chính xác"
            msg.contains("no user record") || msg.contains("user-not-found") ->
                "Tài khoản không tồn tại"
            msg.contains("too many requests") -> 
                "Quá nhiều yêu cầu, vui lòng thử lại sau"
            msg.contains("network error") || msg.contains("network_error") -> 
                "Lỗi kết nối mạng"
            msg.contains("weak-password") || msg.contains("password should be at least") -> 
                "Mật khẩu quá yếu (phải có ít nhất 6 ký tự)"
            else -> "Email hoặc mật khẩu không chính xác"
        }
    }
}
