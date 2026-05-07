package com.example.healthapp.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.healthapp.auth.domain.model.User
import com.example.healthapp.ui.theme.AppGreen
import com.example.healthapp.ui.theme.Poppins

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    user: User?,
    onEditProfile: () -> Unit = {},
    onPermissions: () -> Unit = {},
    onHabits: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val displayName = user?.displayName ?: user?.email?.substringBefore("@") ?: "User"
    val initials = displayName.split(" ")
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .take(2)
        .joinToString("")
        .ifBlank { "U" }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
    ) {
        // ── Header ───────────────────────────────────────────────────────
        Text(
            text = "Profile",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Poppins,
            color = Color(0xFF1A1A1A),
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)
        )

        // ── User card ────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .shadow(4.dp, RoundedCornerShape(20.dp), clip = false)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .padding(vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(AppGreen.copy(alpha = 0.12f))
                    .border(3.dp, AppGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (user?.photoUrl != null) {
                    AsyncImage(
                        model = user.photoUrl,
                        contentDescription = "Profile photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        text = initials,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppGreen,
                        fontFamily = Poppins
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Name
            Text(
                text = displayName,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins,
                color = Color(0xFF1A1A1A)
            )

            Spacer(Modifier.height(10.dp))

            // Level badges
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .border(1.dp, Color(0xFFDDDDDD), RoundedCornerShape(50))
                        .padding(horizontal = 16.dp, vertical = 5.dp)
                ) {
                    Text(
                        "Level 2",
                        fontSize = 12.sp,
                        fontFamily = Poppins,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Medium
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(AppGreen)
                        .padding(horizontal = 16.dp, vertical = 5.dp)
                ) {
                    Text(
                        "Rookie",
                        fontSize = 12.sp,
                        fontFamily = Poppins,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Next: Captain, 5h more needed",
                fontSize = 12.sp,
                color = AppGreen,
                fontFamily = Poppins
            )
        }

        Spacer(Modifier.height(20.dp))

        // ── Menu items (each in its own card) ───────────────────────────
        ProfileMenuCard(
            icon = Icons.Default.Lock,
            label = "Permissions",
            onClick = onPermissions
        )
        Spacer(Modifier.height(10.dp))
        ProfileMenuCard(
            icon = Icons.Default.Edit,
            label = "Edit Profile",
            onClick = onEditProfile
        )
        Spacer(Modifier.height(10.dp))
        ProfileMenuCard(
            icon = Icons.Default.CheckCircle,
            label = "My Habits",
            onClick = onHabits
        )
        Spacer(Modifier.height(10.dp))
        ProfileMenuCard(
            icon = Icons.Default.Translate,
            label = "Languages",
            onClick = {}
        )
        Spacer(Modifier.height(10.dp))
        ProfileMenuCard(
            icon = Icons.Default.Info,
            label = "About us",
            onClick = {}
        )

        Spacer(Modifier.height(20.dp))

        // ── Action buttons ───────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Donate
            Box(
                modifier = Modifier
                    .weight(1f)
                    .shadow(2.dp, RoundedCornerShape(14.dp), clip = false)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .clickable { }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        "$",
                        fontSize = 16.sp,
                        color = AppGreen,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Poppins
                    )
                    Text(
                        "Donate",
                        fontSize = 14.sp,
                        fontFamily = Poppins,
                        color = AppGreen,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Logout
            Box(
                modifier = Modifier
                    .weight(1f)
                    .shadow(2.dp, RoundedCornerShape(14.dp), clip = false)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .clickable { onLogout() }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = null,
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        "Logout",
                        fontSize = 14.sp,
                        fontFamily = Poppins,
                        color = Color(0xFFE53935),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun ProfileMenuCard(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(2.dp, RoundedCornerShape(16.dp), clip = false)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF555555),
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                fontSize = 15.sp,
                fontFamily = Poppins,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF1A1A1A)
            )
        }
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFFBBBBBB),
            modifier = Modifier.size(22.dp)
        )
    }
}
