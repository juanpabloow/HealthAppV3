package com.example.healthapp.habits.domain.repository

import com.example.healthapp.habits.domain.model.Habit
import com.example.healthapp.habits.domain.model.HabitCheckin

interface HabitRepository {
    suspend fun getHabits(userId: String): Result<List<Habit>>
    suspend fun saveHabit(habit: Habit): Result<String>
    suspend fun deleteHabit(habitId: String): Result<Unit>
    suspend fun toggleCheckin(checkin: HabitCheckin, checked: Boolean): Result<Unit>
    suspend fun getCheckins(
        userId: String,
        habitId: String,
        fromMs: Long,
        toMs: Long
    ): Result<List<HabitCheckin>>
    suspend fun getCheckinsForDate(userId: String, date: String): Result<List<HabitCheckin>>
}
