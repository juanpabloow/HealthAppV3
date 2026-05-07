package com.example.healthapp.habits.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthapp.habits.domain.model.Habit
import com.example.healthapp.ui.theme.AppGreen
import com.example.healthapp.ui.theme.Poppins

private val PageBg = Color(0xFFF5F7FA)

@Composable
fun HabitListScreen(
    modifier: Modifier = Modifier,
    state: HabitsUiState,
    onBack: () -> Unit,
    onHabitClick: (String) -> Unit,
    onToggleToday: (String) -> Unit,
    onCreate: () -> Unit
) {
    Box(modifier = modifier.fillMaxSize().background(PageBg)) {
        Column(modifier = Modifier.fillMaxSize()) {
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
                    text = "My Habits",
                    fontSize = 17.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.size(48.dp))
            }

            when {
                state.isLoading && state.habits.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AppGreen)
                    }
                }
                state.habits.isEmpty() -> {
                    EmptyHabits(onCreate = onCreate)
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(items = state.habits, key = { it.id }) { habit ->
                            HabitRow(
                                habit = habit,
                                checkedToday = habit.id in state.todayCheckedHabitIds,
                                streak = state.statsByHabit[habit.id]?.currentStreak ?: 0,
                                onClick = { onHabitClick(habit.id) },
                                onToggle = { onToggleToday(habit.id) }
                            )
                        }
                        item { Spacer(Modifier.height(80.dp)) }
                    }
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick = onCreate,
            containerColor = AppGreen,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 24.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "New habit", tint = Color.White)
        }
    }
}

@Composable
private fun HabitRow(
    habit: Habit,
    checkedToday: Boolean,
    streak: Int,
    onClick: () -> Unit,
    onToggle: () -> Unit
) {
    val tint = HabitColors.parse(habit.color)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
        ) {
            Text(habit.icon, fontSize = 22.sp)
        }
        Spacer(Modifier.size(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = habit.name.ifBlank { "Untitled" },
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Poppins,
                color = Color(0xFF1A1A1A),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (habit.description.isNotBlank()) {
                Text(
                    text = habit.description,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontFamily = Poppins,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (streak > 0) {
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(tint.copy(alpha = 0.14f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "🔥 ${streak}d",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = Poppins,
                        color = tint
                    )
                }
            }
        }
        Spacer(Modifier.size(8.dp))
        // Inner clickable: consumes pointer events so the row click doesn't fire too.
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (checkedToday) tint else Color.White)
                .border(
                    width = 2.dp,
                    color = if (checkedToday) tint else tint.copy(alpha = 0.5f),
                    shape = CircleShape
                )
                .clickable { onToggle() },
            contentAlignment = Alignment.Center
        ) {
            if (checkedToday) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Checked today",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyHabits(onCreate: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🌱", fontSize = 56.sp)
        Spacer(Modifier.height(12.dp))
        Text(
            text = "No habits yet",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = Poppins,
            color = Color(0xFF1A1A1A)
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Tap + to create your first habit and start building a streak.",
            fontSize = 13.sp,
            color = Color.Gray,
            fontFamily = Poppins,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(AppGreen)
                .clickable { onCreate() }
                .padding(horizontal = 22.dp, vertical = 12.dp)
        ) {
            Text(
                "Create habit",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins,
                fontSize = 14.sp
            )
        }
    }
}
