package com.example.healthapp.plans.presentation

import com.example.healthapp.plans.domain.model.Plan
import com.example.healthapp.plans.domain.model.PlanSession

data class PlanUiState(
    val plans: List<Plan> = emptyList(),
    val sessions: Map<String, List<PlanSession>> = emptyMap(), // planId → sessions
    val isLoading: Boolean = false,
    val error: String? = null
)

data class CreatePlanUiState(
    val step: Int = 1,
    val name: String = "",
    val icon: String = "📋",
    val description: String = "",
    val blockedApps: List<String> = emptyList(),
    val scheduleDays: List<Int> = emptyList(),
    val startHour: Int = 9,
    val endHour: Int = 17,
    val strictness: String = "medium",
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
    val editingPlanId: String? = null
)
