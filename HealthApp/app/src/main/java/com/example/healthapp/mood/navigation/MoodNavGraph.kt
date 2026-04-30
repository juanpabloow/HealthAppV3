package com.example.healthapp.mood.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.healthapp.mood.presentation.MoodHistoryScreen
import com.example.healthapp.mood.presentation.MoodTrackerScreen
import com.example.healthapp.mood.presentation.MoodViewModel

private object MoodRoutes {
    const val TRACKER = "mood_tracker"
    const val HISTORY = "mood_history"
}

@Composable
fun MoodNavGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val vm: MoodViewModel = hiltViewModel()
    val state by vm.state.collectAsState()

    NavHost(navController, startDestination = MoodRoutes.TRACKER, modifier = modifier) {
        composable(MoodRoutes.TRACKER) {
            MoodTrackerScreen(
                state = state,
                onSelectMood = vm::selectMood,
                onNoteChange = vm::setNote,
                onSave = vm::saveEntry,
                onHistoryClick = { navController.navigate(MoodRoutes.HISTORY) },
                onClearSaved = vm::clearSaved,
                onClearError = vm::clearError
            )
        }
        composable(MoodRoutes.HISTORY) {
            MoodHistoryScreen(
                state = state,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
