package com.example.healthapp.focus.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthapp.ui.theme.AppGreen
import com.example.healthapp.ui.theme.Poppins
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private val PageBg = Color(0xFFF5F7FA)

@Composable
fun FocusStatsScreen(
    modifier: Modifier = Modifier,
    state: FocusUiState,
    onBack: () -> Unit,
    onSelectRange: (FocusRange) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PageBg)
    ) {
        // ── Top bar ─────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 4.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Focus Stats",
                fontSize = 17.sp,
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.size(48.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            RangeTabs(selected = state.statsRange, onSelect = onSelectRange)
            Spacer(Modifier.height(20.dp))

            val stats = state.stats
            if (stats == null || stats.totalSessions == 0) {
                EmptyStats()
            } else {
                TotalsCard(
                    totalMinutes = stats.totalMinutes,
                    totalSessions = stats.totalSessions
                )
                Spacer(Modifier.height(16.dp))
                if (state.statsRange == FocusRange.WEEK) {
                    WeekBarChart(minutesByDay = stats.minutesByDay)
                    Spacer(Modifier.height(16.dp))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = "Completion",
                        value = "${(stats.completionRate * 100).toInt()}%",
                        accent = AppGreen
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = "Streak",
                        value = "${stats.currentStreakDays}d",
                        accent = Color(0xFFFF9800)
                    )
                }
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun RangeTabs(selected: FocusRange, onSelect: (FocusRange) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFF0F0F0))
            .padding(4.dp)
    ) {
        FocusRange.values().forEach { r ->
            val sel = r == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (sel) AppGreen else Color.Transparent)
                    .clickable { onSelect(r) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (r) {
                        FocusRange.WEEK  -> "Week"
                        FocusRange.MONTH -> "Month"
                        FocusRange.ALL   -> "All time"
                    },
                    fontSize = 13.sp,
                    fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal,
                    color = if (sel) Color.White else Color.Gray,
                    fontFamily = Poppins
                )
            }
        }
    }
}

@Composable
private fun TotalsCard(totalMinutes: Int, totalSessions: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
            Text(
                text = "Total focus",
                fontSize = 12.sp,
                color = Color.Gray,
                fontFamily = Poppins
            )
            Row(verticalAlignment = Alignment.Bottom) {
                val hours = totalMinutes / 60
                val mins  = totalMinutes % 60
                val display = when {
                    hours > 0 -> "${hours}h ${mins}m"
                    else      -> "${mins}m"
                }
                Text(
                    text = display,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Poppins,
                    color = Color(0xFF1A1A1A)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "across $totalSessions session${if (totalSessions != 1) "s" else ""}",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontFamily = Poppins,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun WeekBarChart(minutesByDay: Map<String, Int>) {
    // Build last 7 days (oldest to newest, left to right)
    val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val labelFmt = SimpleDateFormat("EEE", Locale.getDefault())
    val cal = Calendar.getInstance()
    val days = mutableListOf<Pair<String, Int>>()
    val dayLabels = mutableListOf<String>()
    val rolling = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -6) }
    for (i in 0..6) {
        val key = fmt.format(rolling.time)
        days.add(key to (minutesByDay[key] ?: 0))
        dayLabels.add(labelFmt.format(rolling.time).take(1))
        rolling.add(Calendar.DAY_OF_YEAR, 1)
    }
    val maxM = (days.maxOfOrNull { it.second } ?: 0).coerceAtLeast(1)
    val maxBarHeight = 120.dp

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Last 7 days",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Poppins,
                color = Color(0xFF1A1A1A)
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                days.forEachIndexed { idx, (_, mins) ->
                    val frac = mins.toFloat() / maxM
                    val barH = (maxBarHeight.value * if (mins == 0) 0.02f else frac.coerceAtLeast(0.06f)).dp
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (mins > 0) "${mins}" else "",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            fontFamily = Poppins
                        )
                        Spacer(Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(22.dp)
                                .height(barH)
                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                .background(if (mins == 0) AppGreen.copy(alpha = 0.18f) else AppGreen)
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = dayLabels[idx],
                            fontSize = 11.sp,
                            color = Color.Gray,
                            fontFamily = Poppins
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(modifier: Modifier = Modifier, label: String, value: String, accent: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.Gray,
                fontFamily = Poppins
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins,
                color = accent
            )
        }
    }
}

@Composable
private fun EmptyStats() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("📈", fontSize = 56.sp)
        Spacer(Modifier.height(12.dp))
        Text(
            text = "No data yet",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = Poppins,
            color = Color(0xFF1A1A1A)
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Complete a focus session to start seeing your stats.",
            fontSize = 13.sp,
            color = Color.Gray,
            fontFamily = Poppins,
            textAlign = TextAlign.Center
        )
    }
}
