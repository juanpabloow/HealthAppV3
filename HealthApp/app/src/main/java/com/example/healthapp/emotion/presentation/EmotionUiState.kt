package com.example.healthapp.emotion.presentation

import com.example.healthapp.emotion.domain.model.Emotion
import com.example.healthapp.emotion.domain.model.EmotionEntry
import com.example.healthapp.emotion.domain.model.EmotionStats
import java.util.Calendar

enum class StatsRange { WEEK, MONTH, ALL }

data class EmotionUiState(
    val todayEntry: EmotionEntry? = null,
    val selectedEmotion: Emotion? = null,
    val noteDraft: String = "",

    val visibleYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    val visibleMonthZeroBased: Int = Calendar.getInstance().get(Calendar.MONTH),
    val monthEntries: Map<String, EmotionEntry> = emptyMap(),
    val selectedDayDate: String? = null,
    val selectedDayEntry: EmotionEntry? = null,

    val statsRange: StatsRange = StatsRange.WEEK,
    val stats: EmotionStats? = null,

    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)
