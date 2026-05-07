package com.example.healthapp.ai.data.repository

import com.example.healthapp.ai.domain.model.DailyInsightContext
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import javax.inject.Inject

/**
 * Pure function: given a [DailyInsightContext], produces the compact JSON
 * string that becomes the user message body for the OpenAI call. Absent
 * fields are omitted entirely (not serialised as null) so the model never
 * sees "0" or "null" and treats it as a real number/value.
 */
class InsightContextBuilder @Inject constructor() {

    fun build(context: DailyInsightContext): String {
        val payload = buildJsonObject {
            put("today", context.today)
            put("dayName", context.dayName)

            putJsonObject("user") {
                if (!context.userName.isNullOrBlank()) put("name", context.userName)
                if (!context.ageRange.isNullOrBlank()) put("ageRange", context.ageRange)
                if (context.goals.isNotEmpty()) {
                    putJsonArray("goals") { context.goals.forEach { add(it) } }
                }
                if (context.worries.isNotEmpty()) {
                    putJsonArray("worries") { context.worries.forEach { add(it) } }
                }
            }

            context.moodToday?.let { mood ->
                putJsonObject("moodToday") {
                    put("emotion", mood.emotion)
                    if (!mood.note.isNullOrBlank()) put("note", mood.note)
                }
            }
            context.moodYesterday?.let { mood ->
                putJsonObject("moodYesterday") {
                    put("emotion", mood.emotion)
                    if (!mood.note.isNullOrBlank()) put("note", mood.note)
                }
            }

            context.screenTime?.let { st ->
                putJsonObject("screenTime") {
                    put("todayMinutes", st.todayMinutes)
                    put("sevenDayAverageMinutes", st.sevenDayAverageMinutes)
                    put("deltaPercent", st.deltaPercent)
                    st.topAppToday?.let { app ->
                        putJsonObject("topAppToday") {
                            put("name", app.name)
                            put("minutes", app.minutes)
                        }
                    }
                }
            }

            context.habits?.let { h ->
                putJsonObject("habits") {
                    put("totalActive", h.totalActive)
                    put("completedToday", h.completedToday)
                    h.topStreak?.let { ts ->
                        putJsonObject("topStreak") {
                            put("habitName", ts.habitName)
                            put("currentStreakDays", ts.currentStreakDays)
                        }
                    }
                }
            }

            context.focusSessions?.let { fs ->
                putJsonObject("focusSessions") {
                    putJsonObject("today") {
                        put("completed", fs.completedToday)
                        put("totalMinutes", fs.totalMinutesToday)
                        put("abortedCount", fs.abortedToday)
                    }
                }
            }
        }
        return payload.toString()
    }
}
