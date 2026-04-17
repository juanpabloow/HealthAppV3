package com.example.healthapp.dashboard.domain.model

data class AppUsageData(
    val packageName: String,
    val appName: String,
    val totalTimeMs: Long,
    val iconBase64: String? = null   // para persistir en Firestore si se desea
) {
    val totalTimeFormatted: String get() {
        val hours = totalTimeMs / 3_600_000
        val minutes = (totalTimeMs % 3_600_000) / 60_000
        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            else -> "${minutes}m"
        }
    }
}

data class DailyScreenTime(
    val date: String,           // "yyyy-MM-dd"
    val totalTimeMs: Long,
    val apps: List<AppUsageData>,
    val hourlyTotals: Map<Int, Long> = emptyMap()  // hour (0-23) -> ms
) {
    val totalTimeFormatted: String get() {
        val hours = totalTimeMs / 3_600_000
        val minutes = (totalTimeMs % 3_600_000) / 60_000
        return "${hours}h ${minutes}m"
    }
}

data class WeeklyScreenTime(
    val weekNumber: Int,
    val year: Int,
    val dailyTotals: Map<String, Long>,   // "yyyy-MM-dd" -> ms
    val topApps: List<AppUsageData>
) {
    val totalTimeMs: Long get() = dailyTotals.values.sum()
    val totalTimeFormatted: String get() {
        val hours = totalTimeMs / 3_600_000
        val minutes = (totalTimeMs % 3_600_000) / 60_000
        return "${hours}h ${minutes}m"
    }
    val dailyAverageMs: Long get() = if (dailyTotals.isEmpty()) 0L else totalTimeMs / dailyTotals.size
    val dailyAverageFormatted: String get() {
        val hours = dailyAverageMs / 3_600_000
        val minutes = (dailyAverageMs % 3_600_000) / 60_000
        return "${hours}h ${minutes}m"
    }
}
