package com.example.healthapp.habits.domain.usecase

import com.example.healthapp.habits.domain.model.HabitCheckin
import com.example.healthapp.habits.domain.repository.HabitRepository
import javax.inject.Inject

class GetCheckinsForDateUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    suspend operator fun invoke(userId: String, date: String): Result<List<HabitCheckin>> =
        repository.getCheckinsForDate(userId, date)
}
