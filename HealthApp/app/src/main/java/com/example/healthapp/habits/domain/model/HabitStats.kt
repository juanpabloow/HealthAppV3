package com.example.healthapp.habits.domain.model

data class HabitStats(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val completionRate30d: Float = 0f,   // 0..1
    val lastCheckinDate: String? = null, // yyyy-MM-dd
    val totalCheckins: Int = 0
)
