package com.example.healthapp.plans.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthapp.plans.domain.model.Plan
import com.example.healthapp.ui.theme.AppGreen
import com.example.healthapp.ui.theme.Poppins
import java.util.Calendar

private enum class DayStatus { NONE, UPCOMING, TODAY, DONE, MISSED }

@Composable
fun PlanCalendarScreen(
    modifier: Modifier = Modifier,
    plan: Plan,
    onBack: () -> Unit
) {
    var displayMonth by remember { mutableStateOf(Calendar.getInstance()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // ── TopBar ────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "${plan.icon}  ${plan.name}",
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(Modifier.height(12.dp))

        // ── Month navigation ──────────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = {
                        displayMonth = (displayMonth.clone() as Calendar).apply {
                            add(Calendar.MONTH, -1)
                        }
                    }) {
                        Text("‹", fontSize = 22.sp, color = Color(0xFF555555))
                    }

                    val monthFmt = java.text.SimpleDateFormat("MMMM yyyy", java.util.Locale.getDefault())
                    Text(
                        monthFmt.format(displayMonth.time),
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )

                    IconButton(onClick = {
                        displayMonth = (displayMonth.clone() as Calendar).apply {
                            add(Calendar.MONTH, 1)
                        }
                    }) {
                        Text("›", fontSize = 22.sp, color = Color(0xFF555555))
                    }
                }

                // Day-of-week labels
                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf("S", "M", "T", "W", "T", "F", "S").forEach { label ->
                        Text(
                            label,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            fontFamily = Poppins,
                            fontSize = 12.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                CalendarGrid(plan = plan, month = displayMonth)

                Spacer(Modifier.height(12.dp))

                // ── Legend ────────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
                ) {
                    LegendItem(color = AppGreen, label = "Done")
                    LegendItem(color = Color(0xFF1E88E5), label = "Today")
                    LegendItem(color = AppGreen.copy(alpha = 0.3f), label = "Upcoming")
                    LegendItem(color = Color(0xFFBDBDBD), label = "Missed")
                }
            }
        }
    }
}

@Composable
private fun CalendarGrid(plan: Plan, month: Calendar) {
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }

    val planCreated = Calendar.getInstance().apply {
        timeInMillis = plan.createdAt
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }

    val firstOfMonth = (month.clone() as Calendar).apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }
    val startDow = firstOfMonth.get(Calendar.DAY_OF_WEEK) - 1 // 0=Sun
    val daysInMonth = firstOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH)

    val totalCells = startDow + daysInMonth
    val rows = (totalCells + 6) / 7

    Column {
        repeat(rows) { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                repeat(7) { col ->
                    val cellIndex = row * 7 + col
                    val dayNum = cellIndex - startDow + 1

                    if (dayNum < 1 || dayNum > daysInMonth) {
                        Box(modifier = Modifier.weight(1f).aspectRatio(1f)) {}
                    } else {
                        val dayCal = (firstOfMonth.clone() as Calendar).apply {
                            set(Calendar.DAY_OF_MONTH, dayNum)
                        }
                        val dow = dayCal.get(Calendar.DAY_OF_WEEK) - 1
                        val isScheduled = dow in plan.scheduleDays
                        val isPlanDay = !dayCal.before(planCreated)

                        val status = when {
                            !isScheduled || !isPlanDay -> DayStatus.NONE
                            dayCal.timeInMillis == today.timeInMillis -> DayStatus.TODAY
                            dayCal.before(today) -> DayStatus.DONE
                            else -> DayStatus.UPCOMING
                        }

                        CalendarDay(
                            modifier = Modifier.weight(1f),
                            dayNum = dayNum,
                            status = status
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDay(modifier: Modifier = Modifier, dayNum: Int, status: DayStatus) {
    val bgColor = when (status) {
        DayStatus.DONE -> AppGreen
        DayStatus.TODAY -> Color(0xFF1E88E5)
        DayStatus.UPCOMING -> AppGreen.copy(alpha = 0.25f)
        DayStatus.MISSED -> Color(0xFFEEEEEE)
        DayStatus.NONE -> Color.Transparent
    }
    val textColor = when (status) {
        DayStatus.DONE, DayStatus.TODAY -> Color.White
        DayStatus.UPCOMING -> AppGreen
        else -> Color(0xFF555555)
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(3.dp)
            .clip(CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        if (status != DayStatus.NONE) {
            Text(
                text = dayNum.toString(),
                fontFamily = Poppins,
                fontSize = 13.sp,
                fontWeight = if (status == DayStatus.TODAY) FontWeight.Bold else FontWeight.Normal,
                color = textColor
            )
        } else {
            Text(
                text = dayNum.toString(),
                fontFamily = Poppins,
                fontSize = 13.sp,
                color = Color(0xFFAAAAAA)
            )
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(4.dp))
        Text(label, fontFamily = Poppins, fontSize = 11.sp, color = Color.Gray)
    }
}
