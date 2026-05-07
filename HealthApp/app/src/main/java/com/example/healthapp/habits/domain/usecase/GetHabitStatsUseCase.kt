package com.example.healthapp.habits.domain.usecase

import com.example.healthapp.habits.domain.model.HabitStats
import com.example.healthapp.habits.domain.repository.HabitRepository
import com.example.healthapp.habits.domain.util.computeHabitStats
import javax.inject.Inject

class GetHabitStatsUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    suspend operator fun invoke(userId: String, habitId: String): Result<HabitStats> =
        repository.getCheckins(userId, habitId, 0L, System.currentTimeMillis())
            .map { computeHabitStats(it) }
}
