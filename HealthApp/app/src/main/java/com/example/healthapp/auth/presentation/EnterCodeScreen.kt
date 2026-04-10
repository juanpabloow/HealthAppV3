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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthapp.ui.theme.AppGreen

@Composable
fun EnterCodeScreen(
    type: String,
    flow: String,
    onGoBackClick: () -> Unit,
    onCodeVerified: () -> Unit
) {
    var code by remember { mutableStateOf("") }

    AuthScaffold {
        Text(
            text = "Enter Code",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "We've sent a 6-digit code to your $type",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = code,
            onValueChange = { if (it.length <= 6) code = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Verification Code") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onCodeVerified,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppGreen)
        ) {
            Text("VERIFY", fontWeight = FontWeight.Bold)
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
