package com.example.healthapp.habits.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthapp.auth.domain.usecase.GetCurrentUserUseCase
import com.example.healthapp.habits.domain.model.Habit
import com.example.healthapp.habits.domain.usecase.SaveHabitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateEditHabitViewModel @Inject constructor(
    private val getCurrentUser: GetCurrentUserUseCase,
    private val saveHabit: SaveHabitUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CreateEditHabitUiState())
    val state: StateFlow<CreateEditHabitUiState> = _state.asStateFlow()

    fun setName(value: String) = _state.update { it.copy(name = value.take(60)) }
    fun setIcon(emoji: String) = _state.update { it.copy(icon = emoji) }
    fun setDescription(value: String) = _state.update { it.copy(description = value.take(140)) }
    fun setColor(hex: String) = _state.update { it.copy(color = hex) }

    fun toggleDay(day: Int) {
        _state.update {
            val next = if (day in it.targetDays) it.targetDays - day else it.targetDays + day
            it.copy(targetDays = next.sorted())
        }
    }

    fun loadForEdit(habit: Habit) {
        _state.update {
            CreateEditHabitUiState(
                editingHabitId = habit.id,
                name = habit.name,
                icon = habit.icon,
                description = habit.description,
                targetDays = habit.targetDays,
                color = habit.color
            )
        }
    }

    fun save() {
        val uid = getCurrentUser() ?: run {
            _state.update { it.copy(error = "Not signed in") }
            return
        }
        val s = _state.value
        if (!s.isValid) {
            _state.update { it.copy(error = "Name and at least one day are required") }
            return
        }
        val habit = Habit(
            id = s.editingHabitId ?: "",
            userId = uid,
            name = s.name.trim(),
            icon = s.icon,
            description = s.description.trim(),
            targetDays = s.targetDays,
            reminderHour = null,
            color = s.color,
            status = "active",
            createdAt = System.currentTimeMillis()
        )
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            saveHabit(habit).fold(
                onSuccess = { _state.update { it.copy(isLoading = false, isSaved = true) } },
                onFailure = { e -> _state.update { it.copy(isLoading = false, error = e.message ?: "Save failed") } }
            )
        }
    }

    fun clearError() = _state.update { it.copy(error = null) }

    fun reset() {
        _state.value = CreateEditHabitUiState()
    }
}
