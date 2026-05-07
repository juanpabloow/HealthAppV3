package com.example.healthapp.habits.domain.model

data class Habit(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val icon: String = "🌱",
    val description: String = "",
    val targetDays: List<Int> = listOf(0, 1, 2, 3, 4, 5, 6), // 0=Sun … 6=Sat
    val reminderHour: Int? = null,
    val color: String = "#43A047", // hex
    val status: String = "active", // "active" | "archived"
    val createdAt: Long = System.currentTimeMillis()
)
