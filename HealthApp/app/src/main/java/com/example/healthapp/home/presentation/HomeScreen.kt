package com.example.healthapp.home.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.healthapp.habits.domain.model.Habit
import com.example.healthapp.habits.presentation.HabitListViewModel
import com.example.healthapp.ui.theme.AppGreen
import com.example.healthapp.ui.theme.Poppins
import java.util.*

// ── Palette ───────────────────────────────────────────────────────────────────
private val PageBg     = Color(0xFFF2F4F7)
private val CardBg     = Color.White
private val BrandGreen = Color(0xFF43B34C)
private val DeepGreen  = Color(0xFF2A7D31)
private val SubtleGray = Color(0xFF8A8A8A)

// ── Mood options (emoji label only, no icon replacement here) ─────────────────
private data class MoodOption(val emoji: String, val label: String)
private val moodOptions = listOf(
    MoodOption("😞", "Bad"),
    MoodOption("😕", "Okay"),
    MoodOption("🙂", "Good"),
    MoodOption("😊", "Great"),
    MoodOption("🤩", "Amazing")
)

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    userName: String?,
    onMoodClick: () -> Unit,
    onFocusClick: () -> Unit,
    onGoToStats: () -> Unit,
    onGoToHabits: () -> Unit,
    onGoToPlans: () -> Unit,
    onProfileClick: () -> Unit,
    onOpenProgress: () -> Unit = {}
) {
    val firstName = userName
        ?.split(" ")?.firstOrNull()?.replaceFirstChar { it.uppercase() }
        ?: "there"

    val greeting = remember {
        val h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when { h < 12 -> "Good Morning"; h < 17 -> "Good Afternoon"; else -> "Good Evening" }
    }
    var selectedMoodIndex by remember { mutableStateOf<Int?>(null) }

    // ── Habits for today ─────────────────────────────────────────────────────
    val habitVm: HabitListViewModel = hiltViewModel()
    val habitsState by habitVm.state.collectAsState()
    val todayDow = remember { Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1 }
    val todayHabits = remember(habitsState.habits) {
        habitsState.habits.filter { todayDow in it.targetDays }
    }
    // Load current month checkins for the mini progress view
    LaunchedEffect(Unit) { habitVm.loadMonthProgress() }
    val monthCheckinDates = remember(habitsState.monthCheckins) {
        habitsState.monthCheckins.map { it.date }.toSet()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PageBg)
            .padding(start = 16.dp, end = 16.dp, top = 28.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ── Header ──────────────────────────────────────────────────────
        HomeHeader(
            greeting = greeting,
            firstName = firstName,
            onProfileClick = onProfileClick
        )

        // ── Mood card ────────────────────────────────────────────────────
        MoodCard(
            modifier = Modifier.fillMaxWidth(),
            selectedIndex = selectedMoodIndex,
            onSelectMood = { selectedMoodIndex = it },
            onOpen = onMoodClick
        )

        // ── Focus card ───────────────────────────────────────────────────
        FocusCard(onClick = onFocusClick)

        // ── Bento grid: fills all remaining vertical space ───────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Left: Activities + Progress — tall 1×2
            StreakCard(
                modifier = Modifier
                    .weight(0.9f)
                    .fillMaxHeight(),
                todayHabits = todayHabits,
                checkedIds = habitsState.todayCheckedHabitIds,
                monthCheckinDates = monthCheckinDates,
                onToggle = habitVm::toggleToday,
                onClick = onGoToHabits,
                onOpenProgress = onOpenProgress
            )
            // Right: Dashboard + Plans stacked 1×1 each
            Column(
                modifier = Modifier
                    .weight(1.1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatsCard(modifier = Modifier.weight(1f).fillMaxWidth(), onClick = onGoToStats)
                PlansCard(modifier = Modifier.weight(1f).fillMaxWidth(), onClick = onGoToPlans)
            }
        }
    }
}

// ── Header ────────────────────────────────────────────────────────────────────

@Composable
private fun HomeHeader(
    greeting: String,
    firstName: String,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = greeting,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = Poppins,
                color = Color.Gray
            )
            Text(
                text = firstName,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins,
                color = Color(0xFF1A1A1A)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .shadow(2.dp, CircleShape, clip = false)
                    .clip(CircleShape)
                    .background(CardBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.DarkGray,
                    modifier = Modifier.size(18.dp)
                )
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(AppGreen.copy(alpha = 0.15f))
                    .border(1.5.dp, AppGreen, CircleShape)
                    .clickable { onProfileClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = firstName.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Poppins,
                    color = AppGreen
                )
            }
        }
    }
}

// ── Mood card — full-width green gradient ─────────────────────────────────────

@Composable
private fun MoodCard(
    modifier: Modifier = Modifier,
    selectedIndex: Int?,
    onSelectMood: (Int) -> Unit,
    onOpen: () -> Unit
) {
    val currentMood = selectedIndex?.let { moodOptions[it] }

    Column(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(24.dp), clip = false)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(listOf(Color(0xFF4CBB53), Color(0xFF2A7D31)))
            )
            .clickable { onOpen() }
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Top row: label + status + arrow
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Mood",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Poppins,
                    color = Color.White
                )
                Text(
                    text = if (currentMood != null) "Logged · ${currentMood.label}" else "Not logged yet",
                    fontSize = 11.sp,
                    fontFamily = Poppins,
                    color = Color.White.copy(alpha = 0.70f)
                )
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(Modifier.height(14.dp))

        // Mood picker dots — full width spaced evenly
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            moodOptions.forEachIndexed { i, mood ->
                MoodDot(
                    emoji = mood.emoji,
                    label = mood.label,
                    selected = selectedIndex == i,
                    onClick = { onSelectMood(i) }
                )
            }
        }
    }
}

@Composable
private fun MoodDot(emoji: String, label: String, selected: Boolean, onClick: () -> Unit) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.18f else 1f,
        animationSpec = tween(160),
        label = "dot"
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .scale(scale)
                .size(46.dp)
                .clip(CircleShape)
                .background(
                    if (selected) Color.White.copy(alpha = 0.28f)
                    else Color.White.copy(alpha = 0.12f)
                )
                .then(
                    if (selected) Modifier.border(2.dp, Color.White, CircleShape) else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 22.sp)
        }
        Spacer(Modifier.height(5.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontFamily = Poppins,
            color = if (selected) Color.White else Color.White.copy(alpha = 0.60f),
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

// ── Stats card ────────────────────────────────────────────────────────────────

@Composable
private fun StatsCard(modifier: Modifier = Modifier, onClick: () -> Unit) {
    LightCard(modifier = modifier, onClick = onClick) {
        CardTopRow(icon = Icons.Default.BarChart, iconBg = Color(0xFFEAF7EB), iconTint = BrandGreen)
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Dashboard",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Poppins,
            color = Color(0xFF1A1A1A)
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = "Screen time & stats",
            fontSize = 10.sp,
            fontFamily = Poppins,
            color = SubtleGray,
            lineHeight = 14.sp
        )
    }
}

// ── Left tall card — two sections inside one rectangle ───────────────────────

@Composable
private fun StreakCard(
    modifier: Modifier = Modifier,
    todayHabits: List<Habit> = emptyList(),
    checkedIds: Set<String> = emptySet(),
    monthCheckinDates: Set<String> = emptySet(),
    onToggle: (String) -> Unit = {},
    onClick: () -> Unit,
    onOpenProgress: () -> Unit = {}
) {
    val visibleHabits = todayHabits.take(3)
    val extra = todayHabits.size - visibleHabits.size

    // Current week: Mon→Sun dates for mini dots
    val weekDates: List<Pair<String, String>> = remember {
        // Build Mon-Sun of this week
        val cal = Calendar.getInstance()
        // Find Monday: DAY_OF_WEEK 2=Mon; go back to Monday
        val todayDow = cal.get(Calendar.DAY_OF_WEEK) // 1=Sun…7=Sat
        val daysFromMon = if (todayDow == 1) 6 else todayDow - 2
        cal.add(Calendar.DAY_OF_MONTH, -daysFromMon)
        val fmt = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        val dayLabels = listOf("M", "T", "W", "T", "F", "S", "S")
        (0..6).map { i ->
            val label = dayLabels[i]
            val date  = fmt.format(cal.time)
            cal.add(Calendar.DAY_OF_MONTH, 1)
            label to date
        }
    }
    val todayDateStr = remember {
        java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
            .format(Calendar.getInstance().time)
    }

    // Outer rectangle — same card style as before
    Column(
        modifier = modifier
            .shadow(2.dp, RoundedCornerShape(20.dp), clip = false)
            .clip(RoundedCornerShape(20.dp))
            .background(CardBg)
    ) {

        // ── Section 1: Top Activities (50%) ──────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clickable { onClick() }
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header row
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(RoundedCornerShape(9.dp))
                                .background(Color(0xFFFFF3E0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = Color(0xFFE65100),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                        Text(
                            text = "Activities",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Poppins,
                            color = Color(0xFF1A1A1A)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = null,
                        tint = SubtleGray,
                        modifier = Modifier.size(15.dp)
                    )
                }

                // Habits list
                if (todayHabits.isEmpty()) {
                    Text(
                        text = "No habits\ntoday",
                        fontSize = 10.sp,
                        fontFamily = Poppins,
                        color = SubtleGray,
                        lineHeight = 14.sp
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        visibleHabits.forEach { habit ->
                            val checked = habit.id in checkedIds
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(7.dp))
                                    .background(
                                        if (checked) Color(0xFFEDF7EE) else Color(0xFFF5F5F5)
                                    )
                                    .clickable { onToggle(habit.id) }
                                    .padding(horizontal = 6.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(habit.icon, fontSize = 11.sp)
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = habit.name,
                                        fontSize = 9.sp,
                                        fontFamily = Poppins,
                                        fontWeight = FontWeight.Medium,
                                        color = if (checked) Color(0xFF2A7D31) else Color(0xFF1A1A1A),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Icon(
                                    imageVector = if (checked) Icons.Default.CheckCircle
                                                  else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (checked) BrandGreen else Color(0xFFCCCCCC),
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                        if (extra > 0) {
                            Text(
                                text = "+$extra more",
                                fontSize = 9.sp,
                                fontFamily = Poppins,
                                color = SubtleGray
                            )
                        }
                    }
                }
            }

            // Done counter
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = checkedIds.size.toString(),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Poppins,
                    color = Color(0xFF1A1A1A),
                    lineHeight = 28.sp
                )
                Text(
                    text = "/ ${todayHabits.size}",
                    fontSize = 11.sp,
                    fontFamily = Poppins,
                    color = SubtleGray,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }
        }

        // ── Divider ───────────────────────────────────────────────────
        HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)

        // ── Section 2: Progress (50%) ─────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clickable { onOpenProgress() }
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(9.dp))
                            .background(Color(0xFFE8F5E9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = BrandGreen,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    Text(
                        text = "Progress",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Poppins,
                        color = Color(0xFF1A1A1A)
                    )
                }
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = SubtleGray,
                    modifier = Modifier.size(15.dp)
                )
            }

            // Mini week dots — M T W T F S S
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    weekDates.forEach { (label, _) ->
                        Text(
                            text = label,
                            fontSize = 8.sp,
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Medium,
                            color = SubtleGray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    weekDates.forEach { (_, date) ->
                        val isFuture = date > todayDateStr
                        val isToday  = date == todayDateStr
                        val hasCheckin = date in monthCheckinDates
                        val dotColor = when {
                            isFuture  -> Color(0xFFF0F0F0)
                            hasCheckin -> BrandGreen
                            isToday   -> Color(0xFFE8F5E9)
                            else      -> Color(0xFFF0F0F0)
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 2.dp)
                                .aspectRatio(1f)
                                .clip(CircleShape)
                                .background(dotColor)
                                .then(
                                    if (isToday) Modifier.border(1.5.dp, BrandGreen, CircleShape)
                                    else Modifier
                                )
                        )
                    }
                }
            }

            // CTA text
            Text(
                text = "View full calendar →",
                fontSize = 9.sp,
                fontFamily = Poppins,
                color = BrandGreen,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ── Plans card (compact 1×1) ──────────────────────────────────────────────────

@Composable
private fun PlansCard(modifier: Modifier = Modifier, onClick: () -> Unit) {
    LightCard(modifier = modifier, onClick = onClick) {
        CardTopRow(
            icon = Icons.Default.DateRange,
            iconBg = Color(0xFFF3F0FF),
            iconTint = Color(0xFF7C3AED)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "My Plans",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Poppins,
            color = Color(0xFF1A1A1A)
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = "Goals & routines",
            fontSize = 10.sp,
            fontFamily = Poppins,
            color = SubtleGray,
            lineHeight = 14.sp
        )
    }
}

// ── Focus card (full-width) ───────────────────────────────────────────────────

@Composable
private fun FocusCard(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(20.dp), clip = false)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF1C2B1E))
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left: icon pill + title
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF2E4530)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = BrandGreen,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(
                    text = "Focus Session",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Poppins,
                    color = Color.White
                )
                Text(
                    text = "Pomodoro · history",
                    fontSize = 11.sp,
                    fontFamily = Poppins,
                    color = Color.White.copy(alpha = 0.45f)
                )
            }
        }
        // Right: Start button
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(BrandGreen)
                .padding(horizontal = 22.dp, vertical = 9.dp)
        ) {
            Text(
                text = "Start",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins,
                color = Color.White
            )
        }
    }
}

// ── Shared shells ─────────────────────────────────────────────────────────────

@Composable
private fun LightCard(
    modifier: Modifier = Modifier,
    bgColor: Color = CardBg,
    onClick: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .shadow(2.dp, RoundedCornerShape(20.dp), clip = false)
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(16.dp)
    ) { content() }
}

@Composable
private fun CardTopRow(icon: ImageVector, iconBg: Color, iconTint: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = SubtleGray,
            modifier = Modifier.size(20.dp)
        )
    }
}
