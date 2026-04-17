package com.example.healthapp.survey.presentation

data class SurveyUiState(
    val selectedGoals: List<String> = emptyList(),
    val selectedWorries: List<String> = emptyList(),
    val selectedActivities: List<String> = emptyList(),
    val selectedMood: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isCompleted: Boolean = false
)
