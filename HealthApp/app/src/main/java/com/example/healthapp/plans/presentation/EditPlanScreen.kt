package com.example.healthapp.plans.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthapp.plans.domain.model.Plan
import com.example.healthapp.ui.theme.AppGreen
import com.example.healthapp.ui.theme.Poppins

private val EDIT_ICONS = listOf(
    "📋", "💼", "📚", "🏋️", "🎯", "🧘", "💻", "🎨",
    "🎵", "📖", "✏️", "🔬", "🌿", "💡", "🚀", "🏃"
)

private data class EditDay(val index: Int, val short: String)
private val EDIT_DAYS = listOf(
    EditDay(0, "Sun"), EditDay(1, "Mon"), EditDay(2, "Tue"),
    EditDay(3, "Wed"), EditDay(4, "Thu"), EditDay(5, "Fri"), EditDay(6, "Sat")
)

@Composable
fun EditPlanScreen(
    modifier: Modifier = Modifier,
    plan: Plan,
    onBack: () -> Unit,
    onSave: (Plan) -> Unit,
    isSaving: Boolean = false
) {
    var name by remember { mutableStateOf(plan.name) }
    var icon by remember { mutableStateOf(plan.icon) }
    var description by remember { mutableStateOf(plan.description) }
    var scheduleDays by remember { mutableStateOf(plan.scheduleDays) }
    var startHour by remember { mutableStateOf(plan.startHour) }
    var endHour by remember { mutableStateOf(plan.endHour) }
    var strictness by remember { mutableStateOf(plan.strictness) }

    val canSave = name.isNotBlank() && scheduleDays.isNotEmpty() && startHour < endHour

    Column(modifier = modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
        // ── TopBar ────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Edit Plan",
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Name
            item {
                EditSectionCard(title = "Plan name") {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Name", fontFamily = Poppins) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Description (optional)", fontFamily = Poppins) },
                        maxLines = 3,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Icon
            item {
                EditSectionCard(title = "Icon") {
                    EDIT_ICONS.chunked(4).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            row.forEach { e ->
                                val sel = icon == e
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (sel) AppGreen.copy(0.12f) else Color(0xFFF5F5F5))
                                        .border(
                                            if (sel) 2.dp else 1.dp,
                                            if (sel) AppGreen else Color(0xFFE0E0E0),
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable { icon = e },
                                    contentAlignment = Alignment.Center
                                ) { Text(e, fontSize = 24.sp) }
                            }
                            // Fill empty slots in last row
                            repeat(4 - row.size) { Spacer(Modifier.weight(1f)) }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }

            // Days
            item {
                EditSectionCard(title = "Days of the week") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        EDIT_DAYS.forEach { day ->
                            val sel = day.index in scheduleDays
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (sel) AppGreen else Color(0xFFF5F5F5))
                                    .border(
                                        1.dp,
                                        if (sel) AppGreen else Color(0xFFE0E0E0),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        scheduleDays = if (sel) scheduleDays - day.index
                                        else scheduleDays + day.index
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    day.short,
                                    fontFamily = Poppins,
                                    fontSize = 10.sp,
                                    fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (sel) Color.White else Color(0xFF555555),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // Time
            item {
                EditSectionCard(title = "Time range") {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        EditHourPicker(
                            modifier = Modifier.weight(1f),
                            label = "Start",
                            hour = startHour,
                            onHourChange = { startHour = it }
                        )
                        EditHourPicker(
                            modifier = Modifier.weight(1f),
                            label = "End",
                            hour = endHour,
                            onHourChange = { endHour = it }
                        )
                    }
                }
            }

            // Strictness
            item {
                EditSectionCard(title = "Strictness") {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf("low" to "😌 Relaxed", "medium" to "😤 Medium", "high" to "🔥 High")
                            .forEach { (key, label) ->
                                val sel = strictness == key
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (sel) AppGreen.copy(0.12f) else Color(0xFFF5F5F5))
                                        .border(
                                            if (sel) 2.dp else 1.dp,
                                            if (sel) AppGreen else Color(0xFFE0E0E0),
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable { strictness = key }
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        label,
                                        fontFamily = Poppins,
                                        fontSize = 12.sp,
                                        fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (sel) AppGreen else Color(0xFF555555),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                    }
                }
            }
        }

        // ── Save button ───────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Button(
                onClick = {
                    onSave(
                        plan.copy(
                            name = name.trim(),
                            icon = icon,
                            description = description.trim(),
                            scheduleDays = scheduleDays,
                            startHour = startHour,
                            endHour = endHour,
                            strictness = strictness
                        )
                    )
                },
                enabled = canSave && !isSaving,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppGreen)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                } else {
                    Text(
                        "Save Changes",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun EditSectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                title,
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = Color(0xFF1A1A1A)
            )
            Spacer(Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun EditHourPicker(modifier: Modifier = Modifier, label: String, hour: Int, onHourChange: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        OutlinedTextField(
            value = "${hour}:00",
            onValueChange = {},
            readOnly = true,
            label = { Text(label, fontFamily = Poppins, fontSize = 12.sp) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = Color(0xFF1A1A1A),
                disabledBorderColor = Color(0xFFBDBDBD),
                disabledLabelColor = Color.Gray
            )
        )
        Box(modifier = Modifier.matchParentSize().clickable { expanded = true })
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            (0..23).forEach { h ->
                DropdownMenuItem(
                    text = { Text("${h}:00", fontFamily = Poppins) },
                    onClick = { onHourChange(h); expanded = false }
                )
            }
        }
    }
}
