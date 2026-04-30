package com.example.healthapp.mood.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthapp.mood.domain.model.MoodEntry
import com.example.healthapp.ui.theme.AppGreen
import com.example.healthapp.ui.theme.Poppins
import java.text.SimpleDateFormat
import java.util.*

private val MoodOptions = listOf(
    Triple("great",    "😄", Color(0xFF4CAF50)),
    Triple("good",     "🙂", Color(0xFF8BC34A)),
    Triple("okay",     "😐", Color(0xFFFFC107)),
    Triple("bad",      "😔", Color(0xFFFF9800)),
    Triple("terrible", "😞", Color(0xFFF44336)),
)

private fun moodColor(mood: String): Color =
    MoodOptions.firstOrNull { it.first == mood }?.third ?: Color.Gray

private fun moodEmoji(mood: String): String =
    MoodOptions.firstOrNull { it.first == mood }?.second ?: "❓"

private fun moodLabel(mood: String): String =
    mood.replaceFirstChar { it.uppercase() }

@Composable
fun MoodTrackerScreen(
    modifier: Modifier = Modifier,
    state: MoodUiState,
    onSelectMood: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit,
    onHistoryClick: () -> Unit,
    onClearSaved: () -> Unit,
    onClearError: () -> Unit
) {
    if (state.isSaved) {
        LaunchedEffect(Unit) { onClearSaved() }
    }

    Box(modifier = modifier.fillMaxSize().background(Color(0xFFF5F7FA))) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item { MoodHeader(onHistoryClick) }
            item { Spacer(Modifier.height(4.dp)) }
            item {
                if (state.todayEntry != null && state.selectedMood == state.todayEntry.mood) {
                    TodayLoggedCard(
                        entry = state.todayEntry,
                        onEdit = { onSelectMood("") }
                    )
                } else {
                    MoodPickerCard(
                        selectedMood = state.selectedMood,
                        note = state.note,
                        isLoading = state.isLoading,
                        onSelectMood = onSelectMood,
                        onNoteChange = onNoteChange,
                        onSave = onSave
                    )
                }
            }
            item { Spacer(Modifier.height(20.dp)) }
            item { WeekStripSection(entries = state.entries) }
            item { Spacer(Modifier.height(20.dp)) }
            item {
                Text(
                    text = "Recent Entries",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color(0xFF1A1A1A),
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(Modifier.height(8.dp))
            }
            if (state.entries.isEmpty() && !state.isLoading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No entries yet — log your first mood above!",
                            fontFamily = Poppins,
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }
                }
            }
            items(state.entries.take(30), key = { it.id }) { entry ->
                MoodEntryRow(entry)
            }
        }

        // Error snackbar
        AnimatedVisibility(
            visible = state.error != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFB71C1C))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        state.error ?: "",
                        color = Color.White,
                        fontFamily = Poppins,
                        fontSize = 13.sp,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = onClearError) {
                        Text("OK", color = Color.White, fontFamily = Poppins)
                    }
                }
            }
        }
    }
}

// ── Header ────────────────────────────────────────────────────────────────────

@Composable
private fun MoodHeader(onHistoryClick: () -> Unit) {
    val today = remember {
        SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Mood Tracker",
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Color(0xFF1A1A1A)
            )
            Text(
                text = today,
                fontFamily = Poppins,
                fontSize = 13.sp,
                color = Color.Gray
            )
        }
        IconButton(onClick = onHistoryClick) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = "Full History",
                tint = AppGreen
            )
        }
    }
}

// ── Mood picker card (no entry yet today) ─────────────────────────────────────

@Composable
private fun MoodPickerCard(
    selectedMood: String,
    note: String,
    isLoading: Boolean,
    onSelectMood: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "How are you feeling today?",
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color(0xFF1A1A1A)
            )
            Spacer(Modifier.height(20.dp))

            // Mood buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MoodOptions.forEach { (id, emoji, color) ->
                    MoodButton(
                        emoji = emoji,
                        label = moodLabel(id),
                        color = color,
                        selected = selectedMood == id,
                        onClick = { onSelectMood(id) }
                    )
                }
            }

            AnimatedVisibility(visible = selectedMood.isNotBlank()) {
                Column {
                    Spacer(Modifier.height(20.dp))
                    OutlinedTextField(
                        value = note,
                        onValueChange = onNoteChange,
                        placeholder = {
                            Text(
                                "Add a note (optional)…",
                                fontFamily = Poppins,
                                fontSize = 13.sp
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppGreen,
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        ),
                        maxLines = 3,
                        textStyle = LocalTextStyle.current.copy(
                            fontFamily = Poppins,
                            fontSize = 14.sp
                        )
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = onSave,
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppGreen)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                "Save today's mood",
                                fontFamily = Poppins,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MoodButton(
    emoji: String,
    label: String,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .background(if (selected) color.copy(alpha = 0.12f) else Color.Transparent)
            .border(
                width = if (selected) 2.dp else 0.dp,
                color = if (selected) color else Color.Transparent,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 8.dp, vertical = 10.dp)
    ) {
        Text(emoji, fontSize = 30.sp)
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            fontFamily = Poppins,
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (selected) color else Color.Gray
        )
    }
}

// ── Today logged card ─────────────────────────────────────────────────────────

@Composable
private fun TodayLoggedCard(entry: MoodEntry, onEdit: () -> Unit) {
    val color = moodColor(entry.mood)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f)),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(1.5.dp, SolidColor(color.copy(alpha = 0.3f)))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(moodEmoji(entry.mood), fontSize = 34.sp)
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Today you feel ${moodLabel(entry.mood)}",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = color
                )
                if (entry.note.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = entry.note,
                        fontFamily = Poppins,
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Mood logged ✓",
                    fontFamily = Poppins,
                    fontSize = 11.sp,
                    color = color.copy(alpha = 0.7f)
                )
            }
            IconButton(onClick = onEdit) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit mood",
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// ── 7-day week strip ──────────────────────────────────────────────────────────

@Composable
private fun WeekStripSection(entries: List<MoodEntry>) {
    val entryByDate = entries.associateBy { it.date }
    val cal = Calendar.getInstance()
    val today = startOfDay(cal.timeInMillis)

    val days = (6 downTo 0).map { offset ->
        val c = (cal.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -offset) }
        val date = startOfDay(c.timeInMillis)
        val dayLabel = SimpleDateFormat("EEE", Locale.getDefault()).format(c.time)
        val dayNum = c.get(Calendar.DAY_OF_MONTH).toString()
        Triple(date, dayLabel, dayNum)
    }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "This Week",
            fontFamily = Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = Color(0xFF1A1A1A),
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        Spacer(Modifier.height(12.dp))
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                days.forEach { (date, label, num) ->
                    val entry = entryByDate[date]
                    val isToday = date == today
                    WeekDay(
                        label = label,
                        num = num,
                        entry = entry,
                        isToday = isToday
                    )
                }
            }
        }
    }
}

@Composable
private fun WeekDay(
    label: String,
    num: String,
    entry: MoodEntry?,
    isToday: Boolean
) {
    val color = entry?.let { moodColor(it.mood) } ?: Color(0xFFEEEEEE)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontFamily = Poppins,
            fontSize = 11.sp,
            color = if (isToday) AppGreen else Color.Gray,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
        )
        Spacer(Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(if (entry != null) color.copy(alpha = 0.15f) else Color(0xFFF5F5F5))
                .border(
                    width = if (isToday) 2.dp else 0.dp,
                    color = if (isToday) AppGreen else Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (entry != null) {
                Text(moodEmoji(entry.mood), fontSize = 18.sp)
            } else {
                Text(
                    num,
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    color = if (isToday) AppGreen else Color(0xFFBBBBBB),
                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

// ── Entry row ─────────────────────────────────────────────────────────────────

@Composable
private fun MoodEntryRow(entry: MoodEntry) {
    val color = moodColor(entry.mood)
    val dateFmt = remember { SimpleDateFormat("EEE, MMM d", Locale.getDefault()) }
    val dateStr = remember(entry.date) { dateFmt.format(Date(entry.date)) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Color accent circle
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(moodEmoji(entry.mood), fontSize = 22.sp)
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = moodLabel(entry.mood),
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = color
                )
                if (entry.note.isNotBlank()) {
                    Text(
                        text = entry.note,
                        fontFamily = Poppins,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        maxLines = 1
                    )
                }
            }
            Text(
                text = dateStr,
                fontFamily = Poppins,
                fontSize = 11.sp,
                color = Color.Gray
            )
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

private fun startOfDay(epochMs: Long): Long {
    val cal = Calendar.getInstance()
    cal.timeInMillis = epochMs
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}
