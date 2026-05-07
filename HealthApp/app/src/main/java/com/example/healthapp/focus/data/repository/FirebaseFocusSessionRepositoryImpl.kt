package com.example.healthapp.focus.data.repository

import com.example.healthapp.focus.data.model.FocusSessionDto
import com.example.healthapp.focus.domain.model.FocusSession
import com.example.healthapp.focus.domain.repository.FocusSessionRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseFocusSessionRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FocusSessionRepository {

    private val sessionsCol = firestore.collection("focusSessions")

    override suspend fun saveSession(session: FocusSession): Result<String> = try {
        val ref = if (session.id.isBlank()) sessionsCol.document() else sessionsCol.document(session.id)
        val dto = FocusSessionDto.fromDomain(session).copy(id = ref.id)
        ref.set(dto).await()
        Result.success(ref.id)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getSessions(
        userId: String,
        fromMs: Long,
        toMs: Long
    ): Result<List<FocusSession>> = try {
        // Single equality query + in-memory filter/sort matches the existing
        // PlanRepository pattern and avoids requiring a Firestore composite index.
        val snap = sessionsCol.whereEqualTo("userId", userId).get().await()
        val list = snap.documents
            .mapNotNull { it.toObject(FocusSessionDto::class.java)?.toDomain() }
            .filter { it.startedAt in fromMs..toMs }
            .sortedByDescending { it.startedAt }
        Result.success(list)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getRecentSessions(
        userId: String,
        limit: Int
    ): Result<List<FocusSession>> = try {
        val snap = sessionsCol.whereEqualTo("userId", userId).get().await()
        val list = snap.documents
            .mapNotNull { it.toObject(FocusSessionDto::class.java)?.toDomain() }
            .sortedByDescending { it.startedAt }
            .take(limit)
        Result.success(list)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
