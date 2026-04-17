package com.example.healthapp.dashboard.data.repository

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Process
import com.example.healthapp.dashboard.domain.model.AppUsageData
import com.example.healthapp.dashboard.domain.model.DailyScreenTime
import com.example.healthapp.dashboard.domain.model.WeeklyScreenTime
import com.example.healthapp.dashboard.domain.repository.ScreenTimeRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ScreenTimeRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firestore: FirebaseFirestore
) : ScreenTimeRepository {

    private val usageStatsManager by lazy {
        context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    }
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun hasUsagePermission(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    override suspend fun getDailyScreenTime(dateMillis: Long): Result<DailyScreenTime> =
        withContext(Dispatchers.IO) {
            try {
                val cal = Calendar.getInstance().apply { timeInMillis = dateMillis }
                val start = cal.apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                val end = start + 24 * 60 * 60 * 1000L

                val stats = usageStatsManager.queryUsageStats(
                    UsageStatsManager.INTERVAL_DAILY, start, end
                )

                val apps = stats
                    .filter { it.totalTimeInForeground > 0 && it.packageName != context.packageName }
                    .sortedByDescending { it.totalTimeInForeground }
                    .take(20)
                    .map { it.toAppUsageData() }

                val totalMs = apps.sumOf { it.totalTimeMs }
                val dateStr = dateFormat.format(Date(dateMillis))

                val hourlyTotals = getHourlyTotals(start, end)
                Result.success(DailyScreenTime(date = dateStr, totalTimeMs = totalMs, apps = apps, hourlyTotals = hourlyTotals))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun getWeeklyScreenTime(weekOffset: Int): Result<WeeklyScreenTime> =
        withContext(Dispatchers.IO) {
            try {
                val cal = Calendar.getInstance().apply {
                    add(Calendar.WEEK_OF_YEAR, weekOffset)
                    set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val weekStart = cal.timeInMillis
                val weekEnd = weekStart + 7 * 24 * 60 * 60 * 1000L
                val weekNumber = cal.get(Calendar.WEEK_OF_YEAR)
                val year = cal.get(Calendar.YEAR)

                val stats = usageStatsManager.queryUsageStats(
                    UsageStatsManager.INTERVAL_WEEKLY, weekStart, weekEnd
                )

                // Agregar por día
                val dailyTotals = mutableMapOf<String, Long>()
                val appTotals = mutableMapOf<String, Long>()

                for (i in 0..6) {
                    val dayStart = weekStart + i * 24 * 60 * 60 * 1000L
                    val dayStats = usageStatsManager.queryUsageStats(
                        UsageStatsManager.INTERVAL_DAILY,
                        dayStart,
                        dayStart + 24 * 60 * 60 * 1000L
                    )
                    val dayTotal = dayStats
                        .filter { it.packageName != context.packageName }
                        .sumOf { it.totalTimeInForeground }
                    dailyTotals[dateFormat.format(Date(dayStart))] = dayTotal

                    dayStats.forEach { stat ->
                        if (stat.packageName != context.packageName && stat.totalTimeInForeground > 0) {
                            appTotals[stat.packageName] =
                                (appTotals[stat.packageName] ?: 0L) + stat.totalTimeInForeground
                        }
                    }
                }

                val topApps = appTotals.entries
                    .sortedByDescending { it.value }
                    .take(10)
                    .map { (pkg, ms) ->
                        AppUsageData(
                            packageName = pkg,
                            appName = getAppName(pkg),
                            totalTimeMs = ms
                        )
                    }

                Result.success(
                    WeeklyScreenTime(
                        weekNumber = weekNumber,
                        year = year,
                        dailyTotals = dailyTotals,
                        topApps = topApps
                    )
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun cacheDailyData(userId: String, data: DailyScreenTime): Result<Unit> {
        return try {
            val appsMap = data.apps.map {
                mapOf("packageName" to it.packageName, "appName" to it.appName, "totalTimeMs" to it.totalTimeMs)
            }
            // Convert Map<Int,Long> to Map<String,Long> for Firestore (keys must be strings)
            val hourlyMap = data.hourlyTotals.mapKeys { it.key.toString() }

            firestore.collection("screen_time")
                .document(userId)
                .collection("days")
                .document(data.date)
                .set(mapOf(
                    "date" to data.date,
                    "totalTimeMs" to data.totalTimeMs,
                    "apps" to appsMap,
                    "hourlyTotals" to hourlyMap
                ))
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getHourlyTotals(start: Long, end: Long): Map<Int, Long> {
        val hourlyTotals = mutableMapOf<Int, Long>()
        return try {
            val events = usageStatsManager.queryEvents(start, end)
            val event = UsageEvents.Event()
            var lastResumedTime = 0L
            var lastPackage = ""

            while (events.hasNextEvent()) {
                events.getNextEvent(event)
                when (event.eventType) {
                    UsageEvents.Event.ACTIVITY_RESUMED -> {
                        if (event.packageName != context.packageName) {
                            lastResumedTime = event.timeStamp
                            lastPackage = event.packageName
                        }
                    }
                    UsageEvents.Event.ACTIVITY_PAUSED -> {
                        if (lastResumedTime > 0 && event.packageName == lastPackage) {
                            val duration = event.timeStamp - lastResumedTime
                            val cal = Calendar.getInstance().apply { timeInMillis = lastResumedTime }
                            val hour = cal.get(Calendar.HOUR_OF_DAY)
                            hourlyTotals[hour] = (hourlyTotals[hour] ?: 0L) + duration
                            lastResumedTime = 0L
                        }
                    }
                }
            }
            hourlyTotals
        } catch (e: Exception) {
            hourlyTotals
        }
    }

    private fun UsageStats.toAppUsageData(): AppUsageData = AppUsageData(
        packageName = packageName,
        appName = getAppName(packageName),
        totalTimeMs = totalTimeInForeground
    )

    private fun getAppName(packageName: String): String = try {
        val pm = context.packageManager
        val info = pm.getApplicationInfo(packageName, 0)
        pm.getApplicationLabel(info).toString()
    } catch (e: PackageManager.NameNotFoundException) {
        packageName.substringAfterLast(".")
    }
}
