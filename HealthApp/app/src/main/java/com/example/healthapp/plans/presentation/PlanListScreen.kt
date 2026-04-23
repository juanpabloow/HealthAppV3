package com.example.healthapp.plans.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthapp.plans.domain.model.Plan
import com.example.healthapp.ui.theme.AppGreen
import com.example.healthapp.ui.theme.Poppins

@Composable
fun PlanListScreen(
    modifier: Modifier = Modifier,
    state: PlanUiState,
    onPlanClick: (String) -> Unit,
    onCreateClick: () -> Unit,
    onDeletePlan: (String) -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                PlanListHeader(planCount = state.plans.size)
            }

            if (state.isLoading) {
                item {
                    Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AppGreen)
                    }
                }
            } else if (state.plans.isEmpty()) {
                item { EmptyPlansPlaceholder(onCreateClick) }
            } else {
                items(state.plans, key = { it.id }) { plan ->
                    PlanCard(
                        plan = plan,
                        onClick = { onPlanClick(plan.id) },
                        onDelete = { onDeletePlan(plan.id) }
                    )
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick = onCreateClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 24.dp),
            containerColor = AppGreen,
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = "New Plan")
        }
    }
}

@Composable
private fun PlanListHeader(planCount: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Text(
            text = "My Plans",
            fontFamily = Poppins,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            color = Color(0xFF1A1A1A)
        )
        Text(
            text = "$planCount active plan${if (planCount != 1) "s" else ""}",
            fontFamily = Poppins,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun PlanCard(
    plan: Plan,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Plan", fontFamily = Poppins, fontWeight = FontWeight.SemiBold) },
            text = { Text("Delete \"${plan.name}\"? This can't be undone.", fontFamily = Poppins) },
            confirmButton = {
                TextButton(onClick = { showDeleteDialog = false; onDelete() }) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon circle
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(AppGreen.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(plan.icon, fontSize = 24.sp)
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = plan.name,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color(0xFF1A1A1A)
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = scheduleSummary(plan),
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(Modifier.height(6.dp))
                StrictnessBadge(plan.strictness)
            }

            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color(0xFFBDBDBD),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun StrictnessBadge(strictness: String) {
    val (label, color) = when (strictness) {
        "high" -> "High Focus" to Color(0xFFE53935)
        "medium" -> "Medium" to Color(0xFFFB8C00)
        else -> "Relaxed" to Color(0xFF43A047)
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 3.dp)
    ) {
        Text(label, fontFamily = Poppins, fontSize = 11.sp, color = color, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun EmptyPlansPlaceholder(onCreateClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🌱", fontSize = 64.sp)
        Spacer(Modifier.height(16.dp))
        Text(
            "No plans yet",
            fontFamily = Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            color = Color(0xFF1A1A1A)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Create your first plan to start building\nhealthy screen-time habits.",
            fontFamily = Poppins,
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onCreateClick,
            colors = ButtonDefaults.buttonColors(containerColor = AppGreen),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text("Create Plan", fontFamily = Poppins, fontWeight = FontWeight.SemiBold)
        }
    }
}

private fun scheduleSummary(plan: Plan): String {
    val dayNames = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val days = plan.scheduleDays.sorted().joinToString(", ") { dayNames.getOrElse(it) { "?" } }
    val start = "${plan.startHour}:00"
    val end = "${plan.endHour}:00"
    return if (days.isEmpty()) "No days set" else "$days · $start–$end"
}
