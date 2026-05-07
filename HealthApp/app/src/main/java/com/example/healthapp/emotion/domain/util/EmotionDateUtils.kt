package com.example.healthapp.emotion.domain.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private fun isoFormatter(): SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

fun todayDateString(): String =
    isoFormatter().format(Calendar.getInstance().time)

fun daysAgoDateString(days: Int): String {
    val cal = Calendar.getInstance()
    cal.add(Calendar.DAY_OF_YEAR, -days)
    return isoFormatter().format(cal.time)
}

fun firstDayOfMonth(year: Int, monthZeroBased: Int): String {
    val cal = Calendar.getInstance().apply {
        clear()
        set(year, monthZeroBased, 1)
    }
    return isoFormatter().format(cal.time)
}

fun lastDayOfMonth(year: Int, monthZeroBased: Int): String {
    val cal = Calendar.getInstance().apply {
        clear()
        set(year, monthZeroBased, 1)
    }
    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
    return isoFormatter().format(cal.time)
}

fun dateString(year: Int, monthZeroBased: Int, day: Int): String {
    val cal = Calendar.getInstance().apply {
        clear()
        set(year, monthZeroBased, day)
    }
    return isoFormatter().format(cal.time)
}
