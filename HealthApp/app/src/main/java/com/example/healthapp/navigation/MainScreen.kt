package com.example.healthapp.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.healthapp.auth.domain.model.User
import com.example.healthapp.dashboard.presentation.DashboardViewModel
import com.example.healthapp.dashboard.presentation.ScreenTimeScreen
import com.example.healthapp.plans.navigation.PlansNavGraph
import com.example.healthapp.profile.presentation.ProfileScreen
import com.example.healthapp.ui.theme.AppGreen
import com.example.healthapp.ui.theme.Poppins

private enum class MainTab(val label: String, val icon: ImageVector) {
    STATS("Stats", Icons.Default.Star),
    HOME("", Icons.Default.Favorite),
    PROFILE("Profile", Icons.Default.Person)
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    user: User?,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(MainTab.STATS) }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            MainBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                MainTab.STATS -> {
                    val dashboardViewModel: DashboardViewModel = hiltViewModel()
                    val dashboardState by dashboardViewModel.state.collectAsState()
                    ScreenTimeScreen(
                        state = dashboardState,
                        onTabSelected = dashboardViewModel::selectTab,
                        onDateSelected = dashboardViewModel::selectDate,
                        onRefreshPermission = dashboardViewModel::refreshPermission
                    )
                }
                MainTab.HOME -> PlansNavGraph(modifier = Modifier.fillMaxSize())
                MainTab.PROFILE -> ProfileScreen(
                    user = user,
                    onLogout = onLogout
                )
            }
        }
    }
}

private val BarColor    = Color(0xFF43B34C)
private val CenterColor = Color(0xFF5ECF2A)

@Composable
private fun MainBottomBar(selectedTab: MainTab, onTabSelected: (MainTab) -> Unit) {
    // Extra top padding leaves room for the circle to protrude upward
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(start = 20.dp, end = 20.dp, bottom = 12.dp, top = 20.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // ── Pill bar ────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(10.dp, RoundedCornerShape(100.dp), clip = false)
                .clip(RoundedCornerShape(100.dp))
                .background(BarColor)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BottomNavItem(
                icon = MainTab.STATS.icon,
                label = MainTab.STATS.label,
                selected = selectedTab == MainTab.STATS,
                onClick = { onTabSelected(MainTab.STATS) }
            )
            // Gap where the circle sits
            Spacer(modifier = Modifier.size(56.dp))

            BottomNavItem(
                icon = MainTab.PROFILE.icon,
                label = MainTab.PROFILE.label,
                selected = selectedTab == MainTab.PROFILE,
                onClick = { onTabSelected(MainTab.PROFILE) }
            )
        }

        // ── Center circle — protrudes above the bar ──────────────────
        Box(
            modifier = Modifier
                .size(60.dp)
                .offset(y = (-18).dp)          // pop out above the bar
                .shadow(8.dp, CircleShape, clip = false)
                .clip(CircleShape)
                .background(CenterColor)
                .clickable { onTabSelected(MainTab.HOME) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = MainTab.HOME.icon,
                contentDescription = "Home",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) Color.White else Color.White.copy(alpha = 0.55f),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontFamily = Poppins,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (selected) Color.White else Color.White.copy(alpha = 0.55f)
        )
    }
}

