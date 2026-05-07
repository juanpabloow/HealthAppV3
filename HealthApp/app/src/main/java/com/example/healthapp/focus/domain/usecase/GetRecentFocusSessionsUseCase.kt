package com.example.healthapp.focus.domain.usecase

import com.example.healthapp.focus.domain.model.FocusSession
import com.example.healthapp.focus.domain.repository.FocusSessionRepository
import javax.inject.Inject

class GetRecentFocusSessionsUseCase @Inject constructor(
    private val repository: FocusSessionRepository
) {
    suspend operator fun invoke(userId: String, limit: Int = 50): Result<List<FocusSession>> =
        repository.getRecentSessions(userId, limit)
}
