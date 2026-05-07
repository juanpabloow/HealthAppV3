package com.example.healthapp.habits.data.model

import com.example.healthapp.habits.domain.model.HabitCheckin

data class HabitCheckinDto(
    val id: String = "",
    val habitId: String = "",
    val userId: String = "",
    val date: String = "",
    val dateMs: Long = 0L,
    val note: String? = null,
    val createdAt: Long = 0L
) {
    fun toDomain(): HabitCheckin = HabitCheckin(
        id = id,
        habitId = habitId,
        userId = userId,
        date = date,
        dateMs = dateMs,
        note = note?.takeIf { it.isNotBlank() },
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(c: HabitCheckin): HabitCheckinDto = HabitCheckinDto(
            id = c.id,
            habitId = c.habitId,
            userId = c.userId,
            date = c.date,
            dateMs = c.dateMs,
            note = c.note,
            createdAt = c.createdAt
        )
    }
}
