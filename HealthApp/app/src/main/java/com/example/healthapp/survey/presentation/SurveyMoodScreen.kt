package com.example.healthapp.survey.presentation

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthapp.survey.presentation.components.SurveyScaffold
import com.example.healthapp.ui.theme.AppGreen

data class MoodOption(val id: String, val label: String, val emoji: String)

private val moods = listOf(
    MoodOption("anxious", "Anxious", "😰"),
    MoodOption("calm", "Calm", "😊"),
    MoodOption("sad", "Sad", "😢"),
    MoodOption("happy", "Happy", "😄"),
    MoodOption("excited", "Excited", "🤩")
)

@Composable
fun SurveyMoodScreen(
    modifier: Modifier = Modifier,
    selectedMood: String,
    onSelectMood: (String) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit,
    isLoading: Boolean = false
) {
    SurveyScaffold(
        modifier = modifier,
        currentStep = 4,
        onBack = onBack,
        onNext = onNext,
        nextEnabled = selectedMood.isNotEmpty() && !isLoading,
        nextLabel = if (isLoading) "Saving..." else "Finish ✓"
    ) {
        Text(
            text = "Before we dive in, let's get to know you better.",
            fontSize = 12.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = buildAnnotatedString {
                append("How do you feel ")
                withStyle(SpanStyle(color = AppGreen, fontWeight = FontWeight.Bold)) {
                    append("Today?")
                }
            },
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(40.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            moods.forEach { mood ->
                MoodItem(
                    option = mood,
                    selected = selectedMood == mood.id,
                    onClick = { onSelectMood(mood.id) }
                )
            }
        }

        if (isLoading) {
            Spacer(modifier = Modifier.height(32.dp))
            CircularProgressIndicator(
                color = AppGreen,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun MoodItem(option: MoodOption, selected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .border(
                width = if (selected) 2.dp else 0.dp,
                color = if (selected) AppGreen else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Text(
            text = option.emoji,
            fontSize = 40.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = option.label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) AppGreen else Color(0xFF888888),
            textAlign = TextAlign.Center
        )
    }
}
