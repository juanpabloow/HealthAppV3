package com.example.healthapp.plans.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthapp.ui.theme.AppGreen
import com.example.healthapp.ui.theme.Poppins

private val planIcons = listOf(
    "📋", "💼", "📚", "🏋️", "🎯", "🧘", "💻", "🎨",
    "🎵", "📖", "✏️", "🔬", "🌿", "💡", "🚀", "🏃"
)

@Composable
fun CreatePlanStep1Screen(
    modifier: Modifier = Modifier,
    state: CreatePlanUiState,
    onNameChange: (String) -> Unit,
    onIconSelect: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onClose: () -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // ── TopBar ────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            IconButton(onClick = onClose, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
            Text(
                text = "New Plan  1/3",
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // ── Progress bar ──────────────────────────────────────────────
        LinearProgressIndicator(
            progress = { 1f / 3f },
            modifier = Modifier.fillMaxWidth(),
            color = AppGreen,
            trackColor = Color(0xFFE0E0E0)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(24.dp)
        ) {
            Text(
                "Name your plan",
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Give it a clear name and pick an icon.",
                fontFamily = Poppins,
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = state.name,
                onValueChange = onNameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Plan name", fontFamily = Poppins) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(24.dp))

            Text(
                "Choose an icon",
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Spacer(Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.height(200.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(planIcons) { emoji ->
                    val selected = state.icon == emoji
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (selected) AppGreen.copy(alpha = 0.15f)
                                else Color.White
                            )
                            .border(
                                width = if (selected) 2.dp else 1.dp,
                                color = if (selected) AppGreen else Color(0xFFE0E0E0),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onIconSelect(emoji) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(emoji, fontSize = 26.sp)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            OutlinedTextField(
                value = state.description,
                onValueChange = onDescriptionChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Description (optional)", fontFamily = Poppins) },
                maxLines = 3,
                shape = RoundedCornerShape(12.dp)
            )
        }

        // ── Bottom button ─────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Button(
                onClick = onNext,
                enabled = state.name.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppGreen)
            ) {
                Text(
                    "Next",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
        }
    }
}
