package com.example.healthapp.habits.domain.usecase

import com.example.healthapp.habits.domain.model.Habit
import com.example.healthapp.habits.domain.repository.HabitRepository
import javax.inject.Inject

class SaveHabitUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    suspend operator fun invoke(habit: Habit): Result<String> =
        repository.saveHabit(habit)
}
