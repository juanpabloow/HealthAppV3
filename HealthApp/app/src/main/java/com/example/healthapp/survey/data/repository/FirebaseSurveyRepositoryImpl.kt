package com.example.healthapp.survey.data.repository

import com.example.healthapp.survey.data.model.SurveyResponseDto
import com.example.healthapp.survey.domain.model.SurveyResponse
import com.example.healthapp.survey.domain.repository.SurveyRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseSurveyRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : SurveyRepository {

    private val collection = firestore.collection("survey_responses")

    override suspend fun saveSurveyResponse(response: SurveyResponse): Result<Unit> {
        return try {
            val dto = SurveyResponseDto.fromDomain(response)
            collection.document(response.userId).set(dto).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSurveyResponse(userId: String): Result<SurveyResponse?> {
        return try {
            val snapshot = collection.document(userId).get().await()
            val dto = snapshot.toObject(SurveyResponseDto::class.java)
            Result.success(dto?.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
