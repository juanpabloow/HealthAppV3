package com.example.healthapp.survey.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthapp.auth.domain.usecase.GetCurrentUserUseCase
import com.example.healthapp.survey.domain.model.SurveyResponse
import com.example.healthapp.survey.domain.usecase.SaveSurveyResponseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SurveyViewModel @Inject constructor(
    private val saveSurveyResponseUseCase: SaveSurveyResponseUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SurveyUiState())
    val state: StateFlow<SurveyUiState> = _state

    fun toggleGoal(goal: String) {
        _state.update { s ->
            val updated = if (s.selectedGoals.contains(goal))
                s.selectedGoals - goal
            else
                s.selectedGoals + goal
            s.copy(selectedGoals = updated)
        }
    }

    fun toggleWorry(worry: String) {
        _state.update { s ->
            val updated = if (s.selectedWorries.contains(worry))
                s.selectedWorries - worry
            else if (s.selectedWorries.size < 3)
                s.selectedWorries + worry
            else
                s.selectedWorries
            s.copy(selectedWorries = updated)
        }
    }

    fun toggleActivity(activity: String) {
        _state.update { s ->
            val updated = if (s.selectedActivities.contains(activity))
                s.selectedActivities - activity
            else
                s.selectedActivities + activity
            s.copy(selectedActivities = updated)
        }
    }

    fun selectMood(mood: String) {
        _state.update { it.copy(selectedMood = mood) }
    }

    fun submitSurvey() {
        val s = _state.value
        val uid = getCurrentUserUseCase()

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            if (uid != null) {
                val response = SurveyResponse(
                    userId = uid,
                    primaryGoal = s.selectedGoals.joinToString(","),
                    keepingFactors = s.selectedWorries,
                    enjoyedActivities = s.selectedActivities,
                    todayMood = s.selectedMood,
                    completedAt = System.currentTimeMillis()
                )
                saveSurveyResponseUseCase(response)
            }

            // Always advance — don't block the user if save fails or uid is missing
            _state.update { it.copy(isLoading = false, isCompleted = true) }
        }
    }

    fun clearError() = _state.update { it.copy(error = null) }
}
