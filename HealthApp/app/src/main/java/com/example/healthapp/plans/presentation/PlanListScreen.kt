package com.example.healthapp.plans.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.healthapp.auth.domain.model.User
import com.example.healthapp.plans.domain.model.Plan
import com.example.healthapp.ui.theme.AppGreen
import com.example.healthapp.ui.theme.Poppins
import java.util.Calendar

// Colors cycling per plan card
private val PlanCardColors = listOf(
    Color(0xFF43A047),  // green
    Color(0xFF5C6BC0),  // indigo
    Color(0xFFFF9800),  // orange
    Color(0xFFE91E63),  // pink
    Color(0xFF00ACC1),  // cyan
    Color(0xFF8E24AA),  // purple
)

@Composable
fun PlanListScreen(
    modifier: Modifier = Modifier,
    state: PlanUiState,
    user: User? = null,
    onPlanClick: (String) -> Unit,
    onCreateClick: () -> Unit,
    onDeletePlan: (String) -> Unit,
    onProfileClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) } // 0=Active Plans, 1=Calendar

    Box(modifier = modifier.fillMaxSize().background(Color(0xFFF5F7FA))) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // ── Header ────────────────────────────────────────────────────
            item {
                PlanListHeader(
                    user = user,
                    plans = state.plans,
                    onProfileClick = onProfileClick
                )
            }

            // ── Tabs ──────────────────────────────────────────────────────
            item {
                PlanTabRow(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }

            // ── Content ───────────────────────────────────────────────────
            if (state.isLoading) {
                item {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AppGreen)
                    }
                }
            } else if (state.plans.isEmpty()) {
                item { EmptyPlansPlaceholder(onCreateClick) }
            } else {
                itemsIndexed(state.plans, key = { _, p -> p.id }) { index, plan ->
                    PlanCard(
                        plan = plan,
                        cardColor = PlanCardColors[index % PlanCardColors.size],
                        onClick = { onPlanClick(plan.id) },
                        onDelete = { onDeletePlan(plan.id) }
                    )
                }
            }
        }

        // ── FAB ───────────────────────────────────────────────────────────
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

// ── Header with hero section + stats ──────────────────────────────────────────

@Composable
private fun PlanListHeader(
    user: User?,
    plans: List<Plan>,
    onProfileClick: () -> Unit
) {
    val displayName = user?.displayName ?: user?.email?.substringBefore("@") ?: "User"
    val initials = displayName.split(" ")
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .take(2).joinToString("").ifBlank { "U" }

    val activePlans = plans.count { it.status == "active" }
    val dayStreak = computeDayStreak(plans)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 20.dp)
    ) {
        // Top row: app name + icons
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Heal",
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF1A1A1A)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Bell
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF5F5F5)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.DarkGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
                // Avatar
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(AppGreen.copy(alpha = 0.15f))
                        .border(2.dp, AppGreen, CircleShape)
                        .clickable { onProfileClick() },
                    contentAlignment = Alignment.Center
                ) {
                    if (user?.photoUrl != null) {
                        AsyncImage(
                            model = user.photoUrl,
                            contentDescription = "Profile",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(CircleShape)
                        )
                    } else {
                        Text(
                            text = initials,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppGreen,
                            fontFamily = Poppins
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hero title
        Text(
            text = buildAnnotatedString {
                append("It's time to ")
                withStyle(SpanStyle(color = AppGreen, fontWeight = FontWeight.Bold)) {
                    append("heal")
                }
                append(" 🍃")
            },
            fontFamily = Poppins,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = Color(0xFF1A1A1A)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Based on the survey we suggest some plans",
            fontFamily = Poppins,
            fontSize = 13.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Stats row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                value = "$activePlans",
                label = "Active plans",
                valueColor = AppGreen
            )
            StatCard(
                modifier = Modifier.weight(1f),
                value = "$dayStreak",
                label = "Day streak",
                valueColor = Color(0xFFFF9800)
            )
            StatCard(
                modifier = Modifier.weight(1f),
                value = "—",
                label = "Screen time",
                valueColor = Color(0xFF1E88E5)
            )
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    valueColor: Color
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(valueColor.copy(alpha = 0.08f))
            .padding(horizontal = 10.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontFamily = Poppins,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = valueColor
        )
        Text(
            text = label,
            fontFamily = Poppins,
            fontSize = 11.sp,
            color = Color.Gray
        )
    }
}

// ── Tab row ───────────────────────────────────────────────────────────────────

@Composable
private fun PlanTabRow(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    val tabs = listOf("Active Plans", "Calendar")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFEEEEEE))
            .padding(4.dp)
    ) {
        tabs.forEachIndexed { index, label ->
            val selected = index == selectedTab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selected) AppGreen else Color.Transparent)
                    .clickable { onTabSelected(index) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    fontFamily = Poppins,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 13.sp,
                    color = if (selected) Color.White else Color.Gray
                )
            }
        }
    }
}

// ── Plan card ─────────────────────────────────────────────────────────────────

@Composable
private fun PlanCard(
    plan: Plan,
    cardColor: Color,
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

    // Weekly progress
    val (doneThisWeek, totalThisWeek) = weeklyProgress(plan)
    val progressFraction = if (totalThisWeek > 0) doneThisWeek.toFloat() / totalThisWeek else 0f

    // Schedule text
    val dayNames = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val daysText = plan.scheduleDays.sorted().joinToString(", ") { dayNames.getOrElse(it) { "?" } }
    val timeText = "${plan.startHour}:00 – ${plan.endHour}:00"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Left color accent bar
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(cardColor, shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp, vertical = 14.dp)
            ) {
                // Top row: icon + name + status + delete
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Icon circle
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(cardColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(plan.icon, fontSize = 22.sp)
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = plan.name,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1A1A1A),
                        modifier = Modifier.weight(1f)
                    )
                    // Status badge
                    val isActive = plan.status == "active"
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(if (isActive) AppGreen else Color.Gray)
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (isActive) "Active" else "Paused",
                            fontFamily = Poppins,
                            fontSize = 11.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFBDBDBD),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Schedule row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📅 $daysText",
                        fontFamily = Poppins,
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(cardColor.copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "🕒 $timeText",
                            fontFamily = Poppins,
                            fontSize = 11.sp,
                            color = cardColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Description
                if (plan.description.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = plan.description,
                        fontFamily = Poppins,
                        fontSize = 12.sp,
                        color = Color(0xFF888888),
                        lineHeight = 17.sp
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Progress bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Weekly Progress",
                        fontFamily = Poppins,
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Text(
                        "$doneThisWeek/$totalThisWeek sessions",
                        fontFamily = Poppins,
                        fontSize = 11.sp,
                        color = cardColor,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(50)),
                    color = cardColor,
                    trackColor = cardColor.copy(alpha = 0.15f)
                )
            }
        }
    }
}

// ── Empty state ───────────────────────────────────────────────────────────────

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

// ── Helpers ───────────────────────────────────────────────────────────────────

private fun weeklyProgress(plan: Plan): Pair<Int, Int> {
    val cal = Calendar.getInstance()
    val todayDow = cal.get(Calendar.DAY_OF_WEEK) - 1 // 0=Sun
    val scheduledDaysThisWeek = plan.scheduleDays.size
    val doneThisWeek = plan.scheduleDays.count { it <= todayDow }
    return Pair(doneThisWeek, scheduledDaysThisWeek)
}

private fun computeDayStreak(plans: List<Plan>): Int {
    if (plans.isEmpty()) return 0
    val today = Calendar.getInstance()
    var streak = 0
    val cal = Calendar.getInstance()
    for (i in 0..365) {
        cal.timeInMillis = today.timeInMillis
        cal.add(Calendar.DAY_OF_YEAR, -i)
        val dow = cal.get(Calendar.DAY_OF_WEEK) - 1
        val hasSchedule = plans.any { dow in it.scheduleDays && it.status == "active" }
        if (hasSchedule) streak++ else break
    }
    return streak
}
