package com.example.healthapp.focus.domain.model

data class FocusSession(
    val id: String = "",
    val userId: String = "",
    val planId: String? = null,            // optional link to a Plan
    val startedAt: Long = 0L,              // epoch ms
    val endedAt: Long = 0L,                // epoch ms
    val plannedMinutes: Int = 25,
    val actualMinutes: Int = 0,
    val completed: Boolean = false,        // true = ran to end, false = aborted
    val mood: String? = null,              // Emotion.name or null
    val createdAt: Long = System.currentTimeMillis()
)
