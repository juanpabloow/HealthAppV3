package com.example.healthapp.mood.data.model

import com.example.healthapp.mood.domain.model.MoodEntry

data class MoodEntryDto(
    val id: String = "",
    val userId: String = "",
    val mood: String = "",
    val note: String = "",
    val date: Long = 0L,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): MoodEntry = MoodEntry(
        id = id,
        userId = userId,
        mood = mood,
        note = note,
        date = date,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(entry: MoodEntry): MoodEntryDto = MoodEntryDto(
            id = entry.id,
            userId = entry.userId,
            mood = entry.mood,
            note = entry.note,
            date = entry.date,
            createdAt = entry.createdAt
        )
    }
}
