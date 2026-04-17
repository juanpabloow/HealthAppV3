package com.example.healthapp.survey.data.model

import com.example.healthapp.survey.domain.model.SurveyResponse

data class SurveyResponseDto(
    val userId: String = "",
    val primaryGoals: List<String> = emptyList(),
    val worries: List<String> = emptyList(),
    val activities: List<String> = emptyList(),
    val mood: String = "",
    val completedAt: Long = 0L
) {
    fun toDomain(): SurveyResponse = SurveyResponse(
        userId = userId,
        primaryGoal = primaryGoals.joinToString(","),
        keepingFactors = worries,
        enjoyedActivities = activities,
        todayMood = mood,
        completedAt = completedAt
    )

    companion object {
        fun fromDomain(r: SurveyResponse): SurveyResponseDto = SurveyResponseDto(
            userId = r.userId,
            primaryGoals = r.primaryGoal.split(",").filter { it.isNotBlank() },
            worries = r.keepingFactors,
            activities = r.enjoyedActivities,
            mood = r.todayMood,
            completedAt = r.completedAt
        )
    }
}
