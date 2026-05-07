package com.example.healthapp.ai.presentation.insight

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.healthapp.ai.domain.model.DailyInsight
import com.example.healthapp.ui.theme.AppGreen
import com.example.healthapp.ui.theme.Poppins

@Composable
fun DailyInsightCard(
    modifier: Modifier = Modifier,
    viewModel: DailyInsightViewModel = hiltViewModel(),
    onDismissed: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    DailyInsightCardContent(
        state = state,
        modifier = modifier,
        onRegenerate = viewModel::regenerate,
        onRetry = viewModel::retry,
        onAskMore = { /* TODO wire to coach feature */ },
        onDismissed = onDismissed
    )
}

@Composable
private fun DailyInsightCardContent(
    state: DailyInsightUiState,
    modifier: Modifier = Modifier,
    onRegenerate: () -> Unit,
    onRetry: () -> Unit,
    onAskMore: () -> Unit,
    onDismissed: () -> Unit
) {
    when {
        state.insight != null -> Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            InsightBody(
                insight = state.insight,
                isRegenerating = state.isRegenerating,
                onRegenerate = onRegenerate,
                onAskMore = onAskMore
            )
        }

        state.error != null -> ErrorBubble(
            modifier = modifier,
            message = state.error,
            onRetry = onRetry,
            onDismissed = onDismissed
        )

        else -> Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            LoadingBody()
        }
    }
}

// ── Error: tiny floating bubble ───────────────────────────────────────────────

@Composable
private fun ErrorBubble(
    modifier: Modifier = Modifier,
    message: String,
    onRetry: () -> Unit,
    onDismissed: () -> Unit = {}
) {
    var showDialog by remember { mutableStateOf(false) }

    // Small badge aligned to the right — takes almost no vertical space
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(end = 4.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .shadow(3.dp, CircleShape, clip = false)
                .clip(CircleShape)
                .background(Color(0xFFFFF8E1))   // very soft amber — not alarming at all
                .clickable { showDialog = true },
            contentAlignment = Alignment.Center
        ) {
            Text("⚠️", fontSize = 15.sp)
        }
    }

    // Full error detail dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White,
            title = {
                Text(
                    text = "Insight unavailable",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Poppins,
                    color = Color(0xFF1A1A1A)
                )
            },
            text = {
                Text(
                    text = message,
                    fontSize = 13.sp,
                    fontFamily = Poppins,
                    color = Color.Gray,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { onRetry(); showDialog = false }) {
                    Text(
                        "Retry",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        color = AppGreen
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false; onDismissed() }) {
                    Text(
                        "Dismiss",
                        fontFamily = Poppins,
                        color = Color.Gray
                    )
                }
            }
        )
    }
}

// ── Insight body ──────────────────────────────────────────────────────────────

@Composable
private fun InsightBody(
    insight: DailyInsight,
    isRegenerating: Boolean,
    onRegenerate: () -> Unit,
    onAskMore: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier.size(width = 44.dp, height = 44.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = insight.emoji.ifBlank { "🟡" }, fontSize = 28.sp)
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
            Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                if (isRegenerating) {
                    CircularProgressIndicator(
                        color = AppGreen,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    IconButton(onClick = onRegenerate, modifier = Modifier.size(40.dp)) {
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
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(
                onClick = onAskMore,
                enabled = false,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
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

// ── Loading body ──────────────────────────────────────────────────────────────

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
