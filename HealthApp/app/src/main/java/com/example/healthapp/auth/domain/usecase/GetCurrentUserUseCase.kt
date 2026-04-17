package com.example.healthapp.auth.domain.usecase

import com.example.healthapp.auth.domain.repository.AuthRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): String? = authRepository.getCurrentUserId()
}
