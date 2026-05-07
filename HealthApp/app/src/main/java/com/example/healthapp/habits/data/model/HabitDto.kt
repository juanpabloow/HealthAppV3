package com.example.healthapp.habits.data.model

import com.example.healthapp.habits.domain.model.Habit

data class HabitDto(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val icon: String = "🌱",
    val description: String = "",
    // Firestore stores numbers as Long — keep Long here, convert in toDomain()
    val targetDays: List<Long> = emptyList(),
    val reminderHour: Long? = null,
    val color: String = "#43A047",
    val status: String = "active",
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): Habit = Habit(
        id = id,
        userId = userId,
        name = name,
        icon = icon,
        description = description,
        targetDays = targetDays.map { it.toInt() },
        reminderHour = reminderHour?.toInt(),
        color = color,
        status = status,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(habit: Habit): HabitDto = HabitDto(
            id = habit.id,
            userId = habit.userId,
            name = habit.name,
            icon = habit.icon,
            description = habit.description,
            targetDays = habit.targetDays.map { it.toLong() },
            reminderHour = habit.reminderHour?.toLong(),
            color = habit.color,
            status = habit.status,
            createdAt = habit.createdAt
        )
    }
}
