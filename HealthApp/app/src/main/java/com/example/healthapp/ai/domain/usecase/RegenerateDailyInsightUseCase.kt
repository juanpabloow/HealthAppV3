package com.example.healthapp.ai.domain.usecase

import com.example.healthapp.ai.domain.model.DailyInsight
import javax.inject.Inject

/** Skips the cache and forces a fresh API call. */
class RegenerateDailyInsightUseCase @Inject constructor(
    private val getDailyInsight: GetDailyInsightUseCase
) {
    suspend operator fun invoke(): Result<DailyInsight> =
        getDailyInsight(forceRefresh = true)
}
