package com.example.healthapp.ai.presentation.insight

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.healthapp.ai.domain.model.DailyInsight
import com.example.healthapp.ui.theme.AppGreen
import com.example.healthapp.ui.theme.Poppins

private val ErrorRed = Color(0xFFE53935)

@Composable
fun DailyInsightCard(
    modifier: Modifier = Modifier,
    viewModel: DailyInsightViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    DailyInsightCardContent(
        state = state,
        modifier = modifier,
        onRegenerate = viewModel::regenerate,
        onRetry = viewModel::retry,
        onAskMore = { /* TODO wire to coach feature */ }
    )
}

@Composable
private fun DailyInsightCardContent(
    state: DailyInsightUiState,
    modifier: Modifier = Modifier,
    onRegenerate: () -> Unit,
    onRetry: () -> Unit,
    onAskMore: () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        when {
            state.insight != null -> InsightBody(
                insight = state.insight,
                isRegenerating = state.isRegenerating,
                onRegenerate = onRegenerate,
                onAskMore = onAskMore
            )
            state.error != null -> ErrorBody(
                message = state.error,
                onRetry = onRetry
            )
            else -> LoadingBody()
        }
    }
}

@Composable
private fun InsightBody(
    insight: DailyInsight,
    isRegenerating: Boolean,
    onRegenerate: () -> Unit,
    onAskMore: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.Top) {
            // Emoji column — fixed width so headline aligns regardless of glyph width
            Box(
                modifier = Modifier
                    .size(width = 44.dp, height = 44.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = insight.emoji.ifBlank { "🟡" },
                    fontSize = 28.sp
                )
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = insight.headline,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Poppins,
                    color = Color(0xFF1A1A1A),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )
                if (insight.supportingText.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = insight.supportingText,
                        fontSize = 12.sp,
                        fontFamily = Poppins,
                        color = Color.Gray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 16.sp
                    )
                }
            }
            Spacer(Modifier.width(4.dp))
            // Refresh affordance — replaced by a tiny spinner while regenerating
            Box(
                modifier = Modifier.size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isRegenerating) {
                    CircularProgressIndicator(
                        color = AppGreen,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    IconButton(
                        onClick = onRegenerate,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh insight",
                            tint = AppGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(2.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = onAskMore,
                enabled = false,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = 8.dp,
                    vertical = 0.dp
                )
            ) {
                Text(
                    text = "Ask more →",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = Poppins,
                    color = AppGreen
                )
            }
        }
    }
}

@Composable
private fun LoadingBody() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 80.dp)
            .padding(horizontal = 14.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(
            color = AppGreen,
            strokeWidth = 2.dp,
            modifier = Modifier.size(22.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Brewing today's insight…",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Poppins,
                color = Color(0xFF1A1A1A)
            )
            Text(
                text = "Combining your mood, screen time, and habits.",
                fontSize = 11.sp,
                fontFamily = Poppins,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun ErrorBody(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(50))
                    .background(ErrorRed.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Text("⚠️", fontSize = 18.sp)
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Couldn't generate insight today",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Poppins,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    text = message.take(60),
                    fontSize = 11.sp,
                    fontFamily = Poppins,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            TextButton(onClick = onRetry) {
                Text(
                    text = "Retry",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Poppins,
                    color = AppGreen
                )
            }
        }
    }
}
