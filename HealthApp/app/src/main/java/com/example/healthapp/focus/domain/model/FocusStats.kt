package com.example.healthapp.focus.domain.model

data class FocusStats(
    val totalSessions: Int = 0,
    val completedSessions: Int = 0,
    val totalMinutes: Int = 0,
    val completionRate: Float = 0f,                      // 0..1
    val currentStreakDays: Int = 0,
    val minutesByDay: Map<String, Int> = emptyMap(),     // yyyy-MM-dd -> minutes
    val periodStartMs: Long = 0L,
    val periodEndMs: Long = 0L
)
