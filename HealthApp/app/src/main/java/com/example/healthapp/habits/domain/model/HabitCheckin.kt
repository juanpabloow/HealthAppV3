package com.example.healthapp.habits.domain.model

data class HabitCheckin(
    val id: String = "",          // "{habitId}_{yyyy-MM-dd}" — idempotent upsert key
    val habitId: String = "",
    val userId: String = "",
    val date: String = "",        // yyyy-MM-dd
    val dateMs: Long = 0L,        // start-of-day epoch ms (range queries)
    val note: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
