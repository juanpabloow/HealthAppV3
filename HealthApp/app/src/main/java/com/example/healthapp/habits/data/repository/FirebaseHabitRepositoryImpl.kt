package com.example.healthapp.habits.data.repository

import com.example.healthapp.habits.data.model.HabitCheckinDto
import com.example.healthapp.habits.data.model.HabitDto
import com.example.healthapp.habits.domain.model.Habit
import com.example.healthapp.habits.domain.model.HabitCheckin
import com.example.healthapp.habits.domain.repository.HabitRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseHabitRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : HabitRepository {

    private val habitsCol = firestore.collection("habits")
    private val checkinsCol = firestore.collection("habitCheckins")

    override suspend fun getHabits(userId: String): Result<List<Habit>> = try {
        val snap = habitsCol.whereEqualTo("userId", userId).get().await()
        val list = snap.documents
            .mapNotNull { it.toObject(HabitDto::class.java)?.toDomain() }
            .filter { it.status != "archived" }
            .sortedBy { it.createdAt }
        Result.success(list)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun saveHabit(habit: Habit): Result<String> = try {
        val ref = if (habit.id.isBlank()) habitsCol.document() else habitsCol.document(habit.id)
        val dto = HabitDto.fromDomain(habit).copy(id = ref.id)
        ref.set(dto).await()
        Result.success(ref.id)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun deleteHabit(habitId: String): Result<Unit> = try {
        habitsCol.document(habitId).delete().await()
        // Best-effort: also delete this habit's checkins so stats don't include orphans
        val checkins = checkinsCol.whereEqualTo("habitId", habitId).get().await()
        checkins.documents.forEach { it.reference.delete().await() }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun toggleCheckin(
        checkin: HabitCheckin,
        checked: Boolean
    ): Result<Unit> = try {
        val ref = checkinsCol.document(checkin.id)
        if (checked) {
            ref.set(HabitCheckinDto.fromDomain(checkin)).await()
        } else {
            ref.delete().await()
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getCheckins(
        userId: String,
        habitId: String,
        fromMs: Long,
        toMs: Long
    ): Result<List<HabitCheckin>> = try {
        // Two equality filters (no composite index needed) — also ensures the
        // query is provably scoped to the caller, satisfying "userId == auth.uid"
        // security rules.
        val snap = checkinsCol
            .whereEqualTo("userId", userId)
            .whereEqualTo("habitId", habitId)
            .get()
            .await()
        val list = snap.documents
            .mapNotNull { it.toObject(HabitCheckinDto::class.java)?.toDomain() }
            .filter { it.dateMs in fromMs..toMs }
            .sortedBy { it.dateMs }
        Result.success(list)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getCheckinsForDate(
        userId: String,
        date: String
    ): Result<List<HabitCheckin>> = try {
        val snap = checkinsCol
            .whereEqualTo("userId", userId)
            .whereEqualTo("date", date)
            .get()
            .await()
        val list = snap.documents
            .mapNotNull { it.toObject(HabitCheckinDto::class.java)?.toDomain() }
        Result.success(list)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getRecentCheckinsForUser(
        userId: String,
        fromMs: Long,
        toMs: Long
    ): Result<List<HabitCheckin>> = try {
        // Single equality filter + in-memory date range — no composite index
        // needed; the rule engine sees a userId-scoped query so it passes
        // even with strict per-doc rules.
        val snap = checkinsCol.whereEqualTo("userId", userId).get().await()
        val list = snap.documents
            .mapNotNull { it.toObject(HabitCheckinDto::class.java)?.toDomain() }
            .filter { it.dateMs in fromMs..toMs }
        Result.success(list)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
