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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthapp.emotion.domain.model.Emotion
import com.example.healthapp.focus.presentation.components.CountdownRing
import com.example.healthapp.focus.presentation.components.MoodAfterSheet
import com.example.healthapp.focus.presentation.components.PlanPickerSheet
import com.example.healthapp.ui.theme.AppGreen
import com.example.healthapp.ui.theme.Poppins

private val PageBg = Color(0xFFF5F7FA)
private val PRESETS = listOf(15, 25, 45, 60)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusTimerScreen(
    modifier: Modifier = Modifier,
    state: FocusUiState,
    onBack: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenStats: () -> Unit,
    onSelectPreset: (Int) -> Unit,
    onShowPlanPicker: () -> Unit,
    onHidePlanPicker: () -> Unit,
    onSelectPlan: (String?) -> Unit,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit,
    onSelectMoodAfter: (Emotion?) -> Unit,
    onDismissMoodSheet: () -> Unit
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
                text = "Focus",
                fontSize = 17.sp,
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            IconButton(onClick = onOpenHistory) {
                Icon(Icons.Default.History, contentDescription = "History", tint = AppGreen)
            }
            IconButton(onClick = onOpenStats) {
                Icon(Icons.Default.BarChart, contentDescription = "Stats", tint = AppGreen)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(28.dp))

            val plannedMs = (state.plannedMinutes * 60_000L).coerceAtLeast(1L)
            val progress = ((plannedMs - state.remainingMs).toFloat() / plannedMs).coerceIn(0f, 1f)

            CountdownRing(
                progress = progress,
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .fillMaxWidth()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formatTime(state.remainingMs),
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Poppins,
                        color = Color(0xFF1A1A1A)
                    )
                    Text(
                        text = when (state.mode) {
                            TimerMode.IDLE      -> "${state.plannedMinutes} min session"
                            TimerMode.RUNNING   -> "Focusing"
                            TimerMode.PAUSED    -> "Paused"
                            TimerMode.COMPLETED -> "Done!"
                        },
                        fontSize = 13.sp,
                        color = Color.Gray,
                        fontFamily = Poppins
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            PresetRow(
                selectedMinutes = state.plannedMinutes,
                enabled = state.mode == TimerMode.IDLE,
                onSelect = onSelectPreset
            )

            Spacer(Modifier.height(16.dp))

            LinkedPlanRow(
                planName = state.availablePlans.firstOrNull { it.id == state.linkedPlanId }?.name,
                enabled = state.mode == TimerMode.IDLE,
                onClick = onShowPlanPicker
            )

            Spacer(Modifier.height(28.dp))

            ActionButtons(
                mode = state.mode,
                isSaving = state.isSaving,
                onStart = onStart,
                onPause = onPause,
                onResume = onResume,
                onStop = onStop
            )

            if (state.error != null) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = state.error,
                    color = Color(0xFFE53935),
                    fontSize = 13.sp,
                    fontFamily = Poppins,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(40.dp))
        }
    }

    if (state.showPlanPicker) {
        val sheet = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = onHidePlanPicker,
            sheetState = sheet,
            containerColor = Color.White
        ) {
            PlanPickerSheet(
                plans = state.availablePlans,
                selectedPlanId = state.linkedPlanId,
                onSelect = onSelectPlan
            )
        }
    }

    if (state.showMoodSheet) {
        val sheet = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = onDismissMoodSheet,
            sheetState = sheet,
            containerColor = Color.White
        ) {
            MoodAfterSheet(
                actualMinutes = state.lastCompletedActualMinutes,
                onSave = onSelectMoodAfter,
                onSkip = onDismissMoodSheet
            )
        }
    }
}

@Composable
private fun PresetRow(
    selectedMinutes: Int,
    enabled: Boolean,
    onSelect: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PRESETS.forEach { mins ->
            val sel = mins == selectedMinutes
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        when {
                            !enabled && sel -> AppGreen.copy(alpha = 0.4f)
                            sel             -> AppGreen
                            else            -> Color.White
                        }
                    )
                    .clickable(enabled = enabled) { onSelect(mins) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${mins}m",
                    fontSize = 13.sp,
                    fontWeight = if (sel) FontWeight.Bold else FontWeight.Medium,
                    fontFamily = Poppins,
                    color = if (sel) Color.White else Color(0xFF555555)
                )
            }
        }
    }
}

@Composable
private fun LinkedPlanRow(
    planName: String?,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("📋", fontSize = 20.sp)
        Spacer(Modifier.size(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (planName != null) "Linked plan" else "Link to a plan",
                fontSize = 12.sp,
                color = Color.Gray,
                fontFamily = Poppins
            )
            Text(
                text = planName ?: "Optional",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Poppins,
                color = Color(0xFF1A1A1A),
                maxLines = 1
            )
        }
        Text("›", fontSize = 22.sp, color = AppGreen)
    }
}

@Composable
private fun ActionButtons(
    mode: TimerMode,
    isSaving: Boolean,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit
) {
    when (mode) {
        TimerMode.IDLE -> {
            Button(
                onClick = onStart,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppGreen)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(Modifier.size(8.dp))
                Text("Start Focus", fontWeight = FontWeight.Bold, fontFamily = Poppins, fontSize = 15.sp)
            }
        }
        TimerMode.RUNNING, TimerMode.PAUSED -> {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = if (mode == TimerMode.RUNNING) onPause else onResume,
                    modifier = Modifier.weight(1f).height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppGreen)
                ) {
                    Icon(
                        if (mode == TimerMode.RUNNING) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null
                    )
                    Spacer(Modifier.size(6.dp))
                    Text(
                        text = if (mode == TimerMode.RUNNING) "Pause" else "Resume",
                        fontWeight = FontWeight.Bold,
                        fontFamily = Poppins,
                        fontSize = 14.sp
                    )
                }
                OutlinedButton(
                    onClick = onStop,
                    modifier = Modifier.weight(1f).height(54.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Stop, contentDescription = null, tint = Color(0xFFE53935))
                    Spacer(Modifier.size(6.dp))
                    Text(
                        text = "Stop",
                        fontWeight = FontWeight.Bold,
                        fontFamily = Poppins,
                        fontSize = 14.sp,
                        color = Color(0xFFE53935)
                    )
                }
            }
        }
        TimerMode.COMPLETED -> {
            // Mood sheet handles the next step; show a disabled placeholder
            Button(
                onClick = {},
                enabled = false,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = if (isSaving) "Saving…" else "Session complete",
                    fontFamily = Poppins,
                    fontSize = 14.sp
                )
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    val total = (ms / 1000).coerceAtLeast(0)
    val mins = total / 60
    val secs = total % 60
    return "%02d:%02d".format(mins, secs)
}
