package com.example.healthapp.emotion.domain.model

enum class Emotion(val displayName: String, val emoji: String) {
    HAPPY("Happy", "😊"),
    SAD("Sad", "😢"),
    ANXIOUS("Anxious", "😰"),
    ANGRY("Angry", "😠"),
    CALM("Calm", "😌"),
    TIRED("Tired", "😴"),
    EXCITED("Excited", "🤩");

    companion object {
        fun fromName(name: String?): Emotion? =
            name?.let { runCatching { valueOf(it) }.getOrNull() }
    }
}
