package com.example.healthapp.habits.presentation

import androidx.compose.ui.graphics.Color

/** Shared palette for habit cards, heatmaps, and the create/edit color picker. */
object HabitColors {
    val palette: List<String> = listOf(
        "#43A047", // green
        "#5C6BC0", // indigo
        "#FF9800", // orange
        "#E91E63", // pink
        "#00ACC1", // cyan
        "#8E24AA"  // purple
    )

    fun parse(hex: String): Color = try {
        val clean = hex.removePrefix("#").removePrefix("0x")
        val rgb = clean.toLong(16) and 0x00FFFFFFL
        Color(0xFF000000L or rgb)
    } catch (_: Throwable) {
        Color(0xFF43A047)
    }
}

val HabitIconPalette: List<String> = listOf(
    "🌱", "💪", "📚", "🧘", "🚶", "💧",
    "☀️", "🍎", "😴", "🎯", "📵", "⏰"
)
