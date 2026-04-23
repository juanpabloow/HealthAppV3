package com.example.healthapp.plans.data.model

import com.example.healthapp.plans.domain.model.Plan

data class PlanDto(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val icon: String = "📋",
    val description: String = "",
    val blockedApps: List<String> = emptyList(),
    // Firestore stores array numbers as Long — keep Long here, convert in toDomain()
    val scheduleDays: List<Long> = emptyList(),
    val startHour: Long = 9L,
    val endHour: Long = 17L,
    val strictness: String = "medium",
    val status: String = "active",
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): Plan = Plan(
        id = id,
        userId = userId,
        name = name,
        icon = icon,
        description = description,
        blockedApps = blockedApps,
        scheduleDays = scheduleDays.map { it.toInt() },
        startHour = startHour.toInt(),
        endHour = endHour.toInt(),
        strictness = strictness,
        status = status,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(plan: Plan): PlanDto = PlanDto(
            id = plan.id,
            userId = plan.userId,
            name = plan.name,
            icon = plan.icon,
            description = plan.description,
            blockedApps = plan.blockedApps,
            scheduleDays = plan.scheduleDays.map { it.toLong() },
            startHour = plan.startHour.toLong(),
            endHour = plan.endHour.toLong(),
            strictness = plan.strictness,
            status = plan.status,
            createdAt = plan.createdAt
        )
    }
}
