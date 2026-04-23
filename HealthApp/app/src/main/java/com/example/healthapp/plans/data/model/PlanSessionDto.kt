package com.example.healthapp.plans.data.model

import com.example.healthapp.plans.domain.model.PlanSession

data class PlanSessionDto(
    val id: String = "",
    val planId: String = "",
    val userId: String = "",
    val date: Long = 0L,
    val scheduledMinutes: Int = 0,
    val completed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): PlanSession = PlanSession(
        id = id,
        planId = planId,
        userId = userId,
        date = date,
        scheduledMinutes = scheduledMinutes,
        completed = completed,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(session: PlanSession): PlanSessionDto = PlanSessionDto(
            id = session.id,
            planId = session.planId,
            userId = session.userId,
            date = session.date,
            scheduledMinutes = session.scheduledMinutes,
            completed = session.completed,
            createdAt = session.createdAt
        )
    }
}
