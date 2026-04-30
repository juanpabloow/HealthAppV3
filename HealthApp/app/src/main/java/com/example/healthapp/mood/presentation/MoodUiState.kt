package com.example.healthapp.mood.presentation

import com.example.healthapp.mood.domain.model.MoodEntry

data class MoodUiState(
    val todayEntry: MoodEntry? = null,
    val entries: List<MoodEntry> = emptyList(),
    val selectedMood: String = "",
    val note: String = "",
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)
