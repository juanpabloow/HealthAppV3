package com.example.healthapp.habits.domain.usecase

import com.example.healthapp.habits.domain.model.HabitCheckin
import com.example.healthapp.habits.domain.repository.HabitRepository
import javax.inject.Inject

class GetHabitCheckinsUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    suspend operator fun invoke(
        userId: String,
        habitId: String,
        fromMs: Long,
        toMs: Long
    ): Result<List<HabitCheckin>> = repository.getCheckins(userId, habitId, fromMs, toMs)
}
