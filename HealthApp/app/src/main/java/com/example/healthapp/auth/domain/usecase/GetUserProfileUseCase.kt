package com.example.healthapp.auth.domain.usecase

import com.example.healthapp.auth.domain.model.User
import com.example.healthapp.auth.domain.repository.AuthRepository
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(uid: String): Result<User?> = authRepository.getUserProfile(uid)
}
