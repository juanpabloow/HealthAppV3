package com.example.healthapp.ai.presentation.insight

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.healthapp.ai.domain.model.DailyInsight
import com.example.healthapp.ui.theme.AppGreen
import com.example.healthapp.ui.theme.Poppins
import kotlinx.coroutines.delay

// ── Tone palette (driven by the AI's 🟢 / 🟡 / 🔴 emoji) ─────────────────────

private val ToneGreen   = Color(0xFF4CAF50) // AppGreen — positive
private val ToneAmber   = Color(0xFFFFB300) // neutral / setup nudge / warning
private val ToneSoftRed = Color(0xFFEF6E6C) // attention (softened from #E53935)

private fun toneFor(emoji: String?): Color = when (emoji) {
    "🟢" -> ToneGreen
    "🟡" -> ToneAmber
    "🔴" -> ToneSoftRed
    else -> ToneGreen
}

// ── Public entry point ──────────────────────────────────────────────────────

/**
 * Floating 64dp bubble that lives at the bottom-left corner of the dashboard.
 * Tap to expand into a [DailyInsightSheet] (Material3 ModalBottomSheet) that
 * shows the full insight without truncation.
 *
 * The bubble pulses for ~6 seconds the first time today's insight becomes
 * available, then settles. Tapping immediately stops the pulse and removes
 * the unread badge dot. "Read" state is in-memory and resets on each app
 * launch — that's intentional, so the user gets a quick visual cue the next
 * morning when a fresh insight arrives.
 */
@Composable
fun DailyInsightBubble(
    modifier: Modifier = Modifier,
    viewModel: DailyInsightViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var sheetVisible by remember { mutableStateOf(false) }
    var hasReadThisSession by remember { mutableStateOf(false) }
    var pulseActive by remember { mutableStateOf(false) }

    // Pulse for 6s after a fresh insight, then auto-stop. Re-runs if a new
    // insight arrives (e.g. after refresh) and the user hasn't seen it yet.
    LaunchedEffect(state.insight, hasReadThisSession) {
        pulseActive = state.insight != null && !hasReadThisSession
        if (pulseActive) {
            delay(6_000)
            pulseActive = false
        }
    }

    Bubble(
        state = state,
        pulsing = pulseActive,
        unread = !hasReadThisSession && state.insight != null && state.error == null,
        modifier = modifier,
        onClick = {
            hasReadThisSession = true
            sheetVisible = true
        }
    )

    if (sheetVisible) {
        DailyInsightSheet(
            state = state,
            onRegenerate = viewModel::regenerate,
            onRetry = viewModel::retry,
            onAskMore = { /* TODO wire to coach feature */ },
            onDismiss = { sheetVisible = false }
        )
    }
}

// ── Bubble ─────────────────────────────────────────────────────────────────

@Composable
private fun Bubble(
    state: DailyInsightUiState,
    pulsing: Boolean,
    unread: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "pulse")
    val scale by transition.animateFloat(
        initialValue = 1f,
        targetValue = if (pulsing) 1.06f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val effectiveScale = if (pulsing) scale else 1f

    val toneKey = when {
        state.error != null -> ToneAmber
        state.insight != null -> toneFor(state.insight.emoji)
        else -> ToneGreen
    }

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .scale(effectiveScale)
                .shadow(
                    elevation = 6.dp,
                    shape = CircleShape,
                    clip = false,
                    spotColor = toneKey.copy(alpha = 0.40f),
                    ambientColor = toneKey.copy(alpha = 0.20f)
                )
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White,
                            toneKey.copy(alpha = 0.18f)
                        )
                    )
                )
                .border(1.dp, toneKey.copy(alpha = 0.30f), CircleShape)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            BubbleContent(state = state, toneKey = toneKey)
        }

        // Unread badge dot — half-overlapping the bubble's top-right
        if (unread) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .align(Alignment.TopEnd)
                    .scale(effectiveScale)
                    .clip(CircleShape)
                    .background(AppGreen)
                    .border(1.5.dp, Color.White, CircleShape)
            )
        }
    }
}

@Composable
private fun BubbleContent(state: DailyInsightUiState, toneKey: Color) {
    when {
        state.error != null -> Text("⚠️", fontSize = 24.sp)
        state.insight != null -> Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = "AI insight",
            tint = toneKey,
            modifier = Modifier.size(28.dp)
        )
        else -> CircularProgressIndicator(
            color = toneKey,
            strokeWidth = 1.5.dp,
            modifier = Modifier.size(20.dp)
        )
    }
}

// ── Modal bottom sheet (the popup) ─────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DailyInsightSheet(
    state: DailyInsightUiState,
    onRegenerate: () -> Unit,
    onRetry: () -> Unit,
    onAskMore: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        when {
            state.error != null -> ErrorSheetBody(
                message = state.error,
                onRetry = { onRetry(); onDismiss() },
                onClose = onDismiss
            )
            state.insight != null -> InsightSheetBody(
                insight = state.insight,
                isRegenerating = state.isRegenerating,
                onRegenerate = onRegenerate,
                onAskMore = onAskMore,
                onClose = onDismiss
            )
            else -> LoadingSheetBody(onClose = onDismiss)
        }
    }
}

@Composable
private fun InsightSheetBody(
    insight: DailyInsight,
    isRegenerating: Boolean,
    onRegenerate: () -> Unit,
    onAskMore: () -> Unit,
    onClose: () -> Unit
) {
    val toneKey = toneFor(insight.emoji)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 4.dp, bottom = 28.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            EmojiHalo(emoji = insight.emoji, toneKey = toneKey, size = 64.dp, emojiSize = 30.sp)
            Spacer(Modifier.weight(1f))
            CloseIconButton(onClick = onClose)
        }
        Spacer(Modifier.height(14.dp))
        InsightChip()
        Spacer(Modifier.height(8.dp))
        Text(
            text = insight.headline,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Poppins,
            color = Color(0xFF1A1A1A),
            lineHeight = 24.sp
        )
        if (insight.supportingText.isNotBlank()) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = insight.supportingText,
                fontSize = 14.sp,
                fontFamily = Poppins,
                color = Color(0xFF555555),
                lineHeight = 20.sp
            )
        }
        Spacer(Modifier.height(20.dp))
        ThinDivider()
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RefreshIconButton(isRegenerating = isRegenerating, onRegenerate = onRegenerate)
            AskMorePill(onClick = onAskMore, enabled = false)
        }
    }
}

@Composable
private fun LoadingSheetBody(onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 4.dp, bottom = 28.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(Modifier.weight(1f))
            CloseIconButton(onClick = onClose)
        }
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = AppGreen,
                strokeWidth = 2.dp,
                modifier = Modifier.size(36.dp)
            )
            Spacer(Modifier.height(14.dp))
            Text(
                text = "Brewing today's insight…",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Poppins,
                color = Color(0xFF1A1A1A)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Combining your mood, screen time, and habits.",
                fontSize = 12.sp,
                fontFamily = Poppins,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun ErrorSheetBody(
    message: String,
    onRetry: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 4.dp, bottom = 28.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFF8E1)),
                contentAlignment = Alignment.Center
            ) {
                Text("⚠️", fontSize = 30.sp)
            }
            Spacer(Modifier.weight(1f))
            CloseIconButton(onClick = onClose)
        }
        Spacer(Modifier.height(14.dp))
        Text(
            text = "Insight unavailable",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Poppins,
            color = Color(0xFF1A1A1A)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = message,
            fontSize = 14.sp,
            fontFamily = Poppins,
            color = Color(0xFF555555),
            lineHeight = 20.sp
        )
        Spacer(Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .align(Alignment.End)
                .clip(RoundedCornerShape(50))
                .background(AppGreen)
                .clickable { onRetry() }
                .padding(horizontal = 22.dp, vertical = 10.dp)
        ) {
            Text(
                text = "Retry",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Poppins,
                color = Color.White
            )
        }
    }
}

// ── Shared building blocks ─────────────────────────────────────────────────

@Composable
private fun EmojiHalo(
    emoji: String,
    toneKey: Color,
    size: Dp = 52.dp,
    emojiSize: TextUnit = 22.sp
) {
    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            toneKey.copy(alpha = 0.32f),
                            toneKey.copy(alpha = 0.06f)
                        )
                    )
                )
        )
        Box(
            modifier = Modifier
                .size(size * 0.7f)
                .clip(CircleShape)
                .background(toneKey.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji.ifBlank { "🟡" }, fontSize = emojiSize)
        }
    }
}

@Composable
private fun InsightChip() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color.White.copy(alpha = 0.7f))
            .border(1.dp, AppGreen.copy(alpha = 0.30f), RoundedCornerShape(50))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = "TODAY'S INSIGHT",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Poppins,
            color = AppGreen,
            letterSpacing = 0.8.sp
        )
    }
}

@Composable
private fun AskMorePill(onClick: () -> Unit, enabled: Boolean) {
    val bgAlpha = if (enabled) 0.10f else 0.06f
    val textAlpha = if (enabled) 1f else 0.55f
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(AppGreen.copy(alpha = bgAlpha))
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Ask more →",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = Poppins,
            color = AppGreen.copy(alpha = textAlpha)
        )
    }
}

@Composable
private fun CloseIconButton(onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier.size(36.dp)) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = Color.Gray.copy(alpha = 0.55f),
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun RefreshIconButton(isRegenerating: Boolean, onRegenerate: () -> Unit) {
    Box(
        modifier = Modifier.size(36.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isRegenerating) {
            CircularProgressIndicator(
                color = AppGreen,
                strokeWidth = 1.5.dp,
                modifier = Modifier.size(16.dp)
            )
        } else {
            IconButton(onClick = onRegenerate, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Regenerate",
                    tint = Color.Gray.copy(alpha = 0.55f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun ThinDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color.Black.copy(alpha = 0.06f))
    )
}
