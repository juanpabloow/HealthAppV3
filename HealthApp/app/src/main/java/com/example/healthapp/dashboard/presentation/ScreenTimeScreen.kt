package com.example.healthapp.dashboard.presentation

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthapp.dashboard.domain.model.AppUsageData
import com.example.healthapp.ui.theme.AppBarColors
import com.example.healthapp.ui.theme.AppGreen
import com.example.healthapp.ui.theme.Poppins
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ScreenTimeScreen(
    modifier: Modifier = Modifier,
    state: DashboardUiState,
    onTabSelected: (ScreenTimeTab) -> Unit,
    onDateSelected: (Long) -> Unit,
    onRefreshPermission: () -> Unit,
    onProfileClick: () -> Unit = {},
    onMoodClick: () -> Unit = {},
    onFocusClick: () -> Unit = {}
) {
    val context = LocalContext.current

    if (!state.hasUsagePermission) {
        NoPermissionScreen(
            onGrantPermission = {
                context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            },
            onRefresh = onRefreshPermission
        )
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            // Header con mes/semana
            ScreenTimeHeader(state = state, onTabSelected = onTabSelected, onProfileClick = onProfileClick)
        }

        item {
            MoodTodayCard(onClick = onMoodClick)
        }

        item {
            FocusTodayCard(onClick = onFocusClick)
        }

        item {
            when (state.selectedTab) {
                ScreenTimeTab.DAY -> DayView(
                    state = state,
                    onDateSelected = onDateSelected
                )
                ScreenTimeTab.WEEK -> WeekView(state = state)
                ScreenTimeTab.MONTH -> MonthPlaceholder()
            }
        }
    }
}

@Composable
private fun MoodTodayCard(onClick: () -> Unit) {
    DashboardCtaCard(
        emoji = "🙂",
        title = "How are you feeling today?",
        subtitle = "Tap to log your mood",
        onClick = onClick
    )
}

@Composable
private fun FocusTodayCard(onClick: () -> Unit) {
    DashboardCtaCard(
        emoji = "⏱️",
        title = "Start a focus session",
        subtitle = "Pomodoro timer · history · stats",
        onClick = onClick
    )
}

@Composable
private fun DashboardCtaCard(
    emoji: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(AppGreen.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 24.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Poppins,
                color = Color(0xFF1A1A1A)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color.Gray,
                fontFamily = Poppins
            )
        }
        Text(
            text = "›",
            fontSize = 22.sp,
            color = AppGreen,
            modifier = Modifier.padding(end = 6.dp)
        )
    }
}

@Composable
private fun ScreenTimeHeader(
    state: DashboardUiState,
    onTabSelected: (ScreenTimeTab) -> Unit,
    onProfileClick: () -> Unit
) {
    val cal = Calendar.getInstance().apply { timeInMillis = state.selectedDateMs }
    val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    val monthTitle = monthFormat.format(cal.time)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 20.dp, bottom = 12.dp)
    ) {
        // "Calendar" label + bell + avatar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Calendar",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Normal,
                    fontFamily = Poppins
                )
                Text(
                    text = monthTitle,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Poppins,
                    color = Color.Black
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Bell icon
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF5F5F5)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.DarkGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
                // Avatar — tap to go to Profile tab
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(AppGreen.copy(alpha = 0.2f))
                        .border(2.dp, AppGreen, CircleShape)
                        .clickable { onProfileClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "U",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppGreen
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tabs: Day / Week / Month — full width
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFF0F0F0))
                .padding(4.dp)
        ) {
            ScreenTimeTab.values().forEach { tab ->
                val selected = state.selectedTab == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selected) AppGreen else Color.Transparent)
                        .clickable { onTabSelected(tab) }
                        .padding(vertical = 9.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        tab.name.lowercase().replaceFirstChar { it.uppercase() },
                        fontSize = 13.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        color = if (selected) Color.White else Color.Gray,
                        fontFamily = Poppins
                    )
                }
            }
        }
    }
}

@Composable
private fun DayView(state: DashboardUiState, onDateSelected: (Long) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        // Mini calendario semanal
        MiniWeekCalendar(
            selectedDateMs = state.selectedDateMs,
            onDateSelected = onDateSelected
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (state.isLoading) {
            CircularProgressIndicator(
                color = AppGreen,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(32.dp)
            )
        } else {
            val daily = state.dailyData
            if (daily != null) {
                // Screen time total
                ScreenTimeStat(label = "Screen Time", value = daily.totalTimeFormatted, changeLabel = null)
                Spacer(modifier = Modifier.height(20.dp))

                // Hourly bar chart
                if (daily.hourlyTotals.isNotEmpty()) {
                    HourlyBarChart(hourlyTotals = daily.hourlyTotals)
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // App list
                Text("Apps used today", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF8F8F8))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    daily.apps.forEachIndexed { index, app ->
                        AppUsageRow(app = app, maxTimeMs = daily.apps.firstOrNull()?.totalTimeMs ?: 1L, colorIndex = index)
                    }
                }
            } else {
                EmptyState("No screen time data for this day")
            }
        }
    }
}

@Composable
private fun WeekView(state: DashboardUiState) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        if (state.isLoading) {
            CircularProgressIndicator(
                color = AppGreen,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(32.dp)
            )
            return
        }
        val weekly = state.weeklyData ?: return

        // Stats summary
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            ScreenTimeStat("Weekly screen time", weekly.totalTimeFormatted, null)
            Column(horizontalAlignment = Alignment.End) {
                Text(weekly.dailyAverageFormatted, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("daily avg", fontSize = 11.sp, color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Bar chart
        WeeklyBarChart(dailyTotals = weekly.dailyTotals)

        Spacer(modifier = Modifier.height(24.dp))

        Text("Top apps this week", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF8F8F8))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            weekly.topApps.forEachIndexed { index, app ->
                AppUsageRow(
                    app = app,
                    maxTimeMs = weekly.topApps.firstOrNull()?.totalTimeMs ?: 1L,
                    colorIndex = index
                )
            }
        }
    }
}

@Composable
private fun MiniWeekCalendar(selectedDateMs: Long, onDateSelected: (Long) -> Unit) {
    val selectedCal = Calendar.getInstance().apply { timeInMillis = selectedDateMs }
    val today = Calendar.getInstance()

    // Always show current week starting Sunday
    val weekStart = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }

    val numFormat = SimpleDateFormat("d", Locale.getDefault())
    // 3-letter day names starting Sunday
    val dayLabels = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        for (i in 0..6) {
            val dayCal = Calendar.getInstance().apply {
                timeInMillis = weekStart.timeInMillis
                add(Calendar.DAY_OF_YEAR, i)
            }
            val isSelected = dayCal.get(Calendar.DAY_OF_YEAR) == selectedCal.get(Calendar.DAY_OF_YEAR) &&
                    dayCal.get(Calendar.YEAR) == selectedCal.get(Calendar.YEAR)
            val isToday = dayCal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) &&
                    dayCal.get(Calendar.YEAR) == today.get(Calendar.YEAR)

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) AppGreen else Color.Transparent)
                    .clickable { onDateSelected(dayCal.timeInMillis) }
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = dayLabels[i],
                    fontSize = 10.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium,
                    color = if (isSelected) Color.White else Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = numFormat.format(dayCal.time),
                    fontSize = 15.sp,
                    fontFamily = Poppins,
                    fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) Color.White else if (isToday) AppGreen else Color.Black
                )
            }
        }
    }
}

@Composable
private fun WeeklyBarChart(dailyTotals: Map<String, Long>) {
    val maxMs = (dailyTotals.values.maxOrNull() ?: 1L).coerceAtLeast(1L)
    val days = dailyTotals.entries.sortedBy { it.key }
    val dayNames = listOf("M", "T", "W", "T", "F", "S", "S")
    val maxBarHeight = 100.dp

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        days.forEachIndexed { index, (_, ms) ->
            val fraction = (ms.toFloat() / maxMs).coerceIn(0f, 1f)
            val barHeight = (maxBarHeight.value * fraction.coerceAtLeast(0.02f)).dp
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .width(30.dp)
                        .height(barHeight)
                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                        .background(AppGreen.copy(alpha = if (fraction > 0.5f) 1f else 0.45f))
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(dayNames.getOrElse(index) { "?" }, fontSize = 11.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
private fun HourlyBarChart(hourlyTotals: Map<Int, Long>) {
    val maxMs = (hourlyTotals.values.maxOrNull() ?: 1L).coerceAtLeast(1L)
    // Show hours 6..23
    val hours = (6..23).toList()
    val maxBarHeight = 80.dp

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            hours.forEach { hour ->
                val ms = hourlyTotals[hour] ?: 0L
                val fraction = (ms.toFloat() / maxMs).coerceIn(0f, 1f)
                val barHeight = (maxBarHeight.value * fraction.coerceAtLeast(if (ms > 0) 0.04f else 0f)).dp
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 1.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    if (barHeight.value > 0f) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(barHeight)
                                .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                                .background(AppGreen.copy(alpha = if (fraction > 0.5f) 1f else 0.5f))
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxWidth().height(maxBarHeight))
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        // Hour labels (show every 3 hours)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            hours.forEach { hour ->
                Text(
                    text = if (hour % 3 == 0) "${hour}h" else "",
                    fontSize = 9.sp,
                    color = Color.Gray,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun ScreenTimeStat(label: String, value: String, changeLabel: String?) {
    Column {
        Text(label, fontSize = 13.sp, color = Color.Gray)
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            if (changeLabel != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(changeLabel, fontSize = 12.sp, color = AppGreen)
            }
        }
    }
}

@Composable
private fun AppUsageRow(app: AppUsageData, maxTimeMs: Long, colorIndex: Int = 0) {
    val fraction = if (maxTimeMs > 0) app.totalTimeMs.toFloat() / maxTimeMs else 0f
    val barColor = AppBarColors[colorIndex % AppBarColors.size]

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(barColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        app.appName.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = barColor,
                        fontSize = 15.sp
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    app.appName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                app.totalTimeFormatted,
                fontSize = 13.sp,
                color = Color.Gray,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Poppins
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(50))
                .background(barColor.copy(alpha = 0.12f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(50))
                    .background(barColor)
            )
        }
    }
}

@Composable
private fun NoPermissionScreen(onGrantPermission: () -> Unit, onRefresh: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("📊", fontSize = 64.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Usage Access Required", fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "To show your screen time, HealApp needs access to your app usage data.",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(28.dp))
        Button(
            onClick = onGrantPermission,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppGreen),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Go to Settings", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = onRefresh) {
            Text("I already enabled it", color = AppGreen)
        }
    }
}

@Composable
private fun EmptyState(message: String) {
    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
        Text(message, color = Color.Gray, textAlign = TextAlign.Center)
    }
}

@Composable
private fun MonthPlaceholder() {
    Box(modifier = Modifier.fillMaxWidth().padding(64.dp), contentAlignment = Alignment.Center) {
        Text("Month view coming soon", color = Color.Gray)
    }
}
