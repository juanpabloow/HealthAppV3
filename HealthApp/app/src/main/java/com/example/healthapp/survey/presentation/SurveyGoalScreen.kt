package com.example.healthapp.survey.presentation

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthapp.survey.presentation.components.SurveyScaffold
import com.example.healthapp.ui.theme.AppGreen

private val goals = listOf(
    "Improve my screen time and improve productivity",
    "Build healthier digital habits",
    "Minimize distractions and boost productivity",
    "Stay focused on my studies and avoid distractions",
    "Make more time for my hobbies and personal projects",
    "Improve my self-control and build better screen habits"
)

@Composable
fun SurveyGoalScreen(
    modifier: Modifier = Modifier,
    selectedGoals: List<String>,
    onToggleGoal: (String) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    SurveyScaffold(
        modifier = modifier,
        currentStep = 1,
        onBack = onBack,
        onNext = onNext,
        nextEnabled = selectedGoals.isNotEmpty()
    ) {
        Text(
            text = "We're excited to have you on board, just a few more questions to go!",
            fontSize = 12.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = buildAnnotatedString {
                append("What's your primary goal with\n")
                withStyle(SpanStyle(color = AppGreen, fontWeight = FontWeight.Bold)) {
                    append("HealApp")
                }
                append("?")
            },
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 28.sp
        )
        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(goals) { goal ->
                val selected = selectedGoals.contains(goal)
                GoalCard(
                    text = goal,
                    selected = selected,
                    onClick = { onToggleGoal(goal) }
                )
            }
        }
    }
}

@Composable
private fun GoalCard(text: String, selected: Boolean, onClick: () -> Unit) {
    val borderColor = if (selected) AppGreen else Color(0xFFE0E0E0)
    val bgColor = if (selected) AppGreen.copy(alpha = 0.08f) else Color.White

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = bgColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (selected) AppGreen else Color(0xFF333333),
                modifier = Modifier.weight(1f)
            )
            if (selected) {
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(50),
                    color = AppGreen
                ) {
                    Text(
                        text = "✓",
                        color = Color.White,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
        }
    }
}
