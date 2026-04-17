package com.example.healthapp.survey.domain.model

data class SurveyResponse(
    val userId: String = "",
    val primaryGoal: String = "",
    val keepingFactors: List<String> = emptyList(),
    val enjoyedActivities: List<String> = emptyList(),
    val todayMood: String = "",
    val completedAt: Long = 0L
)
