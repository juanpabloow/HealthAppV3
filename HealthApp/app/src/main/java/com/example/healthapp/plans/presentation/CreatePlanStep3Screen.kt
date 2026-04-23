package com.example.healthapp.plans.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.healthapp.ui.theme.AppGreen
import com.example.healthapp.ui.theme.Poppins

private data class DayOption(val index: Int, val short: String)

private val DAYS = listOf(
    DayOption(0, "Sun"),
    DayOption(1, "Mon"),
    DayOption(2, "Tue"),
    DayOption(3, "Wed"),
    DayOption(4, "Thu"),
    DayOption(5, "Fri"),
    DayOption(6, "Sat")
)

private val HOURS = (0..23).toList()

@Composable
fun CreatePlanStep3Screen(
    modifier: Modifier = Modifier,
    state: CreatePlanUiState,
    onToggleDay: (Int) -> Unit,
    onStartHourChange: (Int) -> Unit,
    onEndHourChange: (Int) -> Unit,
    onStrictnessChange: (String) -> Unit,
    onBack: () -> Unit,
    onSave: () -> Unit
) {
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
                text = "New Plan  3/3",
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        LinearProgressIndicator(
            progress = { 1f },
            modifier = Modifier.fillMaxWidth(),
            color = AppGreen,
            trackColor = Color(0xFFE0E0E0)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                "Set your schedule",
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
            Text(
                "Choose which days and hours this plan is active.",
                fontFamily = Poppins,
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(24.dp))

            // ── Days ──────────────────────────────────────────────────
            SectionLabel("Days of the week")
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                DAYS.forEach { day ->
                    val selected = day.index in state.scheduleDays
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selected) AppGreen else Color.White)
                            .border(
                                1.dp,
                                if (selected) AppGreen else Color(0xFFE0E0E0),
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { onToggleDay(day.index) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            day.short,
                            fontFamily = Poppins,
                            fontSize = 11.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            color = if (selected) Color.White else Color(0xFF555555),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Time range ────────────────────────────────────────────
            SectionLabel("Time range")
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HourPicker(
                    modifier = Modifier.weight(1f),
                    label = "Start hour",
                    hour = state.startHour,
                    onHourChange = onStartHourChange
                )
                HourPicker(
                    modifier = Modifier.weight(1f),
                    label = "End hour",
                    hour = state.endHour,
                    onHourChange = onEndHourChange
                )
            }

            Spacer(Modifier.height(24.dp))

            // ── Strictness ────────────────────────────────────────────
            SectionLabel("Strictness level")
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf(
                    Triple("low", "😌", "Relaxed"),
                    Triple("medium", "😤", "Medium"),
                    Triple("high", "🔥", "High")
                ).forEach { (key, emoji, label) ->
                    val selected = state.strictness == key
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onStrictnessChange(key) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selected) AppGreen.copy(alpha = 0.12f) else Color.White
                        ),
                        border = if (selected) BorderStroke(2.dp, AppGreen) else null,
                        elevation = CardDefaults.cardElevation(if (selected) 0.dp else 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(emoji, fontSize = 22.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                label,
                                fontFamily = Poppins,
                                fontSize = 12.sp,
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (selected) AppGreen else Color(0xFF555555),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // ── Save button ───────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Button(
                onClick = onSave,
                enabled = state.scheduleDays.isNotEmpty() && !state.isLoading &&
                    state.startHour < state.endHour,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppGreen)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                } else {
                    Text(
                        if (state.editingPlanId != null) "Save Changes" else "Create Plan",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text,
        fontFamily = Poppins,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        color = Color(0xFF1A1A1A)
    )
}

@Composable
private fun HourPicker(modifier: Modifier = Modifier, label: String, hour: Int, onHourChange: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = "${hour}:00",
            onValueChange = {},
            readOnly = true,
            label = { Text(label, fontFamily = Poppins, fontSize = 12.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            shape = RoundedCornerShape(12.dp),
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = Color(0xFF1A1A1A),
                disabledBorderColor = Color(0xFFBDBDBD),
                disabledLabelColor = Color.Gray
            )
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { expanded = true }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            HOURS.forEach { h ->
                DropdownMenuItem(
                    text = { Text("${h}:00", fontFamily = Poppins) },
                    onClick = { onHourChange(h); expanded = false }
                )
            }
        }
    }
}
