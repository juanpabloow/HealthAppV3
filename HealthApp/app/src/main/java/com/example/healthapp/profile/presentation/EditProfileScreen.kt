package com.example.healthapp.profile.presentation

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.healthapp.auth.presentation.AuthUiState
import com.example.healthapp.ui.theme.AppGreen
import com.example.healthapp.ui.theme.Poppins

private val AGE_RANGES = listOf("13 - 17", "18 - 24", "24 - 40", "40 +")

@Composable
fun EditProfileScreen(
    modifier: Modifier = Modifier,
    authState: AuthUiState,
    onSaveProfile: (name: String, phone: String?, ageRange: String?) -> Unit,
    onUploadPhoto: (ByteArray) -> Unit,
    onClearError: () -> Unit,
    onResetProfileUpdated: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val user = authState.user

    var fullName by remember { mutableStateOf(user?.displayName ?: "") }
    var phone by remember { mutableStateOf(user?.phone ?: "") }
    var selectedAgeRange by remember { mutableStateOf(user?.ageRange ?: "18 - 24") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Navigate back once the save completes successfully
    LaunchedEffect(authState.isProfileUpdated) {
        if (authState.isProfileUpdated) {
            onResetProfileUpdated()
            onBack()
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            val bytes = context.contentResolver.openInputStream(uri)?.readBytes()
            if (bytes != null) onUploadPhoto(bytes)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) photoPickerLauncher.launch("image/*")
    }

    fun openPhotoPicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    if (authState.error != null) {
        AlertDialog(
            onDismissRequest = onClearError,
            title = { Text("Error", fontFamily = Poppins) },
            text = { Text(authState.error, fontFamily = Poppins) },
            confirmButton = {
                TextButton(onClick = onClearError) {
                    Text("OK", color = AppGreen)
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // ── Top bar ───────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 8.dp, vertical = 12.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Text(
                text = "Edit Profile",
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // ── Form ──────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            // ── Avatar ────────────────────────────────────────────────────
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEEEEEE))
                        .clickable { openPhotoPicker() },
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        selectedImageUri != null -> AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Profile photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        user?.photoUrl != null -> AsyncImage(
                            model = user.photoUrl,
                            contentDescription = "Profile photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        else -> Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color(0xFFBBBBBB),
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(AppGreen)
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "✎",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "Change photo",
                fontSize = 12.sp,
                color = AppGreen,
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ── Full name ─────────────────────────────────────────────────
            EditProfileField(
                label = "Full name",
                value = fullName,
                onValueChange = { fullName = it },
                placeholder = "Your full name",
                keyboardType = KeyboardType.Text
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Email (read-only) ─────────────────────────────────────────
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Email",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = user?.email ?: "",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledBorderColor = Color(0xFFE0E0E0),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedBorderColor = Color(0xFFE0E0E0)
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Phone ─────────────────────────────────────────────────────
            EditProfileField(
                label = "Phone number",
                value = phone,
                onValueChange = { phone = it },
                placeholder = "+57 399 9999999",
                keyboardType = KeyboardType.Phone
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Age range ─────────────────────────────────────────────────
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Age range",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AGE_RANGES.forEach { range ->
                        val selected = selectedAgeRange == range
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selected) AppGreen else Color(0xFFF5F5F5))
                                .clickable { selectedAgeRange = range }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = range,
                                fontFamily = Poppins,
                                fontSize = 12.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                color = if (selected) Color.White else Color(0xFF666666)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // ── Save button ───────────────────────────────────────────────
            Button(
                onClick = {
                    onSaveProfile(
                        fullName,
                        phone.ifBlank { null },
                        selectedAgeRange
                    )
                },
                enabled = fullName.isNotBlank() && !authState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppGreen)
            ) {
                if (authState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Save Changes",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun EditProfileField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            label,
            fontSize = 12.sp,
            color = AppGreen,
            fontFamily = Poppins,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color(0xFFCCCCCC)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppGreen,
                unfocusedBorderColor = Color(0xFFE0E0E0),
                cursorColor = AppGreen,
                focusedLabelColor = AppGreen
            )
        )
    }
}
