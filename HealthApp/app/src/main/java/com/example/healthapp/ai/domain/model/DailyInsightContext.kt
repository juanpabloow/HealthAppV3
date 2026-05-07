package com.example.healthapp.ai.domain.model

/**
 * Pure data carrier — everything the prompt builder needs about the user,
 * gathered in one shot by [com.example.healthapp.ai.domain.usecase.GetDailyInsightUseCase].
 */
data class DailyInsightContext(
    val today: String,                // yyyy-MM-dd
    val dayName: String,              // "Tuesday"
    val userName: String?,
    val ageRange: String?,
    val goals: List<String>,
    val worries: List<String>,
    val moodToday: MoodSnapshot?,
    val moodYesterday: MoodSnapshot?,
    val screenTime: ScreenTimeSnapshot?,
    val habits: HabitsSnapshot?,
    val focusSessions: FocusSessionsSnapshot?
) {
    data class MoodSnapshot(val emotion: String, val note: String?)

    data class ScreenTimeSnapshot(
        val todayMinutes: Int,
        val sevenDayAverageMinutes: Int,
        val deltaPercent: Int,           // signed: -22 means 22% below avg
        val topAppToday: TopApp?
    ) {
        data class TopApp(val name: String, val minutes: Int)
    }

    data class HabitsSnapshot(
        val totalActive: Int,
        val completedToday: Int,
        val topStreak: TopStreak?
    ) {
        data class TopStreak(val habitName: String, val currentStreakDays: Int)
    }

    data class FocusSessionsSnapshot(
        val completedToday: Int,
        val totalMinutesToday: Int,
        val abortedToday: Int
    )

    /** True when there's enough signal to ask the model for a real insight. */
    val hasMinimumSignal: Boolean
        get() = moodToday != null
                || moodYesterday != null
                || (screenTime?.todayMinutes ?: 0) > 0
                || (habits?.totalActive ?: 0) > 0
                || (focusSessions?.completedToday ?: 0) > 0
}
