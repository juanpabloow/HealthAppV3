package com.example.healthapp.survey.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

data class ActivityOption(
    val id: String,
    val label: String,
    val emoji: String,
    val bgColor: Color
)

private val activities = listOf(
    ActivityOption("drawing", "Drawing", "🎨", Color(0xFFFFF3E0)),
    ActivityOption("cooking", "Cooking", "🍳", Color(0xFFE8F5E9)),
    ActivityOption("sports", "Sports", "⚽", Color(0xFFE3F2FD)),
    ActivityOption("music", "Music", "🎵", Color(0xFFF3E5F5)),
    ActivityOption("reading", "Reading", "📚", Color(0xFFFCE4EC)),
    ActivityOption("gaming", "Gaming", "🎮", Color(0xFFE0F7FA)),
    ActivityOption("yoga", "Yoga", "🧘", Color(0xFFF9FBE7)),
    ActivityOption("others", "Others", "✨", Color(0xFFF5F5F5))
)

@Composable
fun SurveyActivitiesScreen(
    modifier: Modifier = Modifier,
    selectedActivities: List<String>,
    onToggleActivity: (String) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    SurveyScaffold(
        modifier = modifier,
        currentStep = 3,
        onBack = onBack,
        onNext = onNext,
        nextEnabled = selectedActivities.isNotEmpty()
    ) {
        Text(
            text = "To personalize your app, just a few more questions.",
            fontSize = 12.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = buildAnnotatedString {
                append("What do you ")
                withStyle(SpanStyle(color = AppGreen, fontWeight = FontWeight.Bold)) {
                    append("enjoy")
                }
                append(" doing?")
            },
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(20.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(activities) { activity ->
                val selected = selectedActivities.contains(activity.id)
                ActivityCard(
                    option = activity,
                    selected = selected,
                    onClick = { onToggleActivity(activity.id) }
                )
            }
        }
    }
}

@Composable
private fun ActivityCard(
    option: ActivityOption,
    selected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (selected) AppGreen else Color.Transparent
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(option.bgColor)
            .border(2.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = option.emoji, fontSize = 36.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = option.label,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) AppGreen else Color(0xFF444444),
                textAlign = TextAlign.Center
            )
        }
        if (selected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(20.dp)
                    .clip(RoundedCornerShape(50))
                    .background(AppGreen),
                contentAlignment = Alignment.Center
            ) {
                Text("✓", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
