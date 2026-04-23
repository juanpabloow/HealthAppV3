package com.example.healthapp.plans.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthapp.auth.domain.usecase.GetCurrentUserUseCase
import com.example.healthapp.plans.domain.model.Plan
import com.example.healthapp.plans.domain.model.PlanMetrics
import com.example.healthapp.plans.domain.model.PlanSession
import com.example.healthapp.plans.domain.usecase.DeletePlanUseCase
import com.example.healthapp.plans.domain.usecase.GetPlanSessionsUseCase
import com.example.healthapp.plans.domain.usecase.GetPlansUseCase
import com.example.healthapp.plans.domain.usecase.SavePlanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class PlanViewModel @Inject constructor(
    private val getPlansUseCase: GetPlansUseCase,
    private val savePlanUseCase: SavePlanUseCase,
    private val deletePlanUseCase: DeletePlanUseCase,
    private val getPlanSessionsUseCase: GetPlanSessionsUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PlanUiState())
    val state: StateFlow<PlanUiState> = _state

    private val userId: String get() = getCurrentUserUseCase() ?: ""

    init {
        loadPlans()
    }

    fun loadPlans() {
        val uid = userId
        if (uid.isBlank()) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            getPlansUseCase(uid)
                .onSuccess { plans ->
                    if (plans.isEmpty()) seedDefaultPlans(uid)
                    else _state.update { it.copy(plans = plans, isLoading = false) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun loadSessions(planId: String) {
        if (_state.value.sessions.containsKey(planId)) return
        viewModelScope.launch {
            getPlanSessionsUseCase(planId)
                .onSuccess { sessions ->
                    _state.update { s -> s.copy(sessions = s.sessions + (planId to sessions)) }
                }
        }
    }

    fun deletePlan(planId: String) {
        viewModelScope.launch {
            deletePlanUseCase(planId)
                .onSuccess {
                    _state.update { s -> s.copy(plans = s.plans.filter { it.id != planId }) }
                }
                .onFailure { e -> _state.update { it.copy(error = e.message) } }
        }
    }

    fun reloadAfterSave() {
        val uid = userId
        if (uid.isBlank()) return
        viewModelScope.launch {
            getPlansUseCase(uid)
                .onSuccess { plans -> _state.update { it.copy(plans = plans) } }
        }
    }

    fun clearError() = _state.update { it.copy(error = null) }

    fun computeMetrics(plan: Plan, sessions: List<PlanSession>): PlanMetrics {
        val now = System.currentTimeMillis()
        val cal = Calendar.getInstance()

        // Start of current week (Sunday)
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
        val weekStart = cal.timeInMillis
        val weekEnd = weekStart + 7L * 24 * 60 * 60 * 1000

        var scheduledPast = 0
        var scheduledThisWeek = 0

        val iter = Calendar.getInstance()
        iter.timeInMillis = plan.createdAt
        iter.set(Calendar.HOUR_OF_DAY, 0); iter.set(Calendar.MINUTE, 0)
        iter.set(Calendar.SECOND, 0); iter.set(Calendar.MILLISECOND, 0)

        while (iter.timeInMillis <= now) {
            val dow = iter.get(Calendar.DAY_OF_WEEK) - 1 // 0=Sun
            if (dow in plan.scheduleDays) {
                scheduledPast++
                if (iter.timeInMillis in weekStart until weekEnd) scheduledThisWeek++
            }
            iter.add(Calendar.DAY_OF_YEAR, 1)
        }

        val completedThisWeek = sessions.count {
            it.date in weekStart until weekEnd && it.completed
        }
        val weeklyProgress = if (scheduledThisWeek > 0)
            (completedThisWeek.toFloat() / scheduledThisWeek * 100).toInt()
        else 0

        return PlanMetrics(
            sessionsCompleted = scheduledPast,
            deviationPercent = 0,
            focusHours = scheduledPast * (plan.endHour - plan.startHour),
            weeklyProgressPercent = weeklyProgress
        )
    }

    // ── Default plan seeding ─────────────────────────────────────────────
    private suspend fun seedDefaultPlans(uid: String) {
        listOf(
            Plan(
                userId = uid, name = "Work Focus", icon = "💼",
                description = "Deep work sessions, no distractions",
                scheduleDays = listOf(1, 2, 3, 4, 5),
                startHour = 9, endHour = 17, strictness = "high"
            ),
            Plan(
                userId = uid, name = "Final Project", icon = "📚",
                description = "Study sessions for your final project",
                scheduleDays = listOf(0, 2, 4, 6),
                startHour = 18, endHour = 22, strictness = "medium"
            )
        ).forEach { savePlanUseCase(it) }

        getPlansUseCase(uid).onSuccess { plans ->
            _state.update { it.copy(plans = plans, isLoading = false) }
        }
    }
}
