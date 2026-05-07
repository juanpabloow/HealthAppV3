package com.example.healthapp.emotion.domain.usecase

import com.example.healthapp.emotion.domain.model.EmotionStats
import com.example.healthapp.emotion.domain.repository.EmotionRepository
import javax.inject.Inject

class GetEmotionStatsUseCase @Inject constructor(
    private val repository: EmotionRepository
) {
    suspend operator fun invoke(
        uid: String,
        fromDate: String,
        toDate: String
    ): Result<EmotionStats> =
        repository.getEmotionsByRange(uid, fromDate, toDate).map { entries ->
            val counts = entries.groupingBy { it.emotion }.eachCount()
            val mostFrequent = counts.maxByOrNull { it.value }?.key
            EmotionStats(
                counts = counts,
                mostFrequent = mostFrequent,
                totalEntries = entries.size,
                periodStart = fromDate,
                periodEnd = toDate
            )
        }
}
