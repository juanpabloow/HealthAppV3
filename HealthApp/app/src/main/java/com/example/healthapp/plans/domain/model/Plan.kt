package com.example.healthapp.plans.domain.model

data class Plan(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val icon: String = "📋",
    val description: String = "",
    val blockedApps: List<String> = emptyList(),
    val scheduleDays: List<Int> = emptyList(), // 0 = Sun … 6 = Sat
    val startHour: Int = 9,
    val endHour: Int = 17,
    val strictness: String = "medium",  // "low" | "medium" | "high"
    val status: String = "active",      // "active" | "paused"
    val createdAt: Long = System.currentTimeMillis()
)
