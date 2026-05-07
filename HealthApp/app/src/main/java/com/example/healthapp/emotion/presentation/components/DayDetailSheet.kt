package com.example.healthapp.emotion.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthapp.emotion.domain.model.EmotionEntry
import com.example.healthapp.emotion.presentation.tint
import com.example.healthapp.ui.theme.Poppins
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun DayDetailSheet(date: String, entry: EmotionEntry?) {
    val displayDate = formatLong(date)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 4.dp, bottom = 32.dp)
    ) {
        Text(
            text = displayDate,
            fontSize = 13.sp,
            color = Color.Gray,
            fontFamily = Poppins
        )
        Spacer(Modifier.height(8.dp))
        if (entry == null) {
            Text(
                text = "No mood logged",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Poppins,
                color = Color(0xFF1A1A1A)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Tap an emotion on today's screen to log how you feel.",
                fontSize = 13.sp,
                color = Color.Gray,
                fontFamily = Poppins
            )
        } else {
            val tint = entry.emotion.tint()
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Text(entry.emotion.emoji, fontSize = 38.sp)
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = entry.emotion.displayName,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins,
                color = tint
            )
            if (!entry.note.isNullOrBlank()) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = entry.note,
                    fontSize = 14.sp,
                    color = Color(0xFF1A1A1A),
                    fontFamily = Poppins
                )
            }
        }
    }
}

private fun formatLong(isoDate: String): String = try {
    val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val formatter = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
    formatter.format(parser.parse(isoDate)!!)
} catch (_: Exception) {
    isoDate
}
