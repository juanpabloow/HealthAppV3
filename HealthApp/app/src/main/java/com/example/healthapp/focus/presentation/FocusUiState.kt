package com.example.healthapp.focus.presentation

import com.example.healthapp.focus.domain.model.FocusSession
import com.example.healthapp.focus.domain.model.FocusStats
import com.example.healthapp.plans.domain.model.Plan

enum class TimerMode { IDLE, RUNNING, PAUSED, COMPLETED }
enum class FocusRange { WEEK, MONTH, ALL }

data class FocusUiState(
    // ── Timer ────────────────────────────────────────────────────────────
    val mode: TimerMode = TimerMode.IDLE,
    val plannedMinutes: Int = 25,
    val remainingMs: Long = 25L * 60_000L,
    val sessionStartedAt: Long = 0L,
    val currentSessionId: String? = null,

    // ── Plan link ────────────────────────────────────────────────────────
    val linkedPlanId: String? = null,
    val availablePlans: List<Plan> = emptyList(),
    val showPlanPicker: Boolean = false,

    // ── Post-session mood prompt ─────────────────────────────────────────
    val showMoodSheet: Boolean = false,
    val lastCompletedActualMinutes: Int = 0,

    // ── History ──────────────────────────────────────────────────────────
    val recentSessions: List<FocusSession> = emptyList(),

    // ── Stats ────────────────────────────────────────────────────────────
    val statsRange: FocusRange = FocusRange.WEEK,
    val stats: FocusStats? = null,

    // ── Common ───────────────────────────────────────────────────────────
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
)
