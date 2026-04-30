package com.example.healthapp.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthapp.R
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.healthapp.auth.presentation.AuthUiState
import com.example.healthapp.dashboard.presentation.DashboardViewModel
import com.example.healthapp.dashboard.presentation.ScreenTimeScreen
import com.example.healthapp.plans.navigation.PlansNavGraph
import com.example.healthapp.profile.navigation.ProfileNavGraph
import com.example.healthapp.ui.theme.AppGreen
import com.example.healthapp.ui.theme.Poppins

private enum class MainTab(val label: String, val icon: ImageVector) {
    STATS("Stats", Icons.Default.ShowChart),
    HOME("", Icons.Default.Favorite),
    PROFILE("Profile", Icons.Default.Person)
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    authState: AuthUiState,
    onSaveProfile: (name: String, phone: String?, ageRange: String?) -> Unit,
    onUploadPhoto: (ByteArray) -> Unit,
    onClearError: () -> Unit,
    onResetProfileUpdated: () -> Unit,
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
                        onRefreshPermission = dashboardViewModel::refreshPermission,
                        onProfileClick = { selectedTab = MainTab.PROFILE }
                    )
                }
                MainTab.HOME -> PlansNavGraph(
                    modifier = Modifier.fillMaxSize(),
                    user = authState.user,
                    onProfileClick = { selectedTab = MainTab.PROFILE }
                )
                MainTab.PROFILE -> ProfileNavGraph(
                    modifier = Modifier.fillMaxSize(),
                    authState = authState,
                    onSaveProfile = onSaveProfile,
                    onUploadPhoto = onUploadPhoto,
                    onClearError = onClearError,
                    onResetProfileUpdated = onResetProfileUpdated,
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(start = 16.dp, end = 16.dp, bottom = 10.dp, top = 22.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // ── Pill bar ─────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(6.dp, RoundedCornerShape(100.dp), clip = false)
                .clip(RoundedCornerShape(100.dp))
                .background(BarColor)
                .padding(horizontal = 16.dp, vertical = 8.dp),
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
            Spacer(modifier = Modifier.size(70.dp))

            BottomNavItem(
                icon = MainTab.PROFILE.icon,
                label = MainTab.PROFILE.label,
                selected = selectedTab == MainTab.PROFILE,
                onClick = { onTabSelected(MainTab.PROFILE) }
            )
        }

        // ── Center circle ─────────────────────────────────────────────
        Box(
            modifier = Modifier
                .size(82.dp)
                .offset(y = (-20).dp)
                .shadow(8.dp, CircleShape, clip = false)
                .clip(CircleShape)
                .background(CenterColor)
                .clickable { onTabSelected(MainTab.HOME) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Home",
                tint = Color.White,
                modifier = Modifier.size(46.dp)
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
    val activeColor  = Color.White
    val inactiveColor = Color.White.copy(alpha = 0.55f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 5.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) activeColor else inactiveColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontFamily = Poppins,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (selected) activeColor else inactiveColor
        )
        Spacer(modifier = Modifier.height(2.dp))
        // Underline indicator for selected tab
        Box(
            modifier = Modifier
                .width(16.dp)
                .height(2.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(if (selected) activeColor else Color.Transparent)
        )
    }
}

