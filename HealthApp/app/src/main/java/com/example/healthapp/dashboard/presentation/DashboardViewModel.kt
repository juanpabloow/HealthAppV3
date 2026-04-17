package com.example.healthapp.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthapp.auth.domain.usecase.GetCurrentUserUseCase
import com.example.healthapp.dashboard.domain.repository.ScreenTimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val screenTimeRepository: ScreenTimeRepository,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardUiState())
    val state: StateFlow<DashboardUiState> = _state

    init {
        refreshPermission()
    }

    fun refreshPermission() {
        val hasPerm = screenTimeRepository.hasUsagePermission()
        _state.update { it.copy(hasUsagePermission = hasPerm) }
        if (hasPerm) loadCurrentTab()
    }

    fun selectTab(tab: ScreenTimeTab) {
        _state.update { it.copy(selectedTab = tab) }
        loadCurrentTab()
    }

    fun selectDate(dateMs: Long) {
        _state.update { it.copy(selectedDateMs = dateMs) }
        if (_state.value.selectedTab == ScreenTimeTab.DAY) loadDailyData(dateMs)
    }

    private fun loadCurrentTab() {
        when (_state.value.selectedTab) {
            ScreenTimeTab.DAY -> loadDailyData(_state.value.selectedDateMs)
            ScreenTimeTab.WEEK -> loadWeeklyData()
            ScreenTimeTab.MONTH -> { /* TODO */ }
        }
    }

    private fun loadDailyData(dateMs: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            screenTimeRepository.getDailyScreenTime(dateMs)
                .onSuccess { data ->
                    _state.update { it.copy(isLoading = false, dailyData = data) }
                    // Cache en Firestore en background
                    val uid = getCurrentUserUseCase()
                    if (uid != null) {
                        screenTimeRepository.cacheDailyData(uid, data)
                    }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    private fun loadWeeklyData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            screenTimeRepository.getWeeklyScreenTime(0)
                .onSuccess { data ->
                    _state.update { it.copy(isLoading = false, weeklyData = data) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun clearError() = _state.update { it.copy(error = null) }
}
