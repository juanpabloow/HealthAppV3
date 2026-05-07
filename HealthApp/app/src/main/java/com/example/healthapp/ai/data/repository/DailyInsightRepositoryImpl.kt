package com.example.healthapp.ai.data.repository

import com.example.healthapp.BuildConfig
import com.example.healthapp.ai.data.api.OpenAiApi
import com.example.healthapp.ai.data.dto.ChatMessage
import com.example.healthapp.ai.data.dto.ChatRequest
import com.example.healthapp.ai.data.dto.DailyInsightCacheDto
import com.example.healthapp.ai.data.dto.DailyInsightContentDto
import com.example.healthapp.ai.data.dto.ResponseFormat
import com.example.healthapp.ai.domain.model.DailyInsight
import com.example.healthapp.ai.domain.model.DailyInsightContext
import com.example.healthapp.ai.domain.repository.DailyInsightRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json
import javax.inject.Inject

class DailyInsightRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val openAiApi: OpenAiApi,
    private val contextBuilder: InsightContextBuilder,
    private val json: Json
) : DailyInsightRepository {

    private fun cacheRef(uid: String, date: String) =
        firestore.collection("users").document(uid)
            .collection("aiInsights").document(date)

    override suspend fun getCached(uid: String, date: String): Result<DailyInsight?> = try {
        val snap = cacheRef(uid, date).get().await()
        val cached = snap.toObject(DailyInsightCacheDto::class.java)?.toDomain()
        Result.success(cached)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun generate(
        uid: String,
        context: DailyInsightContext
    ): Result<DailyInsight> {
        return try {
            if (BuildConfig.OPENAI_API_KEY.isBlank()) {
                return Result.failure(IllegalStateException("OpenAI API key not configured"))
            }

            val req = ChatRequest(
                model = MODEL,
                messages = listOf(
                    ChatMessage(role = "system", content = SYSTEM_PROMPT),
                    ChatMessage(role = "user", content = contextBuilder.build(context))
                ),
                temperature = 0.6,
                maxTokens = 200,
                responseFormat = ResponseFormat(type = "json_object")
            )

            val response = openAiApi.chatCompletions(req)
            val rawJson = response.choices.firstOrNull()?.message?.content
                ?: return Result.failure(IllegalStateException("Empty response from model"))

            val parsed = json.decodeFromString<DailyInsightContentDto>(rawJson)
            val now = System.currentTimeMillis()
            val insight = DailyInsight(
                date = context.today,
                emoji = parsed.emoji,
                headline = parsed.headline,
                supportingText = parsed.supportingText,
                generatedAt = now,
                modelUsed = MODEL
            )

            // Cache to Firestore (overwrites any existing doc for today — idempotent upsert)
            cacheRef(uid, context.today).set(DailyInsightCacheDto.fromDomain(insight)).await()

            Result.success(insight)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private companion object {
        const val MODEL = "gpt-4o-mini"

        val SYSTEM_PROMPT = """
You are a concise wellness assistant for HealthApp, a digital wellness app that tracks screen time, mood, daily habits, and focus sessions. You generate ONE short personalized daily insight for the user.

Output STRICTLY as a JSON object with this exact shape and nothing else:
{
  "emoji": "🟢" | "🟡" | "🔴",
  "headline": "<max 15 words>",
  "supporting_text": "<max 25 words>"
}

Emoji rules:
- 🟢 positive — things are going well or improving
- 🟡 neutral — balanced day with one signal worth noticing
- 🔴 needs attention — clear pattern that calls for a small course-correct
Pick exactly one based on the overall picture, not any single number.

Headline rules:
- Max 15 words.
- Combine the user's mood (if logged) with one quantitative observation about screen time, habits, or focus sessions. Cite a specific number when you have one ("22% below", "47 minutes", "4-day streak").
- Specific over general. Reference the actual day name when natural ("Calm Tuesday", "Tough start to the week").
- Never use clinical language ("anxiety", "depression", "disorder").
- Never use shame language ("too much", "should", "wasting").

Supporting text rules:
- Max 25 words.
- One concrete, actionable nudge tied to an existing habit, focus session, or stated goal of the user.
- Reference the user's wellness goals (from their onboarding survey) when relevant.
- End with encouragement, not a command. Use "want to / try" not "you must / you need to".

If the input data is too sparse to find a real pattern (no mood logged this week AND no screen time data AND no habits), return a friendly setup nudge in the same JSON shape, with emoji "🟡".

Never invent data. Only mention things present in the input. If a field is absent, do not reference it.
""".trimIndent()
    }
}
