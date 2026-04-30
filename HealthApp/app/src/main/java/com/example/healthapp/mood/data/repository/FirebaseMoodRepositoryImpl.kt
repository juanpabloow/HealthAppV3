package com.example.healthapp.mood.data.repository

import com.example.healthapp.mood.data.model.MoodEntryDto
import com.example.healthapp.mood.domain.model.MoodEntry
import com.example.healthapp.mood.domain.repository.MoodRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseMoodRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : MoodRepository {

    // Document ID = "{userId}_{date}" enforces one entry per day per user
    private val col = firestore.collection("moodEntries")

    override suspend fun saveMoodEntry(entry: MoodEntry): Result<Unit> = try {
        val docId = "${entry.userId}_${entry.date}"
        val dto = MoodEntryDto.fromDomain(entry).copy(id = docId)
        col.document(docId).set(dto).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getMoodEntries(userId: String): Result<List<MoodEntry>> = try {
        val snap = col.whereEqualTo("userId", userId).get().await()
        val entries = snap.documents
            .mapNotNull { it.toObject(MoodEntryDto::class.java)?.toDomain() }
            .sortedByDescending { it.date }
        Result.success(entries)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getTodayEntry(userId: String, date: Long): Result<MoodEntry?> = try {
        val docId = "${userId}_${date}"
        val doc = col.document(docId).get().await()
        val entry = doc.toObject(MoodEntryDto::class.java)?.toDomain()
        Result.success(entry)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
