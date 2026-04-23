package com.example.healthapp.plans.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import com.example.healthapp.plans.domain.model.PlanMetrics
import com.example.healthapp.plans.domain.model.PlanSession
import com.example.healthapp.ui.theme.AppGreen
import com.example.healthapp.ui.theme.Poppins
import java.util.Calendar

@Composable
fun PlanDetailScreen(
    modifier: Modifier = Modifier,
    plan: Plan,
    sessions: List<PlanSession>,
    metrics: PlanMetrics,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCalendar: () -> Unit
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
    ) {
        // ── Header ────────────────────────────────────────────────────
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
                text = "Plan Details",
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                modifier = Modifier.align(Alignment.Center)
            )
            Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = AppGreen)
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFE53935))
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // ── Plan hero card ────────────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(AppGreen.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(plan.icon, fontSize = 30.sp)
                }
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        plan.name,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    if (plan.description.isNotBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            plan.description,
                            fontFamily = Poppins,
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    StatusBadge(plan.status)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Metrics row ───────────────────────────────────────────────
        Text(
            "Metrics",
            fontFamily = Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                modifier = Modifier.weight(1f),
                value = metrics.sessionsCompleted.toString(),
                label = "Sessions",
                color = Color(0xFF1E88E5)
            )
            MetricCard(
                modifier = Modifier.weight(1f),
                value = "${metrics.weeklyProgressPercent}%",
                label = "This Week",
                color = AppGreen
            )
        }
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                modifier = Modifier.weight(1f),
                value = "${metrics.focusHours}h",
                label = "Focus Time",
                color = Color(0xFF8E24AA)
            )
            MetricCard(
                modifier = Modifier.weight(1f),
                value = "${metrics.deviationPercent}%",
                label = "Deviation",
                color = Color(0xFFFB8C00)
            )
        }

        Spacer(Modifier.height(16.dp))

        // ── Schedule info ─────────────────────────────────────────────
        DetailSection(title = "Schedule") {
            val dayNames = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
            val days = plan.scheduleDays.sorted()
                .joinToString("  ") { dayNames.getOrElse(it) { "?" } }
                .ifBlank { "No days selected" }
            DetailRow(label = "Days", value = days)
            DetailRow(label = "Time", value = "${plan.startHour}:00 – ${plan.endHour}:00")
            DetailRow(
                label = "Strictness",
                value = plan.strictness.replaceFirstChar { it.uppercase() }
            )
        }

        if (plan.blockedApps.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            DetailSection(title = "Blocked Apps (${plan.blockedApps.size})") {
                plan.blockedApps.forEach { pkg ->
                    Text(
                        pkg,
                        fontFamily = Poppins,
                        fontSize = 13.sp,
                        color = Color(0xFF555555),
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Calendar button ───────────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable { onCalendar() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = null,
                    tint = AppGreen,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    "View Calendar",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp
                )
                Spacer(Modifier.weight(1f))
                Text("›", fontSize = 20.sp, color = Color.Gray)
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun MetricCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = color)
            Spacer(Modifier.height(2.dp))
            Text(label, fontFamily = Poppins, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun DetailSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                title,
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = Color(0xFF1A1A1A)
            )
            Spacer(Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontFamily = Poppins, fontSize = 13.sp, color = Color.Gray)
        Text(value, fontFamily = Poppins, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun StatusBadge(status: String) {
    val (label, color) = if (status == "active")
        "Active" to AppGreen else "Paused" to Color.Gray
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 3.dp)
    ) {
        Text(label, fontFamily = Poppins, fontSize = 11.sp, color = color, fontWeight = FontWeight.Medium)
    }
}
