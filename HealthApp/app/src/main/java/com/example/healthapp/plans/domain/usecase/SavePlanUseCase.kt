package com.example.healthapp.plans.domain.usecase

import com.example.healthapp.plans.domain.model.Plan
import com.example.healthapp.plans.domain.repository.PlanRepository
import javax.inject.Inject

class SavePlanUseCase @Inject constructor(private val planRepository: PlanRepository) {
    suspend operator fun invoke(plan: Plan): Result<String> =
        planRepository.savePlan(plan)
}
