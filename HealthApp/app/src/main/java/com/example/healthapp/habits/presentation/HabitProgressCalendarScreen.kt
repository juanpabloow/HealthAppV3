package com.example.healthapp.habits.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthapp.habits.domain.model.Habit
import com.example.healthapp.habits.domain.model.HabitCheckin
import com.example.healthapp.ui.theme.AppGreen
import com.example.healthapp.ui.theme.Poppins
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private val PageBg = Color(0xFFF5F7FA)

// ── Completion color scale ────────────────────────────────────────────────────
private fun completionColor(rate: Float): Color = when {
    rate <= 0f   -> Color(0xFFF0F0F0)                   // 0 % — very light gray
    rate < 0.34f -> Color(0xFFB9F0BB)                   // low — pale green
    rate < 0.67f -> Color(0xFF66BB6A)                   // mid — medium green
    rate < 1f    -> Color(0xFF43A047)                   // high — solid green
    else         -> Color(0xFF2E7D32)                   // 100 % — deep green
}

@Composable
fun HabitProgressCalendarScreen(
    habits: List<Habit>,
    monthCheckins: List<HabitCheckin>,
    onBack: () -> Unit
) {
    val now = remember { Calendar.getInstance() }
    val year       = now.get(Calendar.YEAR)
    val month      = now.get(Calendar.MONTH)            // 0-indexed
    val todayDom   = now.get(Calendar.DAY_OF_MONTH)
    val daysInMonth = now.getActualMaximum(Calendar.DAY_OF_MONTH)

    // Month-start calendar for offset calculation
    val monthStart = remember {
        Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
        }
    }
    // DAY_OF_WEEK: 1=Sun … 7=Sat → column offset for a Sun-first grid
    val startOffset = remember { monthStart.get(Calendar.DAY_OF_WEEK) - 1 }

    val monthPrefix = remember { String.format("%04d-%02d", year, month + 1) }

    // Checkins grouped by date string "yyyy-MM-dd"
    val checkinsByDate: Map<String, List<HabitCheckin>> = remember(monthCheckins) {
        monthCheckins.groupBy { it.date }
    }

    // Stats for header chips
    val (trackedDays, completionPct) = remember(checkinsByDate, habits) {
        var tracked = 0
        var totalScheduled = 0
        var totalChecked   = 0
        for (dom in 1..todayDom) {
            val dayCal = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, dom)
            }
            val dayIndex = dayCal.get(Calendar.DAY_OF_WEEK) - 1 // 0=Sun…6=Sat
            val scheduled = habits.count { dayIndex in it.targetDays }
            if (scheduled == 0) continue
            val dateStr = "$monthPrefix-${dom.toString().padStart(2, '0')}"
            val checked = (checkinsByDate[dateStr]?.size ?: 0).coerceAtMost(scheduled)
            totalScheduled += scheduled
            totalChecked   += checked
            if (checked > 0) tracked++
        }
        val pct = if (totalScheduled == 0) 0
                  else ((totalChecked.toFloat() / totalScheduled) * 100).toInt()
        Pair(tracked, pct)
    }

    val monthLabel = remember {
        SimpleDateFormat("MMMM yyyy", Locale.getDefault())
            .format(now.time)
            .replaceFirstChar { it.uppercase() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg)
    ) {
        // ── Top bar ──────────────────────────────────────────────────────
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
                text = "Habit Progress",
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
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // ── Month title ───────────────────────────────────────────────
            Text(
                text = monthLabel,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins,
                color = Color(0xFF1A1A1A)
            )

            Spacer(Modifier.height(16.dp))

            // ── Stat chips ────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatChip(
                    modifier = Modifier.weight(1f),
                    value = "$trackedDays",
                    label = "days tracked",
                    valueColor = AppGreen
                )
                StatChip(
                    modifier = Modifier.weight(1f),
                    value = "$completionPct%",
                    label = "completion rate",
                    valueColor = Color(0xFF1565C0)
                )
            }

            Spacer(Modifier.height(20.dp))

            // ── Calendar card ─────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Day-of-week header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa").forEach { label ->
                            Text(
                                text = label,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = Poppins,
                                color = Color(0xFF999999)
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // Calendar grid
                    val totalCells = startOffset + daysInMonth
                    val rows = (totalCells + 6) / 7

                    for (row in 0 until rows) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            for (col in 0 until 7) {
                                val cellIndex = row * 7 + col
                                val dom = cellIndex - startOffset + 1

                                if (dom < 1 || dom > daysInMonth) {
                                    // Empty cell — keeps grid aligned
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                    )
                                } else {
                                    val dateStr = "$monthPrefix-${dom.toString().padStart(2, '0')}"
                                    val isFuture = dom > todayDom
                                    val isToday  = dom == todayDom

                                    val dayCal = Calendar.getInstance().apply {
                                        set(Calendar.YEAR, year)
                                        set(Calendar.MONTH, month)
                                        set(Calendar.DAY_OF_MONTH, dom)
                                    }
                                    val dayIndex = dayCal.get(Calendar.DAY_OF_WEEK) - 1
                                    val scheduled = habits.count { dayIndex in it.targetDays }

                                    val bgColor = when {
                                        isFuture   -> Color(0xFFF0F0F0)
                                        scheduled == 0 -> Color(0xFFF7F7F7)
                                        else -> {
                                            val checked = (checkinsByDate[dateStr]?.size ?: 0)
                                                .coerceAtMost(scheduled)
                                            completionColor(checked.toFloat() / scheduled)
                                        }
                                    }

                                    val dayNumColor = when {
                                        isFuture   -> Color(0xFFCCCCCC)
                                        isToday    -> Color.White
                                        scheduled == 0 -> Color(0xFFAAAAAA)
                                        else       -> Color(0xFF1A1A1A)
                                    }

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                            .padding(3.dp)
                                            .clip(CircleShape)
                                            .background(if (isToday) AppGreen else bgColor)
                                            .then(
                                                if (isToday) Modifier.border(
                                                    2.dp, Color(0xFF2E7D32), CircleShape
                                                ) else Modifier
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = dom.toString(),
                                            fontSize = 12.sp,
                                            fontWeight = if (isToday) FontWeight.Bold
                                                         else FontWeight.Medium,
                                            fontFamily = Poppins,
                                            color = dayNumColor
                                        )
                                    }
                                }
                            }
                        }
                        if (row < rows - 1) Spacer(Modifier.height(4.dp))
                    }

                    Spacer(Modifier.height(16.dp))

                    // ── Legend ───────────────────────────────────────────
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LegendDot(color = Color(0xFFF0F0F0), label = "None")
                        LegendDot(color = Color(0xFFB9F0BB), label = "Some")
                        LegendDot(color = Color(0xFF66BB6A), label = "Most")
                        LegendDot(color = Color(0xFF2E7D32), label = "All")
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Per-habit breakdown ───────────────────────────────────────
            if (habits.isNotEmpty()) {
                Text(
                    text = "This month",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Poppins,
                    color = Color(0xFF1A1A1A)
                )
                Spacer(Modifier.height(10.dp))

                habits.forEach { habit ->
                    val habitCheckins = monthCheckins.filter { it.habitId == habit.id }
                    val habitScheduledDays = (1..todayDom).count { dom ->
                        val dc = Calendar.getInstance().apply {
                            set(Calendar.YEAR, year)
                            set(Calendar.MONTH, month)
                            set(Calendar.DAY_OF_MONTH, dom)
                        }
                        (dc.get(Calendar.DAY_OF_WEEK) - 1) in habit.targetDays
                    }
                    val habitCheckedDays = habitCheckins
                        .map { it.date }
                        .distinct()
                        .count()
                    val rate = if (habitScheduledDays == 0) 0f
                               else habitCheckedDays.toFloat() / habitScheduledDays

                    HabitMonthRow(
                        habit = habit,
                        checkedDays = habitCheckedDays,
                        scheduledDays = habitScheduledDays,
                        rate = rate
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

// ── Supporting composables ─────────────────────────────────────────────────────

@Composable
private fun StatChip(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    valueColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = value,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins,
                color = valueColor
            )
            Text(
                text = label,
                fontSize = 11.sp,
                fontFamily = Poppins,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
                .border(0.5.dp, Color(0xFFDDDDDD), CircleShape)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            fontFamily = Poppins,
            color = Color.Gray
        )
    }
}

@Composable
private fun HabitMonthRow(
    habit: Habit,
    checkedDays: Int,
    scheduledDays: Int,
    rate: Float
) {
    val tint = HabitColors.parse(habit.color)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(habit.icon, fontSize = 20.sp)
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Poppins,
                    color = Color(0xFF1A1A1A)
                )
                Spacer(Modifier.height(4.dp))
                // Progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFFF0F0F0))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(rate.coerceIn(0f, 1f))
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(tint)
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = "$checkedDays/$scheduledDays",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins,
                color = tint
            )
        }
    }
}
