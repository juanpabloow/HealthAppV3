package com.example.healthapp.plans.domain.model

data class PlanMetrics(
    val sessionsCompleted: Int = 0,
    val deviationPercent: Int = 0,
    val focusHours: Int = 0,
    val weeklyProgressPercent: Int = 0
)
