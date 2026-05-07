package com.example.healthapp.habits.presentation

import com.example.healthapp.habits.domain.model.Habit
import com.example.healthapp.habits.domain.model.HabitCheckin
import com.example.healthapp.habits.domain.model.HabitStats

data class HabitsUiState(
    val habits: List<Habit> = emptyList(),
    val todayCheckedHabitIds: Set<String> = emptySet(),
    val checkinsByHabit: Map<String, List<HabitCheckin>> = emptyMap(),
    val statsByHabit: Map<String, HabitStats> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class CreateEditHabitUiState(
    val editingHabitId: String? = null,
    val name: String = "",
    val icon: String = "🌱",
    val description: String = "",
    val targetDays: List<Int> = listOf(0, 1, 2, 3, 4, 5, 6),
    val color: String = "#43A047",
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
) {
    val isValid: Boolean
        get() = name.isNotBlank() && targetDays.isNotEmpty()
}
