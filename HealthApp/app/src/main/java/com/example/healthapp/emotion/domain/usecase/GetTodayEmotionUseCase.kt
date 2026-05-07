package com.example.healthapp.emotion.domain.usecase

import com.example.healthapp.emotion.domain.model.EmotionEntry
import com.example.healthapp.emotion.domain.repository.EmotionRepository
import com.example.healthapp.emotion.domain.util.todayDateString
import javax.inject.Inject

class GetTodayEmotionUseCase @Inject constructor(
    private val repository: EmotionRepository
) {
    suspend operator fun invoke(uid: String): Result<EmotionEntry?> =
        repository.getEmotionByDate(uid, todayDateString())
}
