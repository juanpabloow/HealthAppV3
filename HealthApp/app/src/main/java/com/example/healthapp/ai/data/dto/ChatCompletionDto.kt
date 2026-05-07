package com.example.healthapp.ai.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val temperature: Double = 0.6,
    @SerialName("max_tokens") val maxTokens: Int = 200,
    @SerialName("response_format") val responseFormat: ResponseFormat? = null
)

@Serializable
data class ChatMessage(
    val role: String,        // "system" | "user" | "assistant"
    val content: String
)

@Serializable
data class ResponseFormat(val type: String) // "json_object"

@Serializable
data class ChatResponse(
    val choices: List<Choice> = emptyList(),
    val usage: Usage? = null
)

@Serializable
data class Choice(
    val message: ChatMessage,
    @SerialName("finish_reason") val finishReason: String? = null
)

@Serializable
data class Usage(
    @SerialName("prompt_tokens") val promptTokens: Int = 0,
    @SerialName("completion_tokens") val completionTokens: Int = 0,
    @SerialName("total_tokens") val totalTokens: Int = 0
)
