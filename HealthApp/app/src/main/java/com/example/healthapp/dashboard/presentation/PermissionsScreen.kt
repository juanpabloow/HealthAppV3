package com.example.healthapp.dashboard.presentation

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthapp.ui.theme.AppGreen

@Composable
fun PermissionsScreen(
    modifier: Modifier = Modifier,
    onContinue: () -> Unit
) {
    val context = LocalContext.current
    var notificationsEnabled by remember { mutableStateOf(false) }
    var cameraEnabled by remember { mutableStateOf(false) }
    var usageAccessEnabled by remember { mutableStateOf(false) }
    var blockAppsEnabled by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Logo
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = AppGreen,
            modifier = Modifier.size(80.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("🌿", fontSize = 40.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Welcome to HealApp",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "To get started, we'll need a few permissions.",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Why we need them?",
            fontSize = 12.sp,
            color = AppGreen,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Permission rows
        PermissionRow(
            label = "Notifications",
            description = "Stay updated with your progress",
            checked = notificationsEnabled,
            onCheckedChange = { notificationsEnabled = it }
        )
        HorizontalDivider(color = Color(0xFFF0F0F0))

        PermissionRow(
            label = "Camera",
            description = "For profile photo",
            checked = cameraEnabled,
            onCheckedChange = { cameraEnabled = it }
        )
        HorizontalDivider(color = Color(0xFFF0F0F0))

        PermissionRow(
            label = "App Usage Access",
            description = "Track your screen time & app usage",
            checked = usageAccessEnabled,
            onCheckedChange = { checked ->
                usageAccessEnabled = checked
                if (checked) {
                    // Abre configuración del sistema para conceder el permiso
                    context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                }
            }
        )
        HorizontalDivider(color = Color(0xFFF0F0F0))

        PermissionRow(
            label = "Block Distracting Apps",
            description = "Helps limit app usage automatically",
            checked = blockAppsEnabled,
            onCheckedChange = { blockAppsEnabled = it }
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppGreen)
        ) {
            Text("Agree & Continue", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onContinue) {
            Text("Disagree", color = Color.Gray, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun PermissionRow(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Text(description, fontSize = 12.sp, color = Color.Gray)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AppGreen)
        )
    }
}
