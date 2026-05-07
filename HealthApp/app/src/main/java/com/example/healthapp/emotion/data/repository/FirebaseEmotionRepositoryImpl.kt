package com.example.healthapp.emotion.data.repository

import com.example.healthapp.emotion.data.model.EmotionEntryDto
import com.example.healthapp.emotion.domain.model.EmotionEntry
import com.example.healthapp.emotion.domain.repository.EmotionRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseEmotionRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : EmotionRepository {

    private fun userEmotions(uid: String) =
        firestore.collection("users").document(uid).collection("emotions")

    override suspend fun saveEmotion(uid: String, entry: EmotionEntry): Result<Unit> = try {
        val dto = EmotionEntryDto.fromDomain(entry)
        userEmotions(uid).document(entry.date).set(dto).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getEmotionByDate(uid: String, date: String): Result<EmotionEntry?> = try {
        val snap = userEmotions(uid).document(date).get().await()
        val dto = snap.toObject(EmotionEntryDto::class.java)
        Result.success(dto?.toDomain())
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getEmotionsByRange(
        uid: String,
        fromDate: String,
        toDate: String
    ): Result<List<EmotionEntry>> = try {
        val snap = userEmotions(uid)
            .whereGreaterThanOrEqualTo("date", fromDate)
            .whereLessThanOrEqualTo("date", toDate)
            .orderBy("date")
            .get()
            .await()
        val entries = snap.documents.mapNotNull {
            it.toObject(EmotionEntryDto::class.java)?.toDomain()
        }
        Result.success(entries)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
