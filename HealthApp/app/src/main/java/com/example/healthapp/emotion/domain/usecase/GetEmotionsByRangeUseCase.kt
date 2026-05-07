package com.example.healthapp.emotion.domain.usecase

import com.example.healthapp.emotion.domain.model.EmotionEntry
import com.example.healthapp.emotion.domain.repository.EmotionRepository
import javax.inject.Inject

class GetEmotionsByRangeUseCase @Inject constructor(
    private val repository: EmotionRepository
) {
    suspend operator fun invoke(
        uid: String,
        fromDate: String,
        toDate: String
    ): Result<List<EmotionEntry>> =
        repository.getEmotionsByRange(uid, fromDate, toDate)
}
