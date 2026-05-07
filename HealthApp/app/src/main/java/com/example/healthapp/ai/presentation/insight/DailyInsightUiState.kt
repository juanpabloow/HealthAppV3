package com.example.healthapp.ai.presentation.insight

import com.example.healthapp.ai.domain.model.DailyInsight

data class DailyInsightUiState(
    val isLoading: Boolean = true,
    val isRegenerating: Boolean = false,
    val insight: DailyInsight? = null,
    val error: String? = null
)
