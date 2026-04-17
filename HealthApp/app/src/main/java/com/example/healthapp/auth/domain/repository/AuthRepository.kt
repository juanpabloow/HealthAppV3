package com.example.healthapp.auth.domain.repository

import com.example.healthapp.auth.domain.model.User

interface AuthRepository {
    suspend fun signUpWithEmail(email: String, password: String): Result<User>
    suspend fun signInWithEmail(email: String, password: String): Result<User>
    suspend fun saveUserProfile(user: User): Result<Unit>
    suspend fun getUserProfile(uid: String): Result<User?>
    suspend fun uploadProfilePhoto(uid: String, imageBytes: ByteArray): Result<String>
    fun getCurrentUserId(): String?
    suspend fun signOut()
}
