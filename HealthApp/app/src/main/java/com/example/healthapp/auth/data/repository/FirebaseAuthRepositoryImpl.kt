package com.example.healthapp.auth.data.repository

import com.example.healthapp.auth.data.model.UserDto
import com.example.healthapp.auth.domain.model.User
import com.example.healthapp.auth.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : AuthRepository {

    private val usersCollection = firestore.collection("users")

    override suspend fun signUpWithEmail(email: String, password: String): Result<User> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: return Result.failure(Exception("Error al crear usuario"))
            val user = User(
                uid = firebaseUser.uid,
                email = firebaseUser.email,
                phone = null,
                displayName = null,
                ageRange = null,
                photoUrl = null,
                createdAt = System.currentTimeMillis()
            )
            // Guardamos el usuario en Firestore automáticamente al registrarse
            saveUserProfile(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<User> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: return Result.failure(Exception("Error al iniciar sesión"))
            val profile = getUserProfile(firebaseUser.uid).getOrNull()
            val user = profile ?: User(
                uid = firebaseUser.uid,
                email = firebaseUser.email,
                phone = null,
                displayName = null,
                ageRange = null,
                photoUrl = null,
                createdAt = System.currentTimeMillis()
            )
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveUserProfile(user: User): Result<Unit> {
        return try {
            val dto = UserDto.fromDomain(user)
            usersCollection.document(user.uid).set(dto).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserProfile(uid: String): Result<User?> {
        return try {
            val snapshot = usersCollection.document(uid).get().await()
            val dto = snapshot.toObject(UserDto::class.java)
            Result.success(dto?.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadProfilePhoto(uid: String, imageBytes: ByteArray): Result<String> {
        return try {
            val ref = storage.reference.child("profile_photos/$uid.jpg")
            ref.putBytes(imageBytes).await()
            val url = ref.downloadUrl.await().toString()
            Result.success(url)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentUserId(): String? = firebaseAuth.currentUser?.uid

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }
}
