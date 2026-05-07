package com.example.healthapp.emotion.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthapp.emotion.domain.model.Emotion
import com.example.healthapp.ui.theme.AppGreen
import com.example.healthapp.ui.theme.Poppins
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private val PageBg = Color(0xFFF5F7FA)

@Composable
fun EmotionPickerScreen(
    modifier: Modifier = Modifier,
    state: EmotionUiState,
    onSelectEmotion: (Emotion) -> Unit,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit,
    onSaved: () -> Unit,
    onBack: () -> Unit,
    onOpenCalendar: () -> Unit,
    onOpenStats: () -> Unit
) {
    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onSaved()
    }

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
                text = "Today's Mood",
                fontSize = 17.sp,
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            // Keep as lightweight secondary access
            Spacer(modifier = Modifier.size(48.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = todayLongLabel(),
                fontSize = 13.sp,
                color = Color.Gray,
                fontFamily = Poppins
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = if (state.todayEntry == null) "How are you feeling?" else "Update today's mood",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins,
                color = Color(0xFF1A1A1A)
            )
            Spacer(Modifier.height(24.dp))

            EmotionGrid(
                selected = state.selectedEmotion,
                onSelect = onSelectEmotion
            )

            Spacer(Modifier.height(28.dp))

            Text(
                text = "Add a note (optional)",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Poppins,
                color = Color(0xFF555555)
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = state.noteDraft,
                onValueChange = onNoteChange,
                enabled = state.selectedEmotion != null,
                modifier = Modifier.fillMaxWidth().height(110.dp),
                shape = RoundedCornerShape(12.dp),
                placeholder = {
                    Text(
                        "What's on your mind?",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        fontFamily = Poppins
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedIndicatorColor = AppGreen,
                    unfocusedIndicatorColor = Color(0xFFE0E0E0),
                    cursorColor = AppGreen
                )
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${state.noteDraft.length}/280",
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )

            if (state.error != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = state.error,
                    color = Color(0xFFE53935),
                    fontSize = 13.sp,
                    fontFamily = Poppins
                )
            }

            Spacer(Modifier.height(28.dp))

            Button(
                onClick = onSave,
                enabled = state.selectedEmotion != null && !state.isSaving,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppGreen,
                    disabledContainerColor = AppGreen.copy(alpha = 0.4f)
                )
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = if (state.todayEntry == null) "Save" else "Update",
                        fontWeight = FontWeight.Bold,
                        fontFamily = Poppins,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Explore your mood journey ────────────────────────────────
            Text(
                text = "Explore your mood",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Poppins,
                color = Color(0xFF1A1A1A)
            )
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // History card
                MoodShortcutCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.CalendarMonth,
                    iconBg = Color(0xFFE8F5E9),
                    iconTint = AppGreen,
                    title = "History",
                    subtitle = "Day by day",
                    onClick = onOpenCalendar
                )
                // Stats card
                MoodShortcutCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.BarChart,
                    iconBg = Color(0xFFE3F2FD),
                    iconTint = Color(0xFF1565C0),
                    title = "Stats",
                    subtitle = "Trends & patterns",
                    onClick = onOpenStats
                )
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun EmotionGrid(
    selected: Emotion?,
    onSelect: (Emotion) -> Unit
) {
    val all = Emotion.values().toList()
    val rows = all.chunked(4)
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { emotion ->
                    EmotionCell(
                        emotion = emotion,
                        selected = emotion == selected,
                        onClick = { onSelect(emotion) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // pad short rows so cell sizes stay consistent
                repeat(4 - row.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun EmotionCell(
    emotion: Emotion,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tint = emotion.tint()
    Box(
        modifier = modifier
            .height(96.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) tint.copy(alpha = 0.14f) else Color.White)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) tint else Color(0xFFEEEEEE),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emotion.emoji, fontSize = 30.sp)
            Spacer(Modifier.height(4.dp))
            Text(
                text = emotion.displayName,
                fontSize = 11.sp,
                fontFamily = Poppins,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                color = if (selected) tint else Color(0xFF555555)
            )
        }
    }
}

@Composable
private fun MoodShortcutCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .shadow(2.dp, RoundedCornerShape(18.dp), clip = false)
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFFCCCCCC),
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Poppins,
            color = Color(0xFF1A1A1A)
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = subtitle,
            fontSize = 10.sp,
            fontFamily = Poppins,
            color = Color.Gray
        )
    }
}

private fun todayLongLabel(): String {
    val cal = Calendar.getInstance()
    return SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(cal.time)
}
