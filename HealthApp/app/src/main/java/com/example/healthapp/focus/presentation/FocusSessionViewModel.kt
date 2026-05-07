package com.example.healthapp.focus.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthapp.auth.domain.usecase.GetCurrentUserUseCase
import com.example.healthapp.emotion.domain.model.Emotion
import com.example.healthapp.focus.domain.model.FocusSession
import com.example.healthapp.focus.domain.usecase.GetFocusStatsUseCase
import com.example.healthapp.focus.domain.usecase.GetRecentFocusSessionsUseCase
import com.example.healthapp.focus.domain.usecase.SaveFocusSessionUseCase
import com.example.healthapp.plans.domain.usecase.GetPlansUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class FocusSessionViewModel @Inject constructor(
    private val getCurrentUser: GetCurrentUserUseCase,
    private val saveFocusSession: SaveFocusSessionUseCase,
    private val getRecentSessions: GetRecentFocusSessionsUseCase,
    private val getStats: GetFocusStatsUseCase,
    private val getPlans: GetPlansUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(FocusUiState())
    val state: StateFlow<FocusUiState> = _state.asStateFlow()

    private var timerJob: Job? = null

    init {
        loadPlans()
        loadRecent()
        loadStats()
    }

    // ── Preset / plan selection ─────────────────────────────────────────
    fun selectPreset(minutes: Int) {
        if (_state.value.mode != TimerMode.IDLE) return
        _state.update {
            it.copy(plannedMinutes = minutes, remainingMs = minutes * 60_000L)
        }
    }

    fun showPlanPicker() = _state.update { it.copy(showPlanPicker = true) }
    fun hidePlanPicker() = _state.update { it.copy(showPlanPicker = false) }

    fun selectLinkedPlan(planId: String?) {
        _state.update { it.copy(linkedPlanId = planId, showPlanPicker = false) }
    }

    // ── Timer state machine ─────────────────────────────────────────────
    fun start() {
        val s = _state.value
        if (s.mode == TimerMode.RUNNING) return
        val now = System.currentTimeMillis()
        _state.update {
            it.copy(
                mode = TimerMode.RUNNING,
                sessionStartedAt = if (it.mode == TimerMode.IDLE) now else it.sessionStartedAt,
                remainingMs = if (it.mode == TimerMode.IDLE) it.plannedMinutes * 60_000L else it.remainingMs
            )
        }
        startTicker()
    }

    fun pause() {
        if (_state.value.mode != TimerMode.RUNNING) return
        timerJob?.cancel()
        _state.update { it.copy(mode = TimerMode.PAUSED) }
    }

    fun resume() {
        if (_state.value.mode != TimerMode.PAUSED) return
        _state.update { it.copy(mode = TimerMode.RUNNING) }
        startTicker()
    }

    fun stop() {
        val s = _state.value
        timerJob?.cancel()
        if (s.mode == TimerMode.RUNNING || s.mode == TimerMode.PAUSED) {
            // Save as aborted
            val plannedMs = s.plannedMinutes * 60_000L
            val elapsedMs = (plannedMs - s.remainingMs).coerceAtLeast(0L)
            persistSession(
                startedAt = s.sessionStartedAt,
                elapsedMs = elapsedMs,
                completed = false,
                openMoodSheet = false
            )
        }
        resetIdle()
    }

    private fun startTicker() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(TICK_MS)
                val current = _state.value
                if (current.mode != TimerMode.RUNNING) break
                val next = (current.remainingMs - TICK_MS).coerceAtLeast(0L)
                _state.update { it.copy(remainingMs = next) }
                if (next == 0L) {
                    onTimerComplete()
                    break
                }
            }
        }
    }

    private fun onTimerComplete() {
        val s = _state.value
        val plannedMs = s.plannedMinutes * 60_000L
        persistSession(
            startedAt = s.sessionStartedAt,
            elapsedMs = plannedMs,
            completed = true,
            openMoodSheet = true
        )
        _state.update { it.copy(mode = TimerMode.COMPLETED) }
    }

    private fun resetIdle() {
        _state.update {
            it.copy(
                mode = TimerMode.IDLE,
                remainingMs = it.plannedMinutes * 60_000L,
                sessionStartedAt = 0L,
                currentSessionId = null
            )
        }
    }

    // ── Mood after session ──────────────────────────────────────────────
    fun selectMoodAfter(emotion: Emotion?) {
        val s = _state.value
        val sessionId = s.currentSessionId ?: run {
            dismissMoodSheet()
            return
        }
        val uid = getCurrentUser() ?: run {
            dismissMoodSheet()
            return
        }
        val updated = FocusSession(
            id = sessionId,
            userId = uid,
            planId = s.linkedPlanId,
            startedAt = s.sessionStartedAt,
            endedAt = s.sessionStartedAt + s.lastCompletedActualMinutes * 60_000L,
            plannedMinutes = s.plannedMinutes,
            actualMinutes = s.lastCompletedActualMinutes,
            completed = true,
            mood = emotion?.name,
            createdAt = System.currentTimeMillis()
        )
        viewModelScope.launch {
            saveFocusSession(updated)
            refreshAfterPersist()
        }
        dismissMoodSheet()
    }

    fun dismissMoodSheet() {
        _state.update { it.copy(showMoodSheet = false) }
        resetIdle()
    }

    // ── Stats range ─────────────────────────────────────────────────────
    fun selectStatsRange(range: FocusRange) {
        _state.update { it.copy(statsRange = range) }
        loadStats()
    }

    fun clearError() = _state.update { it.copy(error = null) }

    // ── Loaders ─────────────────────────────────────────────────────────
    private fun loadPlans() {
        val uid = getCurrentUser() ?: return
        viewModelScope.launch {
            getPlans(uid).onSuccess { plans ->
                _state.update { it.copy(availablePlans = plans) }
            }
        }
    }

    private fun loadRecent() {
        val uid = getCurrentUser() ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getRecentSessions(uid, 50).fold(
                onSuccess = { list ->
                    _state.update { it.copy(isLoading = false, recentSessions = list) }
                },
                onFailure = { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
            )
        }
    }

    private fun loadStats() {
        val uid = getCurrentUser() ?: return
        val (from, to) = currentStatsRangeMs()
        viewModelScope.launch {
            getStats(uid, from, to).onSuccess { stats ->
                _state.update { it.copy(stats = stats) }
            }
        }
    }

    private fun refreshAfterPersist() {
        loadRecent()
        loadStats()
    }

    private fun persistSession(
        startedAt: Long,
        elapsedMs: Long,
        completed: Boolean,
        openMoodSheet: Boolean
    ) {
        val uid = getCurrentUser() ?: return
        val s = _state.value
        val actualMinutes = (elapsedMs / 60_000L).toInt()
        val now = System.currentTimeMillis()
        val session = FocusSession(
            id = s.currentSessionId ?: "",
            userId = uid,
            planId = s.linkedPlanId,
            startedAt = if (startedAt == 0L) now else startedAt,
            endedAt = now,
            plannedMinutes = s.plannedMinutes,
            actualMinutes = actualMinutes,
            completed = completed,
            mood = null,
            createdAt = now
        )
        _state.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            saveFocusSession(session).fold(
                onSuccess = { id ->
                    _state.update {
                        it.copy(
                            isSaving = false,
                            currentSessionId = id,
                            showMoodSheet = openMoodSheet,
                            lastCompletedActualMinutes = actualMinutes
                        )
                    }
                    refreshAfterPersist()
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(isSaving = false, error = e.message ?: "Save failed")
                    }
                }
            )
        }
    }

    private fun currentStatsRangeMs(): Pair<Long, Long> {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59); set(Calendar.MILLISECOND, 999)
        }
        val to = cal.timeInMillis
        when (_state.value.statsRange) {
            FocusRange.WEEK  -> cal.add(Calendar.DAY_OF_YEAR, -6)
            FocusRange.MONTH -> cal.add(Calendar.DAY_OF_YEAR, -29)
            FocusRange.ALL   -> { return 0L to to }
        }
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis to to
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }

    private companion object {
        const val TICK_MS = 250L
    }
}
