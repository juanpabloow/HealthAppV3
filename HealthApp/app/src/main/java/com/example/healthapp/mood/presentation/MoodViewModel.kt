package com.example.healthapp.mood.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthapp.auth.domain.usecase.GetCurrentUserUseCase
import com.example.healthapp.mood.domain.model.MoodEntry
import com.example.healthapp.mood.domain.usecase.GetMoodEntriesUseCase
import com.example.healthapp.mood.domain.usecase.GetTodayMoodEntryUseCase
import com.example.healthapp.mood.domain.usecase.SaveMoodEntryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class MoodViewModel @Inject constructor(
    private val saveMoodEntryUseCase: SaveMoodEntryUseCase,
    private val getMoodEntriesUseCase: GetMoodEntriesUseCase,
    private val getTodayMoodEntryUseCase: GetTodayMoodEntryUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MoodUiState())
    val state: StateFlow<MoodUiState> = _state

    private val userId: String get() = getCurrentUserUseCase() ?: ""

    init {
        load()
    }

    fun load() {
        val uid = userId
        if (uid.isBlank()) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val today = startOfDay()
            getTodayMoodEntryUseCase(uid, today)
                .onSuccess { todayEntry ->
                    _state.update { s ->
                        s.copy(
                            todayEntry = todayEntry,
                            selectedMood = todayEntry?.mood ?: "",
                            note = todayEntry?.note ?: ""
                        )
                    }
                }
            getMoodEntriesUseCase(uid)
                .onSuccess { entries ->
                    _state.update { it.copy(entries = entries, isLoading = false) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun selectMood(mood: String) = _state.update { it.copy(selectedMood = mood) }

    fun setNote(note: String) = _state.update { it.copy(note = note) }

    fun saveEntry() {
        val uid = userId
        if (uid.isBlank()) return
        val s = _state.value
        if (s.selectedMood.isBlank()) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val today = startOfDay()
            val entry = MoodEntry(
                userId = uid,
                mood = s.selectedMood,
                note = s.note.trim(),
                date = today
            )
            saveMoodEntryUseCase(entry)
                .onSuccess {
                    getMoodEntriesUseCase(uid).onSuccess { entries ->
                        _state.update { it.copy(
                            todayEntry = entry,
                            entries = entries,
                            isLoading = false,
                            isSaved = true
                        ) }
                    }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun clearSaved() = _state.update { it.copy(isSaved = false) }
    fun clearError() = _state.update { it.copy(error = null) }

    private fun startOfDay(epochMs: Long = System.currentTimeMillis()): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = epochMs
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
