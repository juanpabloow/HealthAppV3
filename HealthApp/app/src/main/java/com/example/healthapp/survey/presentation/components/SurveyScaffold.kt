package com.example.healthapp.survey.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthapp.ui.theme.AppGreen

@Composable
fun SurveyScaffold(
    modifier: Modifier = Modifier,
    currentStep: Int,           // 1..4
    totalSteps: Int = 4,
    onBack: () -> Unit,
    onNext: () -> Unit,
    nextEnabled: Boolean = true,
    nextLabel: String = "Next →",
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Progress bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(totalSteps) { index ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (index < currentStep) AppGreen else Color(0xFFE0E0E0)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Screen content
        Column(modifier = Modifier.weight(1f)) {
            content()
        }

        // Navigation buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onBack) {
                Text("← Back", color = Color.Gray, fontSize = 14.sp)
            }
            Button(
                onClick = onNext,
                enabled = nextEnabled,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppGreen),
                contentPadding = PaddingValues(horizontal = 28.dp, vertical = 12.dp)
            ) {
                Text(nextLabel, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}
