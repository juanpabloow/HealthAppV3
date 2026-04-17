package com.example.healthapp.survey.domain.usecase

import com.example.healthapp.survey.domain.model.SurveyResponse
import com.example.healthapp.survey.domain.repository.SurveyRepository
import javax.inject.Inject

class SaveSurveyResponseUseCase @Inject constructor(
    private val surveyRepository: SurveyRepository
) {
    suspend operator fun invoke(response: SurveyResponse): Result<Unit> =
        surveyRepository.saveSurveyResponse(response)
}
