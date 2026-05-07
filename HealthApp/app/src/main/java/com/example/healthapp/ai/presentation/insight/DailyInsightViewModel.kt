package com.example.healthapp.ai.presentation.insight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthapp.ai.domain.usecase.GetDailyInsightUseCase
import com.example.healthapp.ai.domain.usecase.RegenerateDailyInsightUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DailyInsightViewModel @Inject constructor(
    private val getDailyInsight: GetDailyInsightUseCase,
    private val regenerateDailyInsight: RegenerateDailyInsightUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DailyInsightUiState())
    val state: StateFlow<DailyInsightUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun retry() = load()

    fun regenerate() {
        if (_state.value.isRegenerating) return
        viewModelScope.launch {
            _state.update { it.copy(isRegenerating = true, error = null) }
            regenerateDailyInsight().fold(
                onSuccess = { fresh ->
                    _state.update { it.copy(isRegenerating = false, insight = fresh) }
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(
                            isRegenerating = false,
                            error = e.message ?: "Couldn't regenerate"
                        )
                    }
                }
            )
        }
    }

    private fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            getDailyInsight().fold(
                onSuccess = { insight ->
                    _state.update { it.copy(isLoading = false, insight = insight) }
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Couldn't generate insight today"
                        )
                    }
                }
            )
        }
    }
}
