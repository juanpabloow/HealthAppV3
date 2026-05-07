package com.example.healthapp.habits.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthapp.auth.domain.usecase.GetCurrentUserUseCase
import com.example.healthapp.habits.domain.model.HabitCheckin
import com.example.healthapp.habits.domain.usecase.DeleteHabitUseCase
import com.example.healthapp.habits.domain.usecase.GetCheckinsForDateUseCase
import com.example.healthapp.habits.domain.usecase.GetHabitCheckinsUseCase
import com.example.healthapp.habits.domain.usecase.GetHabitsUseCase
import com.example.healthapp.habits.domain.usecase.ToggleHabitCheckinUseCase
import com.example.healthapp.habits.domain.util.computeHabitStats
import com.example.healthapp.habits.domain.util.startOfDayMs
import com.example.healthapp.habits.domain.util.todayDateString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HabitListViewModel @Inject constructor(
    private val getCurrentUser: GetCurrentUserUseCase,
    private val getHabits: GetHabitsUseCase,
    private val deleteHabit: DeleteHabitUseCase,
    private val toggleCheckin: ToggleHabitCheckinUseCase,
    private val getCheckins: GetHabitCheckinsUseCase,
    private val getCheckinsForDate: GetCheckinsForDateUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HabitsUiState())
    val state: StateFlow<HabitsUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        val uid = getCurrentUser() ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            getHabits(uid).fold(
                onSuccess = { habits ->
                    _state.update { it.copy(isLoading = false, habits = habits) }
                    refreshTodayCheckins()
                },
                onFailure = { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
            )
        }
    }

    fun reloadAfterSave() = load()

    /**
     * Toggles today's check-in for [habitId] with full optimistic state updates so
     * both the list and the detail screen reflect the change instantly. Rolls
     * back all three slices ([todayCheckedHabitIds], [checkinsByHabit],
     * [statsByHabit]) on persistence failure.
     */
    fun toggleToday(habitId: String) {
        val uid = getCurrentUser() ?: return
        val today = todayDateString()
        val current = _state.value
        val nowChecked = habitId !in current.todayCheckedHabitIds

        // Snapshot for rollback
        val previousTodaySet = current.todayCheckedHabitIds
        val previousCheckins = current.checkinsByHabit[habitId] ?: emptyList()
        val previousStats = current.statsByHabit[habitId]

        // Build the optimistic checkins list and recompute stats locally
        val optimisticCheckins = if (nowChecked) {
            previousCheckins.filterNot { it.date == today } + HabitCheckin(
                id = "${habitId}_$today",
                habitId = habitId,
                userId = uid,
                date = today,
                dateMs = startOfDayMs(today),
                note = null,
                createdAt = System.currentTimeMillis()
            )
        } else {
            previousCheckins.filterNot { it.date == today }
        }
        val optimisticStats = computeHabitStats(optimisticCheckins)

        _state.update {
            it.copy(
                todayCheckedHabitIds = if (nowChecked) {
                    it.todayCheckedHabitIds + habitId
                } else {
                    it.todayCheckedHabitIds - habitId
                },
                checkinsByHabit = it.checkinsByHabit + (habitId to optimisticCheckins),
                statsByHabit = it.statsByHabit + (habitId to optimisticStats),
                error = null
            )
        }

        viewModelScope.launch {
            toggleCheckin(uid, habitId, today, nowChecked).onFailure { e ->
                _state.update {
                    it.copy(
                        todayCheckedHabitIds = previousTodaySet,
                        checkinsByHabit = it.checkinsByHabit + (habitId to previousCheckins),
                        statsByHabit = if (previousStats != null) {
                            it.statsByHabit + (habitId to previousStats)
                        } else {
                            it.statsByHabit - habitId
                        },
                        error = e.message ?: "Couldn't save check-in"
                    )
                }
            }
        }
    }

    fun loadDetail(habitId: String) {
        viewModelScope.launch { refreshHabit(habitId) }
    }

    fun delete(habitId: String) {
        viewModelScope.launch {
            deleteHabit(habitId).onSuccess {
                _state.update {
                    it.copy(
                        habits = it.habits.filter { h -> h.id != habitId },
                        todayCheckedHabitIds = it.todayCheckedHabitIds - habitId,
                        checkinsByHabit = it.checkinsByHabit - habitId,
                        statsByHabit = it.statsByHabit - habitId
                    )
                }
            }.onFailure { e ->
                _state.update { it.copy(error = e.message ?: "Couldn't delete") }
            }
        }
    }

    fun clearError() = _state.update { it.copy(error = null) }

    private fun refreshTodayCheckins() {
        val uid = getCurrentUser() ?: return
        val today = todayDateString()
        viewModelScope.launch {
            getCheckinsForDate(uid, today).onSuccess { list ->
                _state.update {
                    it.copy(todayCheckedHabitIds = list.map { c -> c.habitId }.toSet())
                }
            }
        }
    }

    private suspend fun refreshHabit(habitId: String) {
        val uid = getCurrentUser() ?: return
        getCheckins(uid, habitId, 0L, System.currentTimeMillis()).onSuccess { checkins ->
            _state.update {
                it.copy(
                    checkinsByHabit = it.checkinsByHabit + (habitId to checkins),
                    statsByHabit = it.statsByHabit + (habitId to computeHabitStats(checkins))
                )
            }
        }
    }
}
