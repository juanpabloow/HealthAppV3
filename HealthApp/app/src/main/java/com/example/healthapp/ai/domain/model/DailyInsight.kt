package com.example.healthapp.ai.domain.model

data class DailyInsight(
    val date: String = "",          // yyyy-MM-dd
    val emoji: String = "",          // 🟢 / 🟡 / 🔴
    val headline: String = "",       // ≤ 15 words
    val supportingText: String = "", // ≤ 25 words
    val generatedAt: Long = 0L,
    val modelUsed: String = ""       // e.g. "gpt-4o-mini" or "fallback"
)
