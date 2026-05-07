package com.example.healthapp.habits.domain.usecase

import com.example.healthapp.habits.domain.model.HabitCheckin
import com.example.healthapp.habits.domain.repository.HabitRepository
import com.example.healthapp.habits.domain.util.startOfDayMs
import javax.inject.Inject

class ToggleHabitCheckinUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    suspend operator fun invoke(
        userId: String,
        habitId: String,
        date: String,
        checked: Boolean,
        note: String? = null
    ): Result<Unit> {
        val checkin = HabitCheckin(
            id = "${habitId}_$date",
            habitId = habitId,
            userId = userId,
            date = date,
            dateMs = startOfDayMs(date),
            note = note,
            createdAt = System.currentTimeMillis()
        )
        return repository.toggleCheckin(checkin, checked)
    }
}
