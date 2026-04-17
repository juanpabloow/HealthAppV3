package com.example.healthapp.auth.domain.usecase

import com.example.healthapp.auth.domain.model.User
import com.example.healthapp.auth.domain.repository.AuthRepository
import javax.inject.Inject

class SignUpWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> =
        authRepository.signUpWithEmail(email, password)
}
