package com.example.healthapp.habits.domain.util

import com.example.healthapp.habits.domain.model.HabitCheckin
import com.example.healthapp.habits.domain.model.HabitStats
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/** Pure computation — used by both the use case (after a Firestore read) and the
 *  ViewModel's optimistic update (no Firestore round-trip). */
fun computeHabitStats(checkins: List<HabitCheckin>): HabitStats {
    val dates = checkins.map { it.date }.toSet()
    return HabitStats(
        currentStreak = computeCurrentStreak(dates),
        longestStreak = computeLongestStreak(dates),
        completionRate30d = computeRate30d(dates),
        lastCheckinDate = checkins.maxByOrNull { it.dateMs }?.date,
        totalCheckins = checkins.size
    )
}

private fun computeCurrentStreak(dates: Set<String>): Int {
    if (dates.isEmpty()) return 0
    val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val cal = Calendar.getInstance()
    if (fmt.format(cal.time) !in dates) cal.add(Calendar.DAY_OF_YEAR, -1)
    var streak = 0
    while (true) {
        val d = fmt.format(cal.time)
        if (d !in dates) return streak
        streak++
        cal.add(Calendar.DAY_OF_YEAR, -1)
    }
}

private fun computeLongestStreak(dates: Set<String>): Int {
    if (dates.isEmpty()) return 0
    val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val sorted = dates.mapNotNull { runCatching { fmt.parse(it) }.getOrNull() }.sorted()
    if (sorted.isEmpty()) return 0
    var longest = 1
    var current = 1
    val cal = Calendar.getInstance()
    for (i in 1 until sorted.size) {
        cal.time = sorted[i - 1]
        cal.add(Calendar.DAY_OF_YEAR, 1)
        if (fmt.format(cal.time) == fmt.format(sorted[i])) {
            current++
            if (current > longest) longest = current
        } else {
            current = 1
        }
    }
    return longest
}

private fun computeRate30d(dates: Set<String>): Float {
    val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val cal = Calendar.getInstance()
    var hits = 0
    for (i in 0 until 30) {
        if (fmt.format(cal.time) in dates) hits++
        cal.add(Calendar.DAY_OF_YEAR, -1)
    }
    return hits / 30f
}
