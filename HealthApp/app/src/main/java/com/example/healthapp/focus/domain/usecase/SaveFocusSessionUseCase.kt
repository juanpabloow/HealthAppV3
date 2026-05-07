package com.example.healthapp.focus.domain.usecase

import com.example.healthapp.focus.domain.model.FocusSession
import com.example.healthapp.focus.domain.repository.FocusSessionRepository
import javax.inject.Inject

class SaveFocusSessionUseCase @Inject constructor(
    private val repository: FocusSessionRepository
) {
    suspend operator fun invoke(session: FocusSession): Result<String> =
        repository.saveSession(session)
}
