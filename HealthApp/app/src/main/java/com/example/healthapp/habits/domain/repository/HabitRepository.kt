package com.example.healthapp.habits.domain.repository

import com.example.healthapp.habits.domain.model.Habit
import com.example.healthapp.habits.domain.model.HabitCheckin

interface HabitRepository {
    suspend fun getHabits(userId: String): Result<List<Habit>>
    suspend fun saveHabit(habit: Habit): Result<String>
    suspend fun deleteHabit(habitId: String, userId: String): Result<Unit>
    suspend fun toggleCheckin(checkin: HabitCheckin, checked: Boolean): Result<Unit>
    suspend fun getCheckins(
        userId: String,
        habitId: String,
        fromMs: Long,
        toMs: Long
    ): Result<List<HabitCheckin>>
    suspend fun getCheckinsForDate(userId: String, date: String): Result<List<HabitCheckin>>

    /**
     * All check-ins for [userId] across all habits whose `dateMs` falls in
     * the range fromMs..toMs. Used by features that need user-wide history
     * (e.g. computing the top current streak across habits) in a single
     * Firestore read instead of one per habit.
     */
    suspend fun getRecentCheckinsForUser(
        userId: String,
        fromMs: Long,
        toMs: Long
    ): Result<List<HabitCheckin>>
}
