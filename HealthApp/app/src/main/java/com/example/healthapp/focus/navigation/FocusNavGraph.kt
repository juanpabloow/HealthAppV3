package com.example.healthapp.focus.navigation

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
import com.example.healthapp.focus.presentation.FocusHistoryScreen
import com.example.healthapp.focus.presentation.FocusSessionViewModel
import com.example.healthapp.focus.presentation.FocusStatsScreen
import com.example.healthapp.focus.presentation.FocusTimerScreen

object FocusRoutes {
    const val GRAPH   = "focus_graph"
    const val TIMER   = "focus_timer"
    const val HISTORY = "focus_history"
    const val STATS   = "focus_stats"
}

fun NavGraphBuilder.focusGraph(
    navController: NavController,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    navigation(
        startDestination = FocusRoutes.TIMER,
        route = FocusRoutes.GRAPH
    ) {
        composable(FocusRoutes.TIMER) { entry ->
            val graphEntry = remember(entry) {
                navController.getBackStackEntry(FocusRoutes.GRAPH)
            }
            val vm: FocusSessionViewModel = hiltViewModel(graphEntry)
            val state by vm.state.collectAsState()
            FocusTimerScreen(
                modifier = modifier,
                state = state,
                onBack = onExit,
                onOpenHistory = { navController.navigate(FocusRoutes.HISTORY) },
                onOpenStats = { navController.navigate(FocusRoutes.STATS) },
                onSelectPreset = vm::selectPreset,
                onShowPlanPicker = vm::showPlanPicker,
                onHidePlanPicker = vm::hidePlanPicker,
                onSelectPlan = vm::selectLinkedPlan,
                onStart = vm::start,
                onPause = vm::pause,
                onResume = vm::resume,
                onStop = vm::stop,
                onSelectMoodAfter = vm::selectMoodAfter,
                onDismissMoodSheet = vm::dismissMoodSheet
            )
        }
        composable(FocusRoutes.HISTORY) { entry ->
            val graphEntry = remember(entry) {
                navController.getBackStackEntry(FocusRoutes.GRAPH)
            }
            val vm: FocusSessionViewModel = hiltViewModel(graphEntry)
            val state by vm.state.collectAsState()
            FocusHistoryScreen(
                modifier = modifier,
                state = state,
                onBack = { navController.popBackStack() }
            )
        }
        composable(FocusRoutes.STATS) { entry ->
            val graphEntry = remember(entry) {
                navController.getBackStackEntry(FocusRoutes.GRAPH)
            }
            val vm: FocusSessionViewModel = hiltViewModel(graphEntry)
            val state by vm.state.collectAsState()
            FocusStatsScreen(
                modifier = modifier,
                state = state,
                onBack = { navController.popBackStack() },
                onSelectRange = vm::selectStatsRange
            )
        }
    }
}
