package com.example.healthapp.ai.data.api

import com.example.healthapp.ai.data.dto.ChatRequest
import com.example.healthapp.ai.data.dto.ChatResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface OpenAiApi {
    @POST("v1/chat/completions")
    suspend fun chatCompletions(@Body request: ChatRequest): ChatResponse
}
