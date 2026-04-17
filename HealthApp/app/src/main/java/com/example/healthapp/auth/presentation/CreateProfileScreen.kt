package com.example.healthapp.auth.presentation

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
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.healthapp.ui.theme.AppGreen

private val ageRanges = listOf("13 - 17", "18 - 24", "24 - 40", "40 +")

@Composable
fun CreateProfileScreen(
    modifier: Modifier = Modifier,
    authState: AuthUiState,
    onSaveProfile: (name: String, phone: String?, ageRange: String?) -> Unit,
    onUploadPhoto: (ByteArray) -> Unit,
    onProfileSaved: () -> Unit,
    onErrorDismiss: () -> Unit
) {
    var showForm by remember { mutableStateOf(false) }

    if (!showForm) {
        ProfileIntroScreen(
            modifier = modifier,
            onContinue = { showForm = true },
            onSkip = onProfileSaved
        )
    } else {
        ProfileFormScreen(
            modifier = modifier,
            authState = authState,
            onSaveProfile = onSaveProfile,
            onUploadPhoto = onUploadPhoto,
            onProfileSaved = onProfileSaved,
            onErrorDismiss = onErrorDismiss
        )
    }
}

// ── Pantalla 1: Intro ────────────────────────────────────────────────────────

@Composable
private fun ProfileIntroScreen(
    modifier: Modifier = Modifier,
    onContinue: () -> Unit,
    onSkip: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Ilustración
        Box(
            modifier = Modifier
                .size(180.dp)
                .clip(CircleShape)
                .background(AppGreen.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = AppGreen,
                    modifier = Modifier.size(64.dp)
                )
            }
            // Badge check
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(AppGreen),
                contentAlignment = Alignment.Center
            ) {
                Text("✓", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Let's personalize\nyour experience",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 34.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "We'll ask a few quick questions to create the perfect plan for your digital wellness.",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Privacy badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(Color(0xFFF5F5F5))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = AppGreen,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                "Your info stays private and secure",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppGreen)
        ) {
            Text("Continue", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onSkip) {
            Text("Skip for now", color = Color.Gray, fontSize = 14.sp)
        }
    }
}

// ── Pantalla 2: Formulario ───────────────────────────────────────────────────

@Composable
private fun ProfileFormScreen(
    modifier: Modifier = Modifier,
    authState: AuthUiState,
    onSaveProfile: (name: String, phone: String?, ageRange: String?) -> Unit,
    onUploadPhoto: (ByteArray) -> Unit,
    onProfileSaved: () -> Unit,
    onErrorDismiss: () -> Unit
) {
    val context = LocalContext.current
    var fullName by remember { mutableStateOf(authState.user?.displayName ?: "") }
    var phone by remember { mutableStateOf(authState.user?.phone ?: "") }
    var selectedAgeRange by remember { mutableStateOf("18 - 24") }
    var savedHandled by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

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

    // Email pre-llenado desde auth
    val email = authState.user?.email ?: authState.pendingEmail

    LaunchedEffect(authState.isLoading, authState.error, authState.user) {
        if (!savedHandled && !authState.isLoading && authState.error == null
            && authState.user?.displayName != null
        ) {
            savedHandled = true
            onProfileSaved()
        }
    }

    if (authState.error != null) {
        AlertDialog(
            onDismissRequest = onErrorDismiss,
            title = { Text("Error") },
            text = { Text(authState.error) },
            confirmButton = { TextButton(onClick = onErrorDismiss) { Text("OK") } }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Create Your Profile",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "This helps us personalize your experience",
            fontSize = 13.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Avatar
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEEEEEE))
                    .clickable { openPhotoPicker() },
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Profile photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
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
                Text("+", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text("Add photo", fontSize = 12.sp, color = AppGreen, fontWeight = FontWeight.Medium)

        Spacer(modifier = Modifier.height(28.dp))

        // Full name
        ProfileField(
            label = "Full name",
            value = fullName,
            onValueChange = { fullName = it },
            placeholder = "Your full name",
            keyboardType = KeyboardType.Text
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Email (solo lectura)
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                "Email",
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = email,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("you@email.com", color = Color(0xFFCCCCCC)) },
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

        // Phone number
        ProfileField(
            label = "Phone number",
            value = phone,
            onValueChange = { phone = it },
            placeholder = "+57 399 9999999",
            keyboardType = KeyboardType.Phone
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Age range
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                "Age range",
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ageRanges.forEach { range ->
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
                            fontSize = 12.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            color = if (selected) Color.White else Color(0xFF666666)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        Button(
            onClick = { onSaveProfile(fullName, phone.ifBlank { null }, selectedAgeRange) },
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
                Text("Continue", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ProfileField(
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
