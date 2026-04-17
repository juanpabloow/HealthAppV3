package com.example.healthapp.dashboard.presentation

import com.example.healthapp.dashboard.domain.model.DailyScreenTime
import com.example.healthapp.dashboard.domain.model.WeeklyScreenTime

data class DashboardUiState(
    val hasUsagePermission: Boolean = false,
    val selectedDateMs: Long = System.currentTimeMillis(),
    val selectedTab: ScreenTimeTab = ScreenTimeTab.DAY,
    val dailyData: DailyScreenTime? = null,
    val weeklyData: WeeklyScreenTime? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

enum class ScreenTimeTab { DAY, WEEK, MONTH }
