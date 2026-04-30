package com.example.healthapp.mood.domain.repository

import com.example.healthapp.mood.domain.model.MoodEntry

interface MoodRepository {
    suspend fun saveMoodEntry(entry: MoodEntry): Result<Unit>
    suspend fun getMoodEntries(userId: String): Result<List<MoodEntry>>
    suspend fun getTodayEntry(userId: String, date: Long): Result<MoodEntry?>
}
