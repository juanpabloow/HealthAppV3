package com.example.healthapp.habits.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.example.healthapp.habits.domain.util.todayDateString
import com.example.healthapp.habits.presentation.CreateEditHabitScreen
import com.example.healthapp.habits.presentation.CreateEditHabitViewModel
import com.example.healthapp.habits.presentation.HabitDetailScreen
import com.example.healthapp.habits.presentation.HabitListScreen
import com.example.healthapp.habits.presentation.HabitListViewModel
import com.example.healthapp.ui.theme.AppGreen

object HabitsRoutes {
    const val GRAPH       = "habits_graph"
    const val LIST        = "habits_list"
    const val DETAIL      = "habits_detail/{habitId}"
    const val CREATE_EDIT = "habits_create_edit?habitId={habitId}"

    fun detail(id: String): String = "habits_detail/$id"
    fun edit(id: String): String   = "habits_create_edit?habitId=$id"
    val create: String             = "habits_create_edit"
}

fun NavGraphBuilder.habitsGraph(
    navController: NavController,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    navigation(
        startDestination = HabitsRoutes.LIST,
        route = HabitsRoutes.GRAPH
    ) {
        composable(HabitsRoutes.LIST) { entry ->
            val graphEntry = remember(entry) {
                navController.getBackStackEntry(HabitsRoutes.GRAPH)
            }
            val vm: HabitListViewModel = hiltViewModel(graphEntry)
            val state by vm.state.collectAsState()
            HabitListScreen(
                modifier = modifier,
                state = state,
                onBack = onExit,
                onHabitClick = { id -> navController.navigate(HabitsRoutes.detail(id)) },
                onToggleToday = vm::toggleToday,
                onCreate = { navController.navigate(HabitsRoutes.create) }
            )
        }

        composable(
            route = HabitsRoutes.DETAIL,
            arguments = listOf(navArgument("habitId") { type = NavType.StringType })
        ) { entry: NavBackStackEntry ->
            val habitId = entry.arguments?.getString("habitId") ?: return@composable
            val graphEntry = remember(entry) {
                navController.getBackStackEntry(HabitsRoutes.GRAPH)
            }
            val vm: HabitListViewModel = hiltViewModel(graphEntry)
            val state by vm.state.collectAsState()

            LaunchedEffect(habitId) { vm.loadDetail(habitId) }

            val habit = state.habits.find { it.id == habitId }
            if (habit == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AppGreen)
                }
            } else {
                val checkins = state.checkinsByHabit[habitId].orEmpty()
                val checkedDates = checkins.map { it.date }.toSet()
                HabitDetailScreen(
                    modifier = modifier,
                    habit = habit,
                    stats = state.statsByHabit[habitId],
                    checkedDates = checkedDates,
                    isCheckedToday = todayDateString() in checkedDates,
                    error = state.error,
                    onBack = { navController.popBackStack() },
                    onEdit = { navController.navigate(HabitsRoutes.edit(habitId)) },
                    onDelete = { vm.delete(habitId); navController.popBackStack() },
                    onToggleToday = { vm.toggleToday(habitId) },
                    onDismissError = vm::clearError
                )
            }
        }

        composable(
            route = HabitsRoutes.CREATE_EDIT,
            arguments = listOf(navArgument("habitId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { entry: NavBackStackEntry ->
            val habitId = entry.arguments?.getString("habitId")
            val graphEntry = remember(entry) {
                navController.getBackStackEntry(HabitsRoutes.GRAPH)
            }
            val listVm: HabitListViewModel = hiltViewModel(graphEntry)
            val listState by listVm.state.collectAsState()

            val createVm: CreateEditHabitViewModel = hiltViewModel()
            val createState by createVm.state.collectAsState()

            LaunchedEffect(habitId) {
                if (habitId != null && createState.editingHabitId != habitId) {
                    listState.habits.find { it.id == habitId }?.let { createVm.loadForEdit(it) }
                }
            }

            LaunchedEffect(createState.isSaved) {
                if (createState.isSaved) {
                    listVm.reloadAfterSave()
                    createVm.reset()
                    navController.popBackStack()
                }
            }

            CreateEditHabitScreen(
                modifier = modifier,
                state = createState,
                onNameChange = createVm::setName,
                onIconChange = createVm::setIcon,
                onDescriptionChange = createVm::setDescription,
                onToggleDay = createVm::toggleDay,
                onColorChange = createVm::setColor,
                onSave = createVm::save,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
