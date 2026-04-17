package com.example.healthapp.survey.domain.repository

import com.example.healthapp.survey.domain.model.SurveyResponse

interface SurveyRepository {
    suspend fun saveSurveyResponse(response: SurveyResponse): Result<Unit>
    suspend fun getSurveyResponse(userId: String): Result<SurveyResponse?>
}
