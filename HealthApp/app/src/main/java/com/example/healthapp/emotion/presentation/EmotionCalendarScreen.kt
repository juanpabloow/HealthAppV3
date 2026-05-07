package com.example.healthapp.emotion.presentation

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
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
import com.example.healthapp.emotion.domain.util.dateString
import com.example.healthapp.emotion.presentation.components.DayDetailSheet
import com.example.healthapp.ui.theme.AppGreen
import com.example.healthapp.ui.theme.Poppins
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private val PageBg = Color(0xFFF5F7FA)
private val TodayBlue = Color(0xFF1E88E5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmotionCalendarScreen(
    modifier: Modifier = Modifier,
    state: EmotionUiState,
    onBack: () -> Unit,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDayClick: (String) -> Unit,
    onDismissDay: () -> Unit
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
                text = "Mood History",
                fontSize = 17.sp,
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            // right spacer to mirror leading IconButton width
            Spacer(modifier = Modifier.size(48.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 12.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(vertical = 16.dp)) {
                    MonthHeader(
                        year = state.visibleYear,
                        monthZeroBased = state.visibleMonthZeroBased,
                        onPrev = onPrevMonth,
                        onNext = onNextMonth
                    )
                    Spacer(Modifier.height(8.dp))
                    WeekdayHeader()
                    Spacer(Modifier.height(4.dp))
                    MonthGrid(
                        year = state.visibleYear,
                        monthZeroBased = state.visibleMonthZeroBased,
                        entriesByDate = state.monthEntries.mapValues { it.value.emotion.emoji },
                        onDayClick = onDayClick
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Legend()
            Spacer(Modifier.height(40.dp))
        }
    }

    if (state.selectedDayDate != null) {
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = onDismissDay,
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            DayDetailSheet(
                date = state.selectedDayDate,
                entry = state.selectedDayEntry
            )
        }
    }
}

@Composable
private fun MonthHeader(
    year: Int,
    monthZeroBased: Int,
    onPrev: () -> Unit,
    onNext: () -> Unit
) {
    val cal = Calendar.getInstance().apply {
        clear(); set(year, monthZeroBased, 1)
    }
    val title = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(cal.time)
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .clickable { onPrev() },
            contentAlignment = Alignment.Center
        ) {
            Text("‹", fontSize = 22.sp, color = Color.DarkGray)
        }
        Text(
            text = title,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = Poppins,
            color = Color(0xFF1A1A1A)
        )
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .clickable { onNext() },
            contentAlignment = Alignment.Center
        ) {
            Text("›", fontSize = 22.sp, color = Color.DarkGray)
        }
    }
}

@Composable
private fun WeekdayHeader() {
    val labels = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
        labels.forEach { l ->
            Text(
                text = l,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 11.sp,
                color = Color.Gray,
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun MonthGrid(
    year: Int,
    monthZeroBased: Int,
    entriesByDate: Map<String, String>,
    onDayClick: (String) -> Unit
) {
    val cal = Calendar.getInstance().apply {
        clear(); set(year, monthZeroBased, 1)
    }
    // Sunday = 1 in Calendar.DAY_OF_WEEK; we want 0-based offset Sun..Sat
    val leadingBlanks = cal.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY
    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

    val today = Calendar.getInstance()
    val isCurrentMonth = today.get(Calendar.YEAR) == year && today.get(Calendar.MONTH) == monthZeroBased
    val todayDay = if (isCurrentMonth) today.get(Calendar.DAY_OF_MONTH) else -1

    // Build a list of cell content: null for blank, day number otherwise. Pad to 42 (6 rows × 7).
    val cells: List<Int?> = buildList {
        repeat(leadingBlanks) { add(null) }
        for (d in 1..daysInMonth) add(d)
        while (size < 42) add(null)
    }

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
        cells.chunked(7).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { day ->
                    DayCell(
                        day = day,
                        emoji = day?.let { entriesByDate[dateString(year, monthZeroBased, it)] },
                        isToday = day != null && day == todayDay,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f),
                        onClick = {
                            if (day != null) onDayClick(dateString(year, monthZeroBased, day))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    day: Int?,
    emoji: String?,
    isToday: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clickable(enabled = day != null) { onClick() }
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        if (day == null) return@Box

        // Visible inner dot (smaller than tap area)
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .let {
                    if (emoji != null) it.background(AppGreen.copy(alpha = 0.10f))
                    else it
                }
                .let {
                    if (isToday) it.border(2.dp, TodayBlue, CircleShape) else it
                },
            contentAlignment = Alignment.Center
        ) {
            if (emoji != null) {
                Text(emoji, fontSize = 18.sp)
            } else {
                Text(
                    text = day.toString(),
                    fontSize = 13.sp,
                    fontFamily = Poppins,
                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium,
                    color = if (isToday) TodayBlue else Color(0xFF555555)
                )
            }
        }
    }
}

@Composable
private fun Legend() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LegendItem(swatch = { Box(Modifier.size(10.dp).clip(CircleShape).background(AppGreen.copy(alpha = 0.4f))) }, label = "Logged")
        LegendItem(swatch = { Box(Modifier.size(10.dp).clip(CircleShape).border(2.dp, TodayBlue, CircleShape)) }, label = "Today")
    }
}

@Composable
private fun LegendItem(swatch: @Composable () -> Unit, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        swatch()
        Spacer(Modifier.size(6.dp))
        Text(label, fontSize = 11.sp, color = Color.Gray, fontFamily = Poppins)
    }
}
