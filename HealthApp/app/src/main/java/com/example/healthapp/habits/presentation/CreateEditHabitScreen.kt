package com.example.healthapp.habits.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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

private val PageBg = Color(0xFFF5F7FA)
private val DAY_LABELS = listOf("S", "M", "T", "W", "T", "F", "S")

@Composable
fun CreateEditHabitScreen(
    modifier: Modifier = Modifier,
    state: CreateEditHabitUiState,
    onNameChange: (String) -> Unit,
    onIconChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onToggleDay: (Int) -> Unit,
    onColorChange: (String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    val accent = HabitColors.parse(state.color)
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
                text = if (state.editingHabitId == null) "New habit" else "Edit habit",
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
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            // Name
            SectionLabel("Name")
            OutlinedTextField(
                value = state.name,
                onValueChange = onNameChange,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                placeholder = { Text("e.g. Morning walk", color = Color.Gray, fontSize = 14.sp) },
                colors = textFieldColors()
            )

            Spacer(Modifier.height(20.dp))
            SectionLabel("Icon")
            IconGrid(selected = state.icon, onSelect = onIconChange, accent = accent)

            Spacer(Modifier.height(20.dp))
            SectionLabel("Description (optional)")
            OutlinedTextField(
                value = state.description,
                onValueChange = onDescriptionChange,
                modifier = Modifier.fillMaxWidth().height(96.dp),
                shape = RoundedCornerShape(12.dp),
                placeholder = {
                    Text("Why is this habit important?", color = Color.Gray, fontSize = 14.sp)
                },
                colors = textFieldColors()
            )

            Spacer(Modifier.height(20.dp))
            SectionLabel("Days")
            DaysRow(selectedDays = state.targetDays, onToggle = onToggleDay, accent = accent)

            Spacer(Modifier.height(20.dp))
            SectionLabel("Color")
            ColorPicker(selected = state.color, onSelect = onColorChange)

            if (state.error != null) {
                Spacer(Modifier.height(16.dp))
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
                enabled = state.isValid && !state.isLoading,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = accent,
                    disabledContainerColor = accent.copy(alpha = 0.4f)
                )
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = if (state.editingHabitId == null) "Create habit" else "Save changes",
                        fontWeight = FontWeight.Bold,
                        fontFamily = Poppins,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        fontFamily = Poppins,
        color = Color(0xFF555555)
    )
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun IconGrid(selected: String, onSelect: (String) -> Unit, accent: Color) {
    val rows = HabitIconPalette.chunked(4)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { emoji ->
                    val isSel = emoji == selected
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSel) accent.copy(alpha = 0.14f) else Color.White)
                            .border(
                                width = if (isSel) 2.dp else 1.dp,
                                color = if (isSel) accent else Color(0xFFEEEEEE),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { onSelect(emoji) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(emoji, fontSize = 26.sp)
                    }
                }
                repeat(4 - row.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun DaysRow(selectedDays: List<Int>, onToggle: (Int) -> Unit, accent: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        DAY_LABELS.forEachIndexed { i, label ->
            val isSel = i in selectedDays
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clickable { onToggle(i) },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (isSel) accent else Color.White)
                        .border(
                            width = if (isSel) 0.dp else 1.dp,
                            color = if (isSel) Color.Transparent else Color(0xFFE0E0E0),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 13.sp,
                        fontFamily = Poppins,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSel) Color.White else Color(0xFF555555)
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorPicker(selected: String, onSelect: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        HabitColors.palette.forEach { hex ->
            val color = HabitColors.parse(hex)
            val isSel = hex.equals(selected, ignoreCase = true)
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clickable { onSelect(hex) },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(
                            width = if (isSel) 3.dp else 0.dp,
                            color = if (isSel) Color.White else Color.Transparent,
                            shape = CircleShape
                        )
                )
                if (isSel) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .border(2.dp, color, CircleShape)
                    )
                }
            }
        }
    }
}

@Composable
private fun textFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    disabledContainerColor = Color.White,
    focusedIndicatorColor = AppGreen,
    unfocusedIndicatorColor = Color(0xFFE0E0E0),
    cursorColor = AppGreen
)
