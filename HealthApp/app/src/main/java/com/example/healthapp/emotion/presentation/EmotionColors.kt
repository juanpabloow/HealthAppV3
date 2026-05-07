package com.example.healthapp.emotion.presentation

import androidx.compose.ui.graphics.Color
import com.example.healthapp.emotion.domain.model.Emotion
import com.example.healthapp.ui.theme.AppGreen

fun Emotion.tint(): Color = when (this) {
    Emotion.HAPPY    -> Color(0xFFFFB300)   // amber
    Emotion.SAD      -> Color(0xFF1E88E5)   // blue (palette)
    Emotion.ANXIOUS  -> Color(0xFF8E24AA)   // purple (palette)
    Emotion.ANGRY    -> Color(0xFFE53935)   // red (palette)
    Emotion.CALM     -> AppGreen
    Emotion.TIRED    -> Color(0xFF6D4C41)   // brown (palette)
    Emotion.EXCITED  -> Color(0xFFE91E63)   // pink
}
