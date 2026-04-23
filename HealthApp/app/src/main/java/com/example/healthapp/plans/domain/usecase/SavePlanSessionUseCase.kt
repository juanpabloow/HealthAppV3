package com.example.healthapp.plans.domain.usecase

import com.example.healthapp.plans.domain.model.PlanSession
import com.example.healthapp.plans.domain.repository.PlanRepository
import javax.inject.Inject

class SavePlanSessionUseCase @Inject constructor(private val planRepository: PlanRepository) {
    suspend operator fun invoke(session: PlanSession): Result<Unit> =
        planRepository.saveSession(session)
}
