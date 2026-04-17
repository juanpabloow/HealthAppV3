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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
            .background(Color(0xFFF8F8F8))
            .verticalScroll(rememberScrollState())
    ) {
        // ── Header label ────────────────────────────────────────────────
        Text(
            text = "Profile",
            fontSize = 13.sp,
            color = Color.Gray,
            fontFamily = Poppins,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        )

        // ── Avatar card ─────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .padding(vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(AppGreen.copy(alpha = 0.15f))
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
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppGreen,
                        fontFamily = Poppins
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Name
            Text(
                text = displayName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Level badges
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .border(1.dp, Color(0xFFDDDDDD), RoundedCornerShape(50))
                        .padding(horizontal = 14.dp, vertical = 5.dp)
                ) {
                    Text("Level 1", fontSize = 12.sp, fontFamily = Poppins, color = Color.DarkGray, fontWeight = FontWeight.Medium)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(AppGreen)
                        .padding(horizontal = 14.dp, vertical = 5.dp)
                ) {
                    Text("Rookie", fontSize = 12.sp, fontFamily = Poppins, color = Color.White, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Next: Explorer, 2h more needed",
                fontSize = 12.sp,
                color = AppGreen,
                fontFamily = Poppins
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Menu items ──────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
        ) {
            ProfileMenuItem(icon = Icons.Default.Lock, label = "Permissions", onClick = onPermissions)
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF0F0F0))
            ProfileMenuItem(icon = Icons.Default.Person, label = "Edit Profile", onClick = onEditProfile)
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF0F0F0))
            ProfileMenuItem(icon = Icons.Default.Settings, label = "Languages", onClick = {})
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF0F0F0))
            ProfileMenuItem(icon = Icons.Default.Info, label = "About us", onClick = {})
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Action buttons ──────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Donate
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, Color(0xFFDDDDDD), RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .clickable { }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("$", fontSize = 16.sp, color = AppGreen, fontWeight = FontWeight.Bold)
                    Text("Donate", fontSize = 14.sp, fontFamily = Poppins, color = Color.DarkGray, fontWeight = FontWeight.Medium)
                }
            }

            // Logout
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, Color(0xFFDDDDDD), RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .clickable { onLogout() }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(16.dp)
                    )
                    Text("Logout", fontSize = 14.sp, fontFamily = Poppins, color = Color(0xFFE53935), fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ProfileMenuItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(20.dp))
            Text(label, fontSize = 15.sp, fontFamily = Poppins, fontWeight = FontWeight.Normal, color = Color.Black)
        }
        Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color(0xFFBBBBBB), modifier = Modifier.size(20.dp))
    }
}
