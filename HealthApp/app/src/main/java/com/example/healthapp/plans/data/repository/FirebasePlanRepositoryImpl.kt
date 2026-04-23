package com.example.healthapp.plans.data.repository

import com.example.healthapp.plans.data.model.PlanDto
import com.example.healthapp.plans.data.model.PlanSessionDto
import com.example.healthapp.plans.domain.model.Plan
import com.example.healthapp.plans.domain.model.PlanSession
import com.example.healthapp.plans.domain.repository.PlanRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebasePlanRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : PlanRepository {

    private val plansCol = firestore.collection("plans")
    private val sessionsCol = firestore.collection("planSessions")

    override suspend fun getPlans(userId: String): Result<List<Plan>> = try {
        val snap = plansCol.whereEqualTo("userId", userId).get().await()
        val plans = snap.documents.mapNotNull { it.toObject(PlanDto::class.java)?.toDomain() }
        Result.success(plans)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun savePlan(plan: Plan): Result<String> = try {
        val ref = if (plan.id.isBlank()) plansCol.document() else plansCol.document(plan.id)
        val dto = PlanDto.fromDomain(plan).copy(id = ref.id)
        ref.set(dto).await()
        Result.success(ref.id)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun deletePlan(planId: String): Result<Unit> = try {
        plansCol.document(planId).delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getSessions(planId: String): Result<List<PlanSession>> = try {
        val snap = sessionsCol.whereEqualTo("planId", planId).get().await()
        val sessions = snap.documents.mapNotNull { it.toObject(PlanSessionDto::class.java)?.toDomain() }
        Result.success(sessions)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun saveSession(session: PlanSession): Result<Unit> = try {
        val ref = if (session.id.isBlank()) sessionsCol.document() else sessionsCol.document(session.id)
        val dto = PlanSessionDto.fromDomain(session).copy(id = ref.id)
        ref.set(dto).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun hasAnyPlan(userId: String): Result<Boolean> = try {
        val snap = plansCol.whereEqualTo("userId", userId).limit(1).get().await()
        Result.success(!snap.isEmpty)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
