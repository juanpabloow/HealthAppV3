package com.example.healthapp.emotion.domain.model

data class EmotionEntry(
    val date: String = "",                    // yyyy-MM-dd
    val emotion: Emotion = Emotion.CALM,
    val note: String? = null,                 // max 280 chars (enforced in UI)
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
