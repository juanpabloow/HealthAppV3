package com.example.healthapp.ai.data.dto

import com.example.healthapp.ai.domain.model.DailyInsight

/** Firestore cache shape — `users/{uid}/aiInsights/{yyyy-MM-dd}`. */
data class DailyInsightCacheDto(
    val date: String = "",
    val emoji: String = "",
    val headline: String = "",
    val supportingText: String = "",
    val generatedAt: Long = 0L,
    val modelUsed: String = ""
) {
    fun toDomain(): DailyInsight = DailyInsight(
        date = date,
        emoji = emoji,
        headline = headline,
        supportingText = supportingText,
        generatedAt = generatedAt,
        modelUsed = modelUsed
    )

    companion object {
        fun fromDomain(insight: DailyInsight): DailyInsightCacheDto = DailyInsightCacheDto(
            date = insight.date,
            emoji = insight.emoji,
            headline = insight.headline,
            supportingText = insight.supportingText,
            generatedAt = insight.generatedAt,
            modelUsed = insight.modelUsed
        )
    }
}
