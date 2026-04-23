package com.example.healthapp.plans.domain.usecase

import com.example.healthapp.plans.domain.model.Plan
import com.example.healthapp.plans.domain.repository.PlanRepository
import javax.inject.Inject

class GetPlansUseCase @Inject constructor(private val planRepository: PlanRepository) {
    suspend operator fun invoke(userId: String): Result<List<Plan>> =
        planRepository.getPlans(userId)
}
