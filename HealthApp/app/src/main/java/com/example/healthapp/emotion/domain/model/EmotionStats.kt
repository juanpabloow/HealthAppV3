package com.example.healthapp.emotion.domain.model

data class EmotionStats(
    val counts: Map<Emotion, Int> = emptyMap(),
    val mostFrequent: Emotion? = null,
    val totalEntries: Int = 0,
    val periodStart: String = "",   // yyyy-MM-dd inclusive
    val periodEnd: String = ""      // yyyy-MM-dd inclusive
)
