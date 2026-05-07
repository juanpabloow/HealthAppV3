package com.example.healthapp.ai.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** The JSON the model is required to produce inside the assistant message. */
@Serializable
data class DailyInsightContentDto(
    val emoji: String,
    val headline: String,
    @SerialName("supporting_text") val supportingText: String
)
