package com.example.healthapp.focus.domain.repository

import com.example.healthapp.focus.domain.model.FocusSession

interface FocusSessionRepository {
    suspend fun saveSession(session: FocusSession): Result<String>
    suspend fun getSessions(userId: String, fromMs: Long, toMs: Long): Result<List<FocusSession>>
    suspend fun getRecentSessions(userId: String, limit: Int): Result<List<FocusSession>>
}
