package com.example.healthapp.mood.domain.model

data class MoodEntry(
    val id: String = "",
    val userId: String = "",
    val mood: String = "",       // "great" | "good" | "okay" | "bad" | "terrible"
    val note: String = "",
    val date: Long = 0L,         // start-of-day epoch ms — one entry per day per user
    val createdAt: Long = System.currentTimeMillis()
)
