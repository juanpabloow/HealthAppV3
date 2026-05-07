package com.example.healthapp.emotion.domain.usecase

import com.example.healthapp.emotion.domain.model.EmotionEntry
import com.example.healthapp.emotion.domain.repository.EmotionRepository
import javax.inject.Inject

class SaveEmotionUseCase @Inject constructor(
    private val repository: EmotionRepository
) {
    suspend operator fun invoke(uid: String, entry: EmotionEntry): Result<Unit> =
        repository.saveEmotion(uid, entry)
}
