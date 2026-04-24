package com.example.healthapp.auth.presentation

import com.example.healthapp.auth.domain.model.User

data class AuthUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val isAuthenticated: Boolean = false,
    val pendingEmail: String = "",
    val pendingPhone: String = "",
    val isProfileUpdated: Boolean = false
)
