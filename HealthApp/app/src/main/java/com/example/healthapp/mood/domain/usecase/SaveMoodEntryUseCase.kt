package com.example.healthapp.mood.domain.usecase

import com.example.healthapp.mood.domain.model.MoodEntry
import com.example.healthapp.mood.domain.repository.MoodRepository
import javax.inject.Inject

class SaveMoodEntryUseCase @Inject constructor(
    private val repository: MoodRepository
) {
    suspend operator fun invoke(entry: MoodEntry): Result<Unit> = repository.saveMoodEntry(entry)
}
