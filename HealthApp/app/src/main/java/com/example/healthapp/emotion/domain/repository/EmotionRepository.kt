package com.example.healthapp.emotion.domain.repository

import com.example.healthapp.emotion.domain.model.EmotionEntry

interface EmotionRepository {
    suspend fun saveEmotion(uid: String, entry: EmotionEntry): Result<Unit>
    suspend fun getEmotionByDate(uid: String, date: String): Result<EmotionEntry?>
    suspend fun getEmotionsByRange(
        uid: String,
        fromDate: String,
        toDate: String
    ): Result<List<EmotionEntry>>
}
