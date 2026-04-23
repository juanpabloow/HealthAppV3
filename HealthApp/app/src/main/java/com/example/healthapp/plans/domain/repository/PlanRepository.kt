package com.example.healthapp.plans.domain.repository

import com.example.healthapp.plans.domain.model.Plan
import com.example.healthapp.plans.domain.model.PlanSession

interface PlanRepository {
    suspend fun getPlans(userId: String): Result<List<Plan>>
    suspend fun savePlan(plan: Plan): Result<String>          // returns final planId
    suspend fun deletePlan(planId: String): Result<Unit>
    suspend fun getSessions(planId: String): Result<List<PlanSession>>
    suspend fun saveSession(session: PlanSession): Result<Unit>
    suspend fun hasAnyPlan(userId: String): Result<Boolean>
}
