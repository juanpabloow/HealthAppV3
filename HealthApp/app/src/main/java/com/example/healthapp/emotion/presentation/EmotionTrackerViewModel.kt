package com.example.healthapp.emotion.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthapp.auth.domain.usecase.GetCurrentUserUseCase
import com.example.healthapp.emotion.domain.model.Emotion
import com.example.healthapp.emotion.domain.model.EmotionEntry
import com.example.healthapp.emotion.domain.usecase.GetEmotionStatsUseCase
import com.example.healthapp.emotion.domain.usecase.GetEmotionsByRangeUseCase
import com.example.healthapp.emotion.domain.usecase.GetTodayEmotionUseCase
import com.example.healthapp.emotion.domain.usecase.SaveEmotionUseCase
import com.example.healthapp.emotion.domain.util.dateString
import com.example.healthapp.emotion.domain.util.daysAgoDateString
import com.example.healthapp.emotion.domain.util.firstDayOfMonth
import com.example.healthapp.emotion.domain.util.lastDayOfMonth
import com.example.healthapp.emotion.domain.util.todayDateString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmotionTrackerViewModel @Inject constructor(
    private val getCurrentUser: GetCurrentUserUseCase,
    private val saveEmotion: SaveEmotionUseCase,
    private val getToday: GetTodayEmotionUseCase,
    private val getRange: GetEmotionsByRangeUseCase,
    private val getStats: GetEmotionStatsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(EmotionUiState())
    val state: StateFlow<EmotionUiState> = _state.asStateFlow()

    init {
        loadToday()
        loadVisibleMonth()
        loadStats()
    }

    // ── Picker ───────────────────────────────────────────────────────────────
    fun selectEmotion(emotion: Emotion) {
        _state.update { it.copy(selectedEmotion = emotion) }
    }

    fun setNoteDraft(text: String) {
        _state.update { it.copy(noteDraft = text.take(280)) }
    }

    fun saveCurrent() {
        val uid = getCurrentUser() ?: run {
            _state.update { it.copy(error = "Not signed in") }
            return
        }
        val emotion = _state.value.selectedEmotion ?: return
        val today = todayDateString()
        val now = System.currentTimeMillis()
        val existing = _state.value.todayEntry
        val entry = EmotionEntry(
            date = today,
            emotion = emotion,
            note = _state.value.noteDraft.trim().ifEmpty { null },
            createdAt = existing?.createdAt ?: now,
            updatedAt = now
        )
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            saveEmotion(uid, entry).fold(
                onSuccess = {
                    _state.update {
                        it.copy(
                            isSaving = false,
                            isSaved = true,
                            todayEntry = entry,
                            monthEntries = it.monthEntries + (today to entry)
                        )
                    }
                    refreshStatsSilently()
                },
                onFailure = { e ->
                    _state.update { it.copy(isSaving = false, error = e.message ?: "Save failed") }
                }
            )
        }
    }

    fun resetSaved() = _state.update { it.copy(isSaved = false) }

    // ── Calendar ─────────────────────────────────────────────────────────────
    fun nextMonth() {
        _state.update {
            val (y, m) = nextMonth(it.visibleYear, it.visibleMonthZeroBased)
            it.copy(visibleYear = y, visibleMonthZeroBased = m)
        }
        loadVisibleMonth()
    }

    fun previousMonth() {
        _state.update {
            val (y, m) = prevMonth(it.visibleYear, it.visibleMonthZeroBased)
            it.copy(visibleYear = y, visibleMonthZeroBased = m)
        }
        loadVisibleMonth()
    }

    fun selectDay(date: String) {
        val entry = _state.value.monthEntries[date]
        _state.update { it.copy(selectedDayDate = date, selectedDayEntry = entry) }
    }

    fun clearDaySelection() =
        _state.update { it.copy(selectedDayDate = null, selectedDayEntry = null) }

    // ── Stats ────────────────────────────────────────────────────────────────
    fun selectStatsRange(range: StatsRange) {
        _state.update { it.copy(statsRange = range) }
        loadStats()
    }

    fun clearError() = _state.update { it.copy(error = null) }

    // ── Loaders ──────────────────────────────────────────────────────────────
    private fun loadToday() {
        val uid = getCurrentUser() ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getToday(uid).fold(
                onSuccess = { entry ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            todayEntry = entry,
                            selectedEmotion = entry?.emotion ?: it.selectedEmotion,
                            noteDraft = entry?.note ?: it.noteDraft
                        )
                    }
                },
                onFailure = { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
            )
        }
    }

    private fun loadVisibleMonth() {
        val uid = getCurrentUser() ?: return
        val s = _state.value
        val from = firstDayOfMonth(s.visibleYear, s.visibleMonthZeroBased)
        val to = lastDayOfMonth(s.visibleYear, s.visibleMonthZeroBased)
        viewModelScope.launch {
            getRange(uid, from, to).fold(
                onSuccess = { list ->
                    _state.update { it.copy(monthEntries = list.associateBy { e -> e.date }) }
                },
                onFailure = { /* keep previous month entries on failure */ }
            )
        }
    }

    private fun loadStats() {
        val uid = getCurrentUser() ?: return
        val (from, to) = currentStatsRange()
        viewModelScope.launch {
            getStats(uid, from, to).fold(
                onSuccess = { stats -> _state.update { it.copy(stats = stats) } },
                onFailure = { /* leave previous stats */ }
            )
        }
    }

    private fun refreshStatsSilently() {
        val uid = getCurrentUser() ?: return
        val (from, to) = currentStatsRange()
        viewModelScope.launch {
            getStats(uid, from, to).onSuccess { stats ->
                _state.update { it.copy(stats = stats) }
            }
        }
    }

    private fun currentStatsRange(): Pair<String, String> {
        val today = todayDateString()
        return when (_state.value.statsRange) {
            StatsRange.WEEK  -> daysAgoDateString(6) to today
            StatsRange.MONTH -> daysAgoDateString(29) to today
            StatsRange.ALL   -> dateString(1970, 0, 1) to today
        }
    }

    private fun nextMonth(year: Int, m: Int): Pair<Int, Int> =
        if (m == 11) (year + 1) to 0 else year to (m + 1)

    private fun prevMonth(year: Int, m: Int): Pair<Int, Int> =
        if (m == 0) (year - 1) to 11 else year to (m - 1)
}
