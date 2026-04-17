package com.example.healthapp.auth.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthapp.ui.theme.AppGreen

@Composable
fun EnterCodeScreen(
    type: String,
    flow: String,
    authState: AuthUiState,
    onGoBackClick: () -> Unit,
    onSignUp: (password: String) -> Unit,
    onSignIn: (password: String) -> Unit,
    onErrorDismiss: () -> Unit,
    onAuthSuccess: () -> Unit
) {
    var password by remember { mutableStateOf("") }
    var authAttempted by remember { mutableStateOf(false) }
    val isEmailFlow = type == "email"

    // Navegar solo si el usuario hizo click Y la auth completó exitosamente
    LaunchedEffect(authState.user, authState.isLoading, authState.error) {
        if (authAttempted && !authState.isLoading && authState.error == null && authState.user != null) {
            onAuthSuccess()
        }
    }

    // Mostrar error si hay uno
    if (authState.error != null) {
        AlertDialog(
            onDismissRequest = onErrorDismiss,
            title = { Text("Error") },
            text = { Text(authState.error) },
            confirmButton = {
                TextButton(onClick = onErrorDismiss) { Text("OK") }
            }
        )
    }

    AuthScaffold {
        Text(
            text = if (isEmailFlow) "Crear contraseña" else "Enter Code",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (isEmailFlow) {
                if (flow == "signup") "Elige una contraseña para tu cuenta" else "Ingresa tu contraseña"
            } else {
                "We've sent a 6-digit code to your $type"
            },
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(if (isEmailFlow) "Contraseña" else "Verification Code") },
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isEmailFlow) KeyboardType.Password else KeyboardType.Number
            ),
            visualTransformation = if (isEmailFlow) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                authAttempted = true
                if (isEmailFlow) {
                    if (flow == "signup") onSignUp(password) else onSignIn(password)
                } else {
                    onAuthSuccess()
                }
            },
            enabled = password.isNotBlank() && !authState.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppGreen)
        ) {
            if (authState.isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = if (isEmailFlow && flow == "signup") "CREAR CUENTA" else "CONTINUAR",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        TextButton(
            onClick = onGoBackClick,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Go Back", color = Color.Gray)
        }
    }
}
