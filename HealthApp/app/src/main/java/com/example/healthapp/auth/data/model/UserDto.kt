package com.example.healthapp.auth.data.model

import com.example.healthapp.auth.domain.model.User

data class UserDto(
    val uid: String = "",
    val email: String? = null,
    val phone: String? = null,
    val displayName: String? = null,
    val ageRange: String? = null,
    val photoUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): User = User(
        uid = uid,
        email = email,
        phone = phone,
        displayName = displayName,
        ageRange = ageRange,
        photoUrl = photoUrl,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(user: User): UserDto = UserDto(
            uid = user.uid,
            email = user.email,
            phone = user.phone,
            displayName = user.displayName,
            ageRange = user.ageRange,
            photoUrl = user.photoUrl,
            createdAt = user.createdAt
        )
    }
}
