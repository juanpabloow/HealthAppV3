package com.example.healthapp.habits.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun HabitHeatmap(
    checkedDates: Set<String>,
    habitColor: Color,
    modifier: Modifier = Modifier
) {
    val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val today = Calendar.getInstance()
    val todayStr = fmt.format(today.time)

    // End of the current week (next Saturday inclusive)
    val end = Calendar.getInstance().apply {
        while (get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
            add(Calendar.DAY_OF_YEAR, 1)
        }
    }
    // 5 weeks back (35 cells, including the trailing future days of this week)
    val start = Calendar.getInstance().apply {
        timeInMillis = end.timeInMillis
        add(Calendar.DAY_OF_YEAR, -34)
    }

    val rows: List<List<Cell>> = buildList {
        val cursor = Calendar.getInstance().apply { timeInMillis = start.timeInMillis }
        repeat(5) {
            val week = mutableListOf<Cell>()
            repeat(7) {
                val ds = fmt.format(cursor.time)
                week.add(
                    Cell(
                        dateStr = ds,
                        checked = ds in checkedDates,
                        isToday = ds == todayStr,
                        isFuture = cursor.timeInMillis > today.timeInMillis &&
                                ds != todayStr
                    )
                )
                cursor.add(Calendar.DAY_OF_YEAR, 1)
            }
            add(week)
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        rows.forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                week.forEach { cell ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when {
                                    cell.isFuture -> Color.Transparent
                                    cell.checked  -> habitColor
                                    else          -> Color(0xFFEFEFEF)
                                }
                            )
                            .let {
                                if (cell.isToday) {
                                    it.border(2.dp, habitColor, RoundedCornerShape(8.dp))
                                } else it
                            }
                    )
                }
            }
        }
    }
}

private data class Cell(
    val dateStr: String,
    val checked: Boolean,
    val isToday: Boolean,
    val isFuture: Boolean
)
