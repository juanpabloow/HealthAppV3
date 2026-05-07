package com.example.healthapp.emotion.data.model

import com.example.healthapp.emotion.domain.model.Emotion
import com.example.healthapp.emotion.domain.model.EmotionEntry

data class EmotionEntryDto(
    val date: String = "",
    val emotion: String = "",
    val note: String? = null,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
) {
    fun toDomain(): EmotionEntry? {
        val parsed = Emotion.fromName(emotion) ?: return null
        return EmotionEntry(
            date = date,
            emotion = parsed,
            note = note?.takeIf { it.isNotBlank() },
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun fromDomain(entry: EmotionEntry): EmotionEntryDto = EmotionEntryDto(
            date = entry.date,
            emotion = entry.emotion.name,
            note = entry.note,
            createdAt = entry.createdAt,
            updatedAt = entry.updatedAt
        )
    }
}
