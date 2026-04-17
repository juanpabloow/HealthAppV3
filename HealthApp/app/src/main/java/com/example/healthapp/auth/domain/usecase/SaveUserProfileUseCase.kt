package com.example.healthapp.auth.domain.usecase

import com.example.healthapp.auth.domain.model.User
import com.example.healthapp.auth.domain.repository.AuthRepository
import javax.inject.Inject

class SaveUserProfileUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(user: User): Result<Unit> =
        authRepository.saveUserProfile(user)
}
