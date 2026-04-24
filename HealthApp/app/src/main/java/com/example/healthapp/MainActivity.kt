package com.example.healthapp

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Modifier
import com.example.healthapp.navigation.AppNavGraph
import com.example.healthapp.ui.theme.HealthAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = Color.WHITE,
                darkScrim = Color.WHITE
            ),
            navigationBarStyle = SystemBarStyle.light(
                scrim = Color.WHITE,
                darkScrim = Color.WHITE
            )
        )
        setContent {
            HealthAppTheme {
                AppNavGraph(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding()
                )
            }
        }
    }
}
