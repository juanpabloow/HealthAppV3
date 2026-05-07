package com.example.healthapp.focus.domain.usecase

import com.example.healthapp.focus.domain.model.FocusSession
import com.example.healthapp.focus.domain.model.FocusStats
import com.example.healthapp.focus.domain.repository.FocusSessionRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class GetFocusStatsUseCase @Inject constructor(
    private val repository: FocusSessionRepository
) {
    suspend operator fun invoke(
        userId: String,
        fromMs: Long,
        toMs: Long
    ): Result<FocusStats> = repository.getSessions(userId, fromMs, toMs).map { sessions ->
        val total = sessions.size
        val completed = sessions.count { it.completed }
        val totalMins = sessions.sumOf { it.actualMinutes }
        val rate = if (total > 0) completed.toFloat() / total else 0f
        val byDay = computeByDay(sessions)
        val streak = computeStreak(byDay.keys)
        FocusStats(
            totalSessions = total,
            completedSessions = completed,
            totalMinutes = totalMins,
            completionRate = rate,
            currentStreakDays = streak,
            minutesByDay = byDay,
            periodStartMs = fromMs,
            periodEndMs = toMs
        )
    }

    private fun computeByDay(sessions: List<FocusSession>): Map<String, Int> {
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sessions
            .filter { it.completed && it.startedAt > 0L }
            .groupBy { fmt.format(Date(it.startedAt)) }
            .mapValues { (_, list) -> list.sumOf { it.actualMinutes } }
    }

    // Streak = consecutive days ending today (or yesterday if today has no completed session)
    private fun computeStreak(daysWithSessions: Set<String>): Int {
        if (daysWithSessions.isEmpty()) return 0
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val cal = Calendar.getInstance()
        val todayStr = fmt.format(cal.time)
        if (todayStr !in daysWithSessions) {
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }
        var streak = 0
        while (true) {
            val d = fmt.format(cal.time)
            if (d !in daysWithSessions) return streak
            streak++
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }
    }
}
