package com.example.healthapp.dashboard.domain.repository

import com.example.healthapp.dashboard.domain.model.DailyScreenTime
import com.example.healthapp.dashboard.domain.model.WeeklyScreenTime

interface ScreenTimeRepository {
    fun hasUsagePermission(): Boolean
    suspend fun getDailyScreenTime(dateMillis: Long): Result<DailyScreenTime>
    suspend fun getWeeklyScreenTime(weekOffset: Int = 0): Result<WeeklyScreenTime>
    suspend fun cacheDailyData(userId: String, data: DailyScreenTime): Result<Unit>
}
