package com.example.healthapp.survey.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment

private val worries = listOf(
    "Brain Rot", "Doom Scrolling",
    "Stress", "Productivity",
    "Poor Self Control", "I need to study",
    "Anxiety", "Social Media",
    "Gaming", "Procrastination"
)

@Composable
fun SurveyWorriesScreen(
    modifier: Modifier = Modifier,
    selectedWorries: List<String>,
    onToggleWorry: (String) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    SurveyScaffold(
        modifier = modifier,
        currentStep = 2,
        onBack = onBack,
        onNext = onNext,
        nextEnabled = selectedWorries.isNotEmpty()
    ) {
        Text(
            text = "Tell us why you're reaching out today.",
            fontSize = 12.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = buildAnnotatedString {
                append("What is ")
                withStyle(SpanStyle(color = AppGreen, fontWeight = FontWeight.Bold)) {
                    append("worrying")
                }
                append(" you?")
            },
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Select up to 3 options",
            fontSize = 12.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Grid de chips en filas de 2
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            val rows = worries.chunked(2)
            items(rows) { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    row.forEach { worry ->
                        val selected = selectedWorries.contains(worry)
                        val maxReached = selectedWorries.size >= 3 && !selected
                        WorryChip(
                            text = worry,
                            selected = selected,
                            dimmed = maxReached,
                            onClick = { if (!maxReached || selected) onToggleWorry(worry) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun WorryChip(
    text: String,
    selected: Boolean,
    dimmed: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = when {
        selected -> AppGreen
        dimmed -> Color(0xFFE0E0E0)
        else -> Color(0xFFCCCCCC)
    }
    val bgColor = when {
        selected -> AppGreen.copy(alpha = 0.1f)
        else -> Color.White
    }
    val textColor = when {
        selected -> AppGreen
        dimmed -> Color(0xFFBBBBBB)
        else -> Color(0xFF444444)
    }

    Surface(
        modifier = modifier
            .border(1.5.dp, borderColor, RoundedCornerShape(50.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(50.dp),
        color = bgColor
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Text(
                text = text,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                color = textColor
            )
        }
    }
}
