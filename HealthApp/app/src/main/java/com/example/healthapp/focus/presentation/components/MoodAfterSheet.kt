package com.example.healthapp.focus.presentation.components

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthapp.emotion.domain.model.Emotion
import com.example.healthapp.emotion.presentation.tint
import com.example.healthapp.ui.theme.AppGreen
import com.example.healthapp.ui.theme.Poppins

@Composable
fun MoodAfterSheet(
    actualMinutes: Int,
    onSave: (Emotion?) -> Unit,
    onSkip: () -> Unit
) {
    var picked by remember { mutableStateOf<Emotion?>(null) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 4.dp, bottom = 24.dp)
    ) {
        Text(
            text = "Nice work! 🎉",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Poppins,
            color = Color(0xFF1A1A1A)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "$actualMinutes min focused. How did it go?",
            fontSize = 13.sp,
            color = Color.Gray,
            fontFamily = Poppins
        )
        Spacer(Modifier.height(20.dp))
        EmotionRow(picked = picked, onPick = { picked = it })
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = { onSave(picked) },
            enabled = picked != null,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppGreen,
                disabledContainerColor = AppGreen.copy(alpha = 0.4f)
            )
        ) {
            Text("Save", fontWeight = FontWeight.Bold, fontFamily = Poppins, fontSize = 14.sp)
        }
        Spacer(Modifier.height(4.dp))
        TextButton(onClick = onSkip, modifier = Modifier.fillMaxWidth()) {
            Text("Skip", color = Color.Gray, fontFamily = Poppins, fontSize = 13.sp)
        }
    }
}

@Composable
private fun EmotionRow(picked: Emotion?, onPick: (Emotion) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Emotion.values().forEach { e ->
            val selected = picked == e
            val tint = e.tint()
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onPick(e) }
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (selected) tint.copy(alpha = 0.18f) else Color(0xFFF5F5F5))
                        .border(
                            width = if (selected) 2.dp else 0.dp,
                            color = if (selected) tint else Color.Transparent,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(e.emoji, fontSize = 24.sp)
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = e.displayName,
                    fontSize = 9.sp,
                    fontFamily = Poppins,
                    color = if (selected) tint else Color.Gray,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}
