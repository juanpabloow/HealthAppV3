package com.example.healthapp.ai.domain.usecase

import com.example.healthapp.ai.domain.model.DailyInsight
import com.example.healthapp.ai.domain.model.DailyInsightContext
import com.example.healthapp.ai.domain.repository.DailyInsightRepository
import com.example.healthapp.auth.domain.repository.AuthRepository
import com.example.healthapp.auth.domain.usecase.GetCurrentUserUseCase
import com.example.healthapp.dashboard.domain.repository.ScreenTimeRepository
import com.example.healthapp.emotion.domain.repository.EmotionRepository
import com.example.healthapp.emotion.domain.util.daysAgoDateString
import com.example.healthapp.emotion.domain.util.todayDateString
import com.example.healthapp.focus.domain.repository.FocusSessionRepository
import com.example.healthapp.habits.domain.repository.HabitRepository
import com.example.healthapp.habits.domain.util.computeHabitStats
import com.example.healthapp.habits.domain.util.startOfDayMs
import com.example.healthapp.survey.domain.repository.SurveyRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class GetDailyInsightUseCase @Inject constructor(
    private val getCurrentUser: GetCurrentUserUseCase,
    private val authRepository: AuthRepository,
    private val emotionRepository: EmotionRepository,
    private val screenTimeRepository: ScreenTimeRepository,
    private val habitRepository: HabitRepository,
    private val focusSessionRepository: FocusSessionRepository,
    private val surveyRepository: SurveyRepository,
    private val insightRepository: DailyInsightRepository
) {
    suspend operator fun invoke(forceRefresh: Boolean = false): Result<DailyInsight> {
        val uid = getCurrentUser()
            ?: return Result.failure(IllegalStateException("Not signed in"))
        val today = todayDateString()

        // Cache hit short-circuits the API
        if (!forceRefresh) {
            val cached = insightRepository.getCached(uid, today).getOrNull()
            if (cached != null) return Result.success(cached)
        }

        val context = buildContext(uid, today)

        // Sparsity guard — brand-new user with no signal at all gets a deterministic nudge
        if (!context.hasMinimumSignal) {
            return Result.success(buildSetupNudge(today))
        }

        return insightRepository.generate(uid, context)
    }

    private suspend fun buildContext(uid: String, today: String): DailyInsightContext = coroutineScope {
        val yesterday = daysAgoDateString(1)
        val dayName = SimpleDateFormat("EEEE", Locale.getDefault())
            .format(Calendar.getInstance().time)
        val startOfToday = startOfDayMs()
        val endOfToday = startOfToday + DAY_MS - 1L
        val ninetyDaysAgo = startOfToday - 90L * DAY_MS
        val nowMs = System.currentTimeMillis()

        val moodsDeferred = async { emotionRepository.getEmotionsByRange(uid, yesterday, today) }
        val dailyDeferred = async { screenTimeRepository.getDailyScreenTime(nowMs) }
        val weeklyDeferred = async { screenTimeRepository.getWeeklyScreenTime(0) }
        val habitsDeferred = async { habitRepository.getHabits(uid) }
        val checkinsTodayDeferred = async { habitRepository.getCheckinsForDate(uid, today) }
        val recentCheckinsDeferred = async {
            habitRepository.getRecentCheckinsForUser(uid, ninetyDaysAgo, nowMs)
        }
        val focusDeferred = async { focusSessionRepository.getSessions(uid, startOfToday, endOfToday) }
        val surveyDeferred = async { surveyRepository.getSurveyResponse(uid) }
        val userDeferred = async { authRepository.getUserProfile(uid) }

        val moods = moodsDeferred.await().getOrNull().orEmpty()
        val daily = dailyDeferred.await().getOrNull()
        val weekly = weeklyDeferred.await().getOrNull()
        val habits = habitsDeferred.await().getOrNull().orEmpty()
        val checkinsToday = checkinsTodayDeferred.await().getOrNull().orEmpty()
        val recentCheckins = recentCheckinsDeferred.await().getOrNull().orEmpty()
        val focusToday = focusDeferred.await().getOrNull().orEmpty()
        val survey = surveyDeferred.await().getOrNull()
        val user = userDeferred.await().getOrNull()

        val moodToday = moods.firstOrNull { it.date == today }
            ?.let { DailyInsightContext.MoodSnapshot(it.emotion.name, it.note) }
        val moodYesterday = moods.firstOrNull { it.date == yesterday }
            ?.let { DailyInsightContext.MoodSnapshot(it.emotion.name, it.note) }

        val screenTimeSnap = if (daily != null && weekly != null) {
            val avgMs = weekly.dailyAverageMs
            val todayMin = (daily.totalTimeMs / 60_000L).toInt()
            val avgMin = (avgMs / 60_000L).toInt()
            val deltaPercent = if (avgMin > 0) ((todayMin - avgMin) * 100 / avgMin) else 0
            val top = daily.apps.firstOrNull()?.let {
                DailyInsightContext.ScreenTimeSnapshot.TopApp(
                    name = it.appName,
                    minutes = (it.totalTimeMs / 60_000L).toInt()
                )
            }
            DailyInsightContext.ScreenTimeSnapshot(
                todayMinutes = todayMin,
                sevenDayAverageMinutes = avgMin,
                deltaPercent = deltaPercent,
                topAppToday = top
            )
        } else null

        val habitsSnap = if (habits.isNotEmpty()) {
            val checkedIds = checkinsToday.map { it.habitId }.toSet()
            val checkinsByHabit = recentCheckins.groupBy { it.habitId }
            val topStreak = habits
                .mapNotNull { h ->
                    val streak = computeHabitStats(checkinsByHabit[h.id].orEmpty()).currentStreak
                    if (streak > 0) h to streak else null
                }
                .maxByOrNull { it.second }
                ?.let { (h, streak) ->
                    DailyInsightContext.HabitsSnapshot.TopStreak(h.name, streak)
                }
            DailyInsightContext.HabitsSnapshot(
                totalActive = habits.size,
                completedToday = habits.count { it.id in checkedIds },
                topStreak = topStreak
            )
        } else null

        val focusSnap = if (focusToday.isNotEmpty()) {
            DailyInsightContext.FocusSessionsSnapshot(
                completedToday = focusToday.count { it.completed },
                totalMinutesToday = focusToday.sumOf { it.actualMinutes },
                abortedToday = focusToday.count { !it.completed }
            )
        } else null

        DailyInsightContext(
            today = today,
            dayName = dayName,
            userName = user?.displayName,
            ageRange = user?.ageRange,
            goals = survey?.primaryGoal
                ?.split(",")
                ?.map { it.trim() }
                ?.filter { it.isNotBlank() }
                .orEmpty(),
            worries = survey?.keepingFactors.orEmpty(),
            moodToday = moodToday,
            moodYesterday = moodYesterday,
            screenTime = screenTimeSnap,
            habits = habitsSnap,
            focusSessions = focusSnap
        )
    }

    private fun buildSetupNudge(today: String): DailyInsight = DailyInsight(
        date = today,
        emoji = "🟡",
        headline = "Welcome — log your first mood to start your daily insights.",
        supportingText = "Tap the mood card below. After two or three days I'll start spotting patterns for you.",
        generatedAt = System.currentTimeMillis(),
        modelUsed = "fallback"
    )

    private companion object {
        const val DAY_MS = 24L * 60 * 60 * 1000L
    }
}
