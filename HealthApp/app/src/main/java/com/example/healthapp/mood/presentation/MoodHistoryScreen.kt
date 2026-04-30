package com.example.healthapp.mood.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.healthapp.mood.domain.model.MoodEntry
import com.example.healthapp.ui.theme.AppGreen
import com.example.healthapp.ui.theme.Poppins
import java.text.SimpleDateFormat
import java.util.*

private val MoodOptionsList = listOf(
    Triple("great",    "😄", Color(0xFF4CAF50)),
    Triple("good",     "🙂", Color(0xFF8BC34A)),
    Triple("okay",     "😐", Color(0xFFFFC107)),
    Triple("bad",      "😔", Color(0xFFFF9800)),
    Triple("terrible", "😞", Color(0xFFF44336)),
)

private fun histMoodColor(mood: String): Color =
    MoodOptionsList.firstOrNull { it.first == mood }?.third ?: Color.Gray

private fun histMoodEmoji(mood: String): String =
    MoodOptionsList.firstOrNull { it.first == mood }?.second ?: "❓"

private fun histMoodLabel(mood: String): String =
    mood.replaceFirstChar { it.uppercase() }

@Composable
fun MoodHistoryScreen(
    modifier: Modifier = Modifier,
    state: MoodUiState,
    onBack: () -> Unit
) {
    Column(modifier = modifier.fillMaxSize().background(Color(0xFFF5F7FA))) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF1A1A1A))
            }
            Text(
                text = "Mood History",
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF1A1A1A)
            )
        }

        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AppGreen)
            }
            return@Column
        }

        if (state.entries.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🌸", fontSize = 56.sp)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "No mood entries yet",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = Color(0xFF1A1A1A)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Start logging your daily mood\nto see your history here.",
                        fontFamily = Poppins,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
            return@Column
        }

        LazyColumn(contentPadding = PaddingValues(vertical = 16.dp, horizontal = 16.dp)) {
            item { MoodSummaryCard(state.entries) }
            item { Spacer(Modifier.height(20.dp)) }
            item { MoodCalendarSection(state.entries) }
            item { Spacer(Modifier.height(20.dp)) }
            item {
                Text(
                    "All Entries",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color(0xFF1A1A1A)
                )
                Spacer(Modifier.height(8.dp))
            }
            items(state.entries, key = { it.id }) { entry ->
                HistoryEntryRow(entry)
            }
        }
    }
}

// ── Summary card ──────────────────────────────────────────────────────────────

@Composable
private fun MoodSummaryCard(entries: List<MoodEntry>) {
    val moodCounts = entries.groupingBy { it.mood }.eachCount()
    val dominantMood = moodCounts.maxByOrNull { it.value }?.key ?: ""
    val totalEntries = entries.size

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Summary",
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = Color(0xFF1A1A1A)
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryStatItem(
                    value = totalEntries.toString(),
                    label = "Days logged",
                    color = AppGreen
                )
                if (dominantMood.isNotBlank()) {
                    SummaryStatItem(
                        value = histMoodEmoji(dominantMood),
                        label = "Most frequent",
                        color = histMoodColor(dominantMood),
                        isEmoji = true
                    )
                }
                val positiveCount = entries.count { it.mood == "great" || it.mood == "good" }
                val positivePercent = if (totalEntries > 0)
                    (positiveCount.toFloat() / totalEntries * 100).toInt() else 0
                SummaryStatItem(
                    value = "$positivePercent%",
                    label = "Good days",
                    color = Color(0xFF8BC34A)
                )
            }
            Spacer(Modifier.height(20.dp))
            // Mood frequency bars
            Text(
                "Mood Breakdown",
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                color = Color.Gray
            )
            Spacer(Modifier.height(12.dp))
            MoodOptionsList.forEach { (id, emoji, color) ->
                val count = moodCounts[id] ?: 0
                val fraction = if (totalEntries > 0) count.toFloat() / totalEntries else 0f
                MoodBreakdownBar(
                    emoji = emoji,
                    label = histMoodLabel(id),
                    count = count,
                    fraction = fraction,
                    color = color
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun SummaryStatItem(
    value: String,
    label: String,
    color: Color,
    isEmoji: Boolean = false
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (isEmoji) {
            Text(value, fontSize = 30.sp)
        } else {
            Text(
                value,
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = color
            )
        }
        Text(
            label,
            fontFamily = Poppins,
            fontSize = 11.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun MoodBreakdownBar(
    emoji: String,
    label: String,
    count: Int,
    fraction: Float,
    color: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(emoji, fontSize = 16.sp, modifier = Modifier.width(28.dp))
        Text(
            label,
            fontFamily = Poppins,
            fontSize = 12.sp,
            color = Color(0xFF555555),
            modifier = Modifier.width(72.dp)
        )
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(50)),
            color = color,
            trackColor = color.copy(alpha = 0.12f)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            "$count",
            fontFamily = Poppins,
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.width(20.dp),
            textAlign = TextAlign.End
        )
    }
}

// ── Calendar section ──────────────────────────────────────────────────────────

@Composable
private fun MoodCalendarSection(entries: List<MoodEntry>) {
    val entryByDate = entries.associateBy { it.date }
    var displayMonth by remember { mutableStateOf(Calendar.getInstance()) }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Month navigation
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
                val monthFmt = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()) }
                Text(
                    monthFmt.format(displayMonth.time),
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = Color(0xFF1A1A1A)
                )
                IconButton(onClick = {
                    displayMonth = (displayMonth.clone() as Calendar).apply {
                        add(Calendar.MONTH, 1)
                    }
                }) {
                    Text("›", fontSize = 22.sp, color = Color(0xFF555555))
                }
            }
            Spacer(Modifier.height(4.dp))
            // Day-of-week labels
            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("S", "M", "T", "W", "T", "F", "S").forEach { d ->
                    Text(
                        d,
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
            MoodCalendarGrid(displayMonth = displayMonth, entryByDate = entryByDate)
        }
    }
}

@Composable
private fun MoodCalendarGrid(
    displayMonth: Calendar,
    entryByDate: Map<Long, MoodEntry>
) {
    val todayCal = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }
    val firstOfMonth = (displayMonth.clone() as Calendar).apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }
    val startDow = firstOfMonth.get(Calendar.DAY_OF_WEEK) - 1
    val daysInMonth = firstOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
    val rows = (startDow + daysInMonth + 6) / 7

    Column {
        repeat(rows) { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                repeat(7) { col ->
                    val dayNum = row * 7 + col - startDow + 1
                    if (dayNum < 1 || dayNum > daysInMonth) {
                        Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                    } else {
                        val dayCal = (firstOfMonth.clone() as Calendar).apply {
                            set(Calendar.DAY_OF_MONTH, dayNum)
                        }
                        val date = dayCal.timeInMillis
                        val entry = entryByDate[date]
                        val isToday = dayCal.timeInMillis == todayCal.timeInMillis
                        val moodColor = entry?.let { histMoodColor(it.mood) }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        moodColor != null -> moodColor.copy(alpha = 0.18f)
                                        isToday -> AppGreen.copy(alpha = 0.1f)
                                        else -> Color.Transparent
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (entry != null) {
                                Text(histMoodEmoji(entry.mood), fontSize = 14.sp)
                            } else {
                                Text(
                                    dayNum.toString(),
                                    fontFamily = Poppins,
                                    fontSize = 12.sp,
                                    color = if (isToday) AppGreen else Color(0xFFAAAAAA),
                                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── History entry row ─────────────────────────────────────────────────────────

@Composable
private fun HistoryEntryRow(entry: MoodEntry) {
    val color = histMoodColor(entry.mood)
    val dateFmt = remember { SimpleDateFormat("EEE, MMM d", Locale.getDefault()) }
    val dateStr = remember(entry.date) { dateFmt.format(Date(entry.date)) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(histMoodEmoji(entry.mood), fontSize = 20.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    histMoodLabel(entry.mood),
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = color
                )
                if (entry.note.isNotBlank()) {
                    Text(
                        entry.note,
                        fontFamily = Poppins,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        maxLines = 1
                    )
                }
            }
            Text(
                dateStr,
                fontFamily = Poppins,
                fontSize = 11.sp,
                color = Color.Gray
            )
        }
    }
}
