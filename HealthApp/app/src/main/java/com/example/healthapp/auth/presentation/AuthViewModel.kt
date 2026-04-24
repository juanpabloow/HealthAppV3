package com.example.healthapp.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthapp.auth.domain.model.User
import com.example.healthapp.auth.domain.usecase.GetCurrentUserUseCase
import com.example.healthapp.auth.domain.usecase.GetUserProfileUseCase
import com.example.healthapp.auth.domain.usecase.SaveUserProfileUseCase
import com.example.healthapp.auth.domain.usecase.SignInWithEmailUseCase
import com.example.healthapp.auth.domain.usecase.SignUpWithEmailUseCase
import com.example.healthapp.auth.domain.usecase.UploadProfilePhotoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signUpWithEmailUseCase: SignUpWithEmailUseCase,
    private val signInWithEmailUseCase: SignInWithEmailUseCase,
    private val saveUserProfileUseCase: SaveUserProfileUseCase,
    private val uploadProfilePhotoUseCase: UploadProfilePhotoUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state

    init {
        val currentUid = getCurrentUserUseCase()
        if (currentUid != null) {
            _state.update { it.copy(isAuthenticated = true) }
            loadCurrentUser(currentUid)
        }
    }

    private fun loadCurrentUser(uid: String) {
        viewModelScope.launch {
            getUserProfileUseCase(uid)
                .onSuccess { user ->
                    if (user != null) _state.update { it.copy(user = user) }
                }
        }
    }

    fun setPendingEmail(email: String) {
        _state.update { it.copy(pendingEmail = email) }
    }

    fun setPendingPhone(phone: String) {
        _state.update { it.copy(pendingPhone = phone) }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            signUpWithEmailUseCase(email, password)
                .onSuccess { user ->
                    _state.update { it.copy(isLoading = false, user = user, isAuthenticated = true) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            signInWithEmailUseCase(email, password)
                .onSuccess { user ->
                    _state.update { it.copy(isLoading = false, user = user, isAuthenticated = true) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun saveProfile(displayName: String, phone: String?, ageRange: String?) {
        val currentUser = _state.value.user ?: return
        val updatedUser = currentUser.copy(
            displayName = displayName.trim(),
            phone = phone?.trim()?.ifBlank { null },
            ageRange = ageRange
        )
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            saveUserProfileUseCase(updatedUser)
                .onSuccess {
                    _state.update { it.copy(isLoading = false, user = updatedUser, isProfileUpdated = true) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun uploadProfilePhoto(imageBytes: ByteArray) {
        val uid = _state.value.user?.uid ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            uploadProfilePhotoUseCase(uid, imageBytes)
                .onSuccess { url ->
                    val updatedUser = _state.value.user?.copy(photoUrl = url)
                    if (updatedUser != null) {
                        saveUserProfileUseCase(updatedUser)
                        _state.update { it.copy(isLoading = false, user = updatedUser) }
                    } else {
                        _state.update { it.copy(isLoading = false) }
                    }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun resetProfileUpdated() {
        _state.update { it.copy(isProfileUpdated = false) }
    }
}
