package com.example.healthapp.emotion.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthapp.emotion.domain.model.Emotion
import com.example.healthapp.ui.theme.AppGreen
import com.example.healthapp.ui.theme.Poppins

private val PageBg = Color(0xFFF5F7FA)

@Composable
fun EmotionStatsScreen(
    modifier: Modifier = Modifier,
    state: EmotionUiState,
    onBack: () -> Unit,
    onSelectRange: (StatsRange) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PageBg)
    ) {
        // ── Top bar ─────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 4.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Mood Stats",
                fontSize = 17.sp,
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.size(48.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            RangeTabs(
                selected = state.statsRange,
                onSelect = onSelectRange
            )
            Spacer(Modifier.height(20.dp))

            val stats = state.stats
            if (stats == null || stats.totalEntries == 0) {
                EmptyStats()
            } else {
                MostFrequentCard(emotion = stats.mostFrequent, count = stats.mostFrequent?.let { stats.counts[it] } ?: 0, total = stats.totalEntries)
                Spacer(Modifier.height(20.dp))
                EmotionBarChart(counts = stats.counts)
                Spacer(Modifier.height(24.dp))
                CountsList(counts = stats.counts, total = stats.totalEntries)
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun RangeTabs(selected: StatsRange, onSelect: (StatsRange) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFF0F0F0))
            .padding(4.dp)
    ) {
        StatsRange.values().forEach { r ->
            val sel = r == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (sel) AppGreen else Color.Transparent)
                    .clickable { onSelect(r) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (r) {
                        StatsRange.WEEK -> "Week"
                        StatsRange.MONTH -> "Month"
                        StatsRange.ALL -> "All time"
                    },
                    fontSize = 13.sp,
                    fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal,
                    color = if (sel) Color.White else Color.Gray,
                    fontFamily = Poppins
                )
            }
        }
    }
}

@Composable
private fun MostFrequentCard(emotion: Emotion?, count: Int, total: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val tint = emotion?.tint() ?: AppGreen
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Text(emotion?.emoji ?: "🙂", fontSize = 32.sp)
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = "Most frequent",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontFamily = Poppins
                )
                Text(
                    text = emotion?.displayName ?: "—",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Poppins,
                    color = tint
                )
                Text(
                    text = "$count of $total entries",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontFamily = Poppins
                )
            }
        }
    }
}

@Composable
private fun EmotionBarChart(counts: Map<Emotion, Int>) {
    val all = Emotion.values().toList()
    val maxCount = (counts.values.maxOrNull() ?: 1).coerceAtLeast(1)
    val maxBarHeight = 120.dp

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Distribution",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Poppins,
                color = Color(0xFF1A1A1A)
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                all.forEach { e ->
                    val c = counts[e] ?: 0
                    val frac = c.toFloat() / maxCount
                    val barH = (maxBarHeight.value * if (c == 0) 0.02f else frac.coerceAtLeast(0.06f)).dp
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (c > 0) c.toString() else "",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            fontFamily = Poppins
                        )
                        Spacer(Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(26.dp)
                                .height(barH)
                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                .background(
                                    if (c == 0) e.tint().copy(alpha = 0.18f) else e.tint()
                                )
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(e.emoji, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun CountsList(counts: Map<Emotion, Int>, total: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Breakdown",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Poppins,
                color = Color(0xFF1A1A1A)
            )
            Spacer(Modifier.height(12.dp))
            Emotion.values()
                .map { it to (counts[it] ?: 0) }
                .sortedByDescending { it.second }
                .forEach { (e, c) ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(e.emoji, fontSize = 22.sp)
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = e.displayName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = Poppins,
                            modifier = Modifier.weight(1f),
                            color = Color(0xFF1A1A1A)
                        )
                        Box(
                            modifier = Modifier
                                .width(120.dp)
                                .height(6.dp)
                                .clip(RoundedCornerShape(50))
                                .background(e.tint().copy(alpha = 0.14f))
                        ) {
                            val frac = if (total > 0) c.toFloat() / total else 0f
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(frac.coerceIn(0f, 1f))
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(e.tint())
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = c.toString(),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = Poppins,
                            color = Color(0xFF555555)
                        )
                    }
                }
        }
    }
}

@Composable
private fun EmptyStats() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("📊", fontSize = 56.sp)
        Spacer(Modifier.height(12.dp))
        Text(
            text = "No data yet",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = Poppins,
            color = Color(0xFF1A1A1A)
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Log a mood to start seeing your patterns.",
            fontSize = 13.sp,
            color = Color.Gray,
            fontFamily = Poppins,
            textAlign = TextAlign.Center
        )
    }
}
