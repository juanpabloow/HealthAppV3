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

@Composable
fun VerificationCodeScreen(
    modifier: Modifier = Modifier,
    verificationType: String = "email", // "email" or "phone"
    onGoBackClick: () -> Unit,
    onSendCodeClick: (String) -> Unit
) {
    var inputValue by remember { mutableStateOf("") }
    
    val isEmail = verificationType == "email"
    val title = "Send Verification Code"
    val subtitle = if (isEmail) "Enter your email address" else "Enter your phone number"
    val label = if (isEmail) "Email Address" else "Phone Number"
    val placeholder = if (isEmail) "example@gmail.com" else "+1 123 456 7890"
    val keyboardType = if (isEmail) KeyboardType.Email else KeyboardType.Phone

    AuthScaffold(modifier = modifier) {
        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = subtitle,
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Label
        Text(
            text = label,
            fontSize = 14.sp,
            color = AppGreen,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Custom Outlined TextField
        OutlinedTextField(
            value = inputValue,
            onValueChange = { inputValue = it },
            placeholder = { Text(placeholder, color = Color.LightGray) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            trailingIcon = {
                if (inputValue.isNotEmpty()) {
                    IconButton(onClick = { inputValue = "" }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear text",
                            tint = Color.Black
                        )
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                cursorColor = Color.Black,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Send Verification Code Button
        Button(
            onClick = { onSendCodeClick(inputValue) },
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
