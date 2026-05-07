package com.example.healthapp.habits.domain.usecase

import com.example.healthapp.habits.domain.repository.HabitRepository
import javax.inject.Inject

class DeleteHabitUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    suspend operator fun invoke(habitId: String, userId: String): Result<Unit> =
        repository.deleteHabit(habitId, userId)
}
