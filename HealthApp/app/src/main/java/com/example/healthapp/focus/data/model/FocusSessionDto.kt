package com.example.healthapp.focus.data.model

import com.example.healthapp.focus.domain.model.FocusSession

data class FocusSessionDto(
    val id: String = "",
    val userId: String = "",
    val planId: String? = null,
    val startedAt: Long = 0L,
    val endedAt: Long = 0L,
    // Firestore stores numbers as Long — keep Long here, convert in toDomain()
    val plannedMinutes: Long = 0L,
    val actualMinutes: Long = 0L,
    val completed: Boolean = false,
    val mood: String? = null,
    val createdAt: Long = 0L
) {
    fun toDomain(): FocusSession = FocusSession(
        id = id,
        userId = userId,
        planId = planId,
        startedAt = startedAt,
        endedAt = endedAt,
        plannedMinutes = plannedMinutes.toInt(),
        actualMinutes = actualMinutes.toInt(),
        completed = completed,
        mood = mood,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(s: FocusSession): FocusSessionDto = FocusSessionDto(
            id = s.id,
            userId = s.userId,
            planId = s.planId,
            startedAt = s.startedAt,
            endedAt = s.endedAt,
            plannedMinutes = s.plannedMinutes.toLong(),
            actualMinutes = s.actualMinutes.toLong(),
            completed = s.completed,
            mood = s.mood,
            createdAt = s.createdAt
        )
    }
}
