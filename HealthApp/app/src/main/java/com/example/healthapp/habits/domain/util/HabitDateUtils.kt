package com.example.healthapp.habits.domain.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private fun isoFormatter() = SimpleDateFormat("yyyy-MM-dd", Locale.US)

fun todayDateString(): String =
    isoFormatter().format(Calendar.getInstance().time)

fun startOfDayMs(): Long = Calendar.getInstance().apply {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}.timeInMillis

fun startOfDayMs(dateString: String): Long = runCatching {
    isoFormatter().parse(dateString)?.time ?: 0L
}.getOrDefault(0L)
