package com.example.healthapp.emotion.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.healthapp.emotion.presentation.EmotionCalendarScreen
import com.example.healthapp.emotion.presentation.EmotionPickerScreen
import com.example.healthapp.emotion.presentation.EmotionStatsScreen
import com.example.healthapp.emotion.presentation.EmotionTrackerViewModel

object EmotionRoutes {
    const val GRAPH    = "emotion_graph"
    const val PICKER   = "emotion_picker"
    const val CALENDAR = "emotion_calendar"
    const val STATS    = "emotion_stats"
}

fun NavGraphBuilder.emotionGraph(
    navController: NavController,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    navigation(
        startDestination = EmotionRoutes.PICKER,
        route = EmotionRoutes.GRAPH
    ) {
        composable(EmotionRoutes.PICKER) { entry ->
            val graphEntry = remember(entry) {
                navController.getBackStackEntry(EmotionRoutes.GRAPH)
            }
            val vm: EmotionTrackerViewModel = hiltViewModel(graphEntry)
            val state by vm.state.collectAsState()
            EmotionPickerScreen(
                modifier = modifier,
                state = state,
                onSelectEmotion = vm::selectEmotion,
                onNoteChange = vm::setNoteDraft,
                onSave = vm::saveCurrent,
                onSaved = {
                    vm.resetSaved()
                    onExit()
                },
                onBack = onExit,
                onOpenCalendar = { navController.navigate(EmotionRoutes.CALENDAR) },
                onOpenStats = { navController.navigate(EmotionRoutes.STATS) }
            )
        }

        composable(EmotionRoutes.CALENDAR) { entry ->
            val graphEntry = remember(entry) {
                navController.getBackStackEntry(EmotionRoutes.GRAPH)
            }
            val vm: EmotionTrackerViewModel = hiltViewModel(graphEntry)
            val state by vm.state.collectAsState()
            EmotionCalendarScreen(
                modifier = modifier,
                state = state,
                onBack = { navController.popBackStack() },
                onPrevMonth = vm::previousMonth,
                onNextMonth = vm::nextMonth,
                onDayClick = vm::selectDay,
                onDismissDay = vm::clearDaySelection
            )
        }

        composable(EmotionRoutes.STATS) { entry ->
            val graphEntry = remember(entry) {
                navController.getBackStackEntry(EmotionRoutes.GRAPH)
            }
            val vm: EmotionTrackerViewModel = hiltViewModel(graphEntry)
            val state by vm.state.collectAsState()
            EmotionStatsScreen(
                modifier = modifier,
                state = state,
                onBack = { navController.popBackStack() },
                onSelectRange = vm::selectStatsRange
            )
        }
    }
}
