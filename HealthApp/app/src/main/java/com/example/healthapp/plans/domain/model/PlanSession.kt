package com.example.healthapp.plans.domain.model

data class PlanSession(
    val id: String = "",
    val planId: String = "",
    val userId: String = "",
    val date: Long = 0L,              // start-of-day epoch ms
    val scheduledMinutes: Int = 0,
    val completed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
