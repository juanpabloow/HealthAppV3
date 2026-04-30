package com.example.healthapp.mood.domain.usecase

import com.example.healthapp.mood.domain.model.MoodEntry
import com.example.healthapp.mood.domain.repository.MoodRepository
import javax.inject.Inject

class GetTodayMoodEntryUseCase @Inject constructor(
    private val repository: MoodRepository
) {
    suspend operator fun invoke(userId: String, date: Long): Result<MoodEntry?> =
        repository.getTodayEntry(userId, date)
}
