package com.example.healthapp.auth.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthapp.ui.theme.AppGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationCodeScreen(
    modifier: Modifier = Modifier,
    onGoBackClick: () -> Unit,
    onSendCodeClick: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }

    AuthScaffold(modifier = modifier) {
        Text(
            text = "Send Verification Code",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Enter your email address",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Email Label
        Text(
            text = "Email Address",
            fontSize = 14.sp,
            color = AppGreen,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Custom Outlined TextField - Fixed for Material 3
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("example@gmail.com", color = Color.LightGray) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            trailingIcon = {
                if (email.isNotEmpty()) {
                    IconButton(onClick = { email = "" }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear text",
                            tint = Color.Black
                        )
                    }
                }
            },
            // The fix: Using OutlinedTextFieldDefaults.colors for Material 3 compatibility
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                cursorColor = Color.Black,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Send Verification Code Button
        Button(
            onClick = { onSendCodeClick(email) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppGreen,
                contentColor = Color.White
            )
        ) {
            Text(
                "SEND VERIFICATION CODE",
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Progress Indicators (Horizontal Lines)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(2.dp)
                    .background(Color(0xFFEEEEEE))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(2.dp)
                    .background(Color(0xFFEEEEEE))
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Go Back Text Button
        TextButton(
            onClick = onGoBackClick,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "Go Back",
                color = Color.LightGray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
