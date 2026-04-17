package com.example.healthapp.auth.domain.usecase

import com.example.healthapp.auth.domain.repository.AuthRepository
import javax.inject.Inject

class UploadProfilePhotoUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(uid: String, imageBytes: ByteArray): Result<String> =
        authRepository.uploadProfilePhoto(uid, imageBytes)
}
