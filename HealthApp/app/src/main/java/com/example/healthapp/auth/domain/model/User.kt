package com.example.healthapp.auth.domain.model

data class User(
    val uid: String,
    val email: String?,
    val phone: String?,
    val displayName: String?,
    val ageRange: String?,
    val photoUrl: String?,
    val createdAt: Long
)
