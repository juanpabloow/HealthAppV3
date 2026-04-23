package com.example.healthapp.plans.domain.usecase

import com.example.healthapp.plans.domain.repository.PlanRepository
import javax.inject.Inject

class DeletePlanUseCase @Inject constructor(private val planRepository: PlanRepository) {
    suspend operator fun invoke(planId: String): Result<Unit> =
        planRepository.deletePlan(planId)
}
