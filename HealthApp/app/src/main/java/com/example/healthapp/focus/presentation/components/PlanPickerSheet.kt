package com.example.healthapp.focus.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthapp.plans.domain.model.Plan
import com.example.healthapp.ui.theme.AppGreen
import com.example.healthapp.ui.theme.Poppins

@Composable
fun PlanPickerSheet(
    plans: List<Plan>,
    selectedPlanId: String?,
    onSelect: (String?) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 4.dp, bottom = 24.dp)
    ) {
        Text(
            text = "Link to a plan",
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = Poppins,
            color = Color(0xFF1A1A1A)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Sessions linked to a plan show up on its history.",
            fontSize = 12.sp,
            color = Color.Gray,
            fontFamily = Poppins
        )
        Spacer(Modifier.height(16.dp))

        PlanRow(
            emoji = "🚫",
            name = "No plan",
            description = "Free focus session",
            selected = selectedPlanId == null,
            onClick = { onSelect(null) }
        )

        if (plans.isEmpty()) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = "You don't have any plans yet. Create one from the Home tab.",
                fontSize = 12.sp,
                color = Color.Gray,
                fontFamily = Poppins
            )
        } else {
            plans.forEach { plan ->
                PlanRow(
                    emoji = plan.icon.ifBlank { "📋" },
                    name = plan.name.ifBlank { "Untitled plan" },
                    description = plan.description.ifBlank { "${plan.startHour}:00 – ${plan.endHour}:00" },
                    selected = selectedPlanId == plan.id,
                    onClick = { onSelect(plan.id) }
                )
            }
        }
    }
}

@Composable
private fun PlanRow(
    emoji: String,
    name: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) AppGreen.copy(alpha = 0.10f) else Color(0xFFF7F7F7))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(AppGreen.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 20.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Poppins,
                color = Color(0xFF1A1A1A)
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = Color.Gray,
                fontFamily = Poppins,
                maxLines = 1
            )
        }
        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = AppGreen,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
