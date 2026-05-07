package com.example.healthapp.ai.domain.repository

import com.example.healthapp.ai.domain.model.DailyInsight
import com.example.healthapp.ai.domain.model.DailyInsightContext

interface DailyInsightRepository {
    /** Fetches today's cached insight, or null if not generated yet. */
    suspend fun getCached(uid: String, date: String): Result<DailyInsight?>

    /** Calls the model, caches the result in Firestore, and returns the parsed insight. */
    suspend fun generate(uid: String, context: DailyInsightContext): Result<DailyInsight>
}
