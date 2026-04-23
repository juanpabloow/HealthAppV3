package com.example.healthapp.plans.presentation

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthapp.ui.theme.AppGreen
import com.example.healthapp.ui.theme.AppBarColors
import com.example.healthapp.ui.theme.Poppins

@Composable
fun CreatePlanStep2Screen(
    modifier: Modifier = Modifier,
    state: CreatePlanUiState,
    onToggleApp: (String) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val context = LocalContext.current
    val pm = context.packageManager

    val installedApps = remember {
        try {
            pm.getInstalledApplications(PackageManager.GET_META_DATA)
                .filter { info ->
                    (info.flags and ApplicationInfo.FLAG_SYSTEM) == 0 &&
                        pm.getLaunchIntentForPackage(info.packageName) != null
                }
                .sortedBy { pm.getApplicationLabel(it).toString() }
        } catch (e: Exception) {
            emptyList()
        }
    }

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
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "New Plan  2/3",
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        LinearProgressIndicator(
            progress = { 2f / 3f },
            modifier = Modifier.fillMaxWidth(),
            color = AppGreen,
            trackColor = Color(0xFFE0E0E0)
        )

        // ── Header ────────────────────────────────────────────────────
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
            Text(
                "Block apps",
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
            Text(
                "Select apps to block during this plan. " +
                    "${state.blockedApps.size} selected.",
                fontFamily = Poppins,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        // ── App list ──────────────────────────────────────────────────
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
        ) {
            if (installedApps.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No user-installed apps found.",
                            fontFamily = Poppins,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                items(installedApps, key = { it.packageName }) { appInfo ->
                    val appName = pm.getApplicationLabel(appInfo).toString()
                    val pkg = appInfo.packageName
                    val isSelected = pkg in state.blockedApps
                    val colorIndex = installedApps.indexOf(appInfo)

                    AppBlockRow(
                        appName = appName,
                        packageName = pkg,
                        isSelected = isSelected,
                        colorIndex = colorIndex,
                        onToggle = { onToggleApp(pkg) }
                    )
                }
            }
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppGreen)
            ) {
                Text(
                    if (state.blockedApps.isEmpty()) "Skip" else "Next",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun AppBlockRow(
    appName: String,
    packageName: String,
    isSelected: Boolean,
    colorIndex: Int,
    onToggle: () -> Unit
) {
    val dotColor = AppBarColors[colorIndex % AppBarColors.size]

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onToggle() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) AppGreen.copy(alpha = 0.06f) else Color.White
        ),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(dotColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    appName.first().uppercase(),
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = dotColor
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    appName,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
                Text(
                    packageName,
                    fontFamily = Poppins,
                    fontSize = 11.sp,
                    color = Color.Gray,
                    maxLines = 1
                )
            }
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(checkedColor = AppGreen)
            )
        }
    }
}
