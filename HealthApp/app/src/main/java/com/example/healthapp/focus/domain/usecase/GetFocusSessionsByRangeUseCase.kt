package com.example.healthapp.focus.domain.usecase

import com.example.healthapp.focus.domain.model.FocusSession
import com.example.healthapp.focus.domain.repository.FocusSessionRepository
import javax.inject.Inject

class GetFocusSessionsByRangeUseCase @Inject constructor(
    private val repository: FocusSessionRepository
) {
    suspend operator fun invoke(
        userId: String,
        fromMs: Long,
        toMs: Long
    ): Result<List<FocusSession>> = repository.getSessions(userId, fromMs, toMs)
}
