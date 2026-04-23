package com.example.healthapp.plans.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthapp.auth.domain.usecase.GetCurrentUserUseCase
import com.example.healthapp.plans.domain.model.Plan
import com.example.healthapp.plans.domain.usecase.SavePlanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatePlanViewModel @Inject constructor(
    private val savePlanUseCase: SavePlanUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CreatePlanUiState())
    val state: StateFlow<CreatePlanUiState> = _state

    fun setName(v: String) = _state.update { it.copy(name = v) }
    fun setIcon(v: String) = _state.update { it.copy(icon = v) }
    fun setDescription(v: String) = _state.update { it.copy(description = v) }

    fun toggleBlockedApp(packageName: String) = _state.update { s ->
        val updated = if (s.blockedApps.contains(packageName))
            s.blockedApps - packageName else s.blockedApps + packageName
        s.copy(blockedApps = updated)
    }

    fun toggleDay(day: Int) = _state.update { s ->
        val updated = if (s.scheduleDays.contains(day))
            s.scheduleDays - day else s.scheduleDays + day
        s.copy(scheduleDays = updated)
    }

    fun setStartHour(h: Int) = _state.update { it.copy(startHour = h) }
    fun setEndHour(h: Int) = _state.update { it.copy(endHour = h) }
    fun setStrictness(v: String) = _state.update { it.copy(strictness = v) }

    fun nextStep() = _state.update { it.copy(step = it.step + 1) }
    fun prevStep() = _state.update { it.copy(step = (it.step - 1).coerceAtLeast(1)) }

    fun loadForEdit(plan: Plan) = _state.update {
        CreatePlanUiState(
            editingPlanId = plan.id,
            name = plan.name,
            icon = plan.icon,
            description = plan.description,
            blockedApps = plan.blockedApps,
            scheduleDays = plan.scheduleDays,
            startHour = plan.startHour,
            endHour = plan.endHour,
            strictness = plan.strictness
        )
    }

    fun save() {
        val s = _state.value
        val uid = getCurrentUserUseCase() ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val plan = Plan(
                id = s.editingPlanId ?: "",
                userId = uid,
                name = s.name.trim(),
                icon = s.icon,
                description = s.description.trim(),
                blockedApps = s.blockedApps,
                scheduleDays = s.scheduleDays,
                startHour = s.startHour,
                endHour = s.endHour,
                strictness = s.strictness
            )
            savePlanUseCase(plan)
                .onSuccess { _state.update { it.copy(isLoading = false, isSaved = true) } }
                .onFailure { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
        }
    }

    fun clearError() = _state.update { it.copy(error = null) }
    fun reset() = _state.update { CreatePlanUiState() }
}
