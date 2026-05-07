package com.example.healthapp.habits.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.healthapp.habits.domain.util.todayDateString
import com.example.healthapp.habits.presentation.CreateEditHabitScreen
import com.example.healthapp.habits.presentation.CreateEditHabitViewModel
import com.example.healthapp.habits.presentation.HabitDetailScreen
import com.example.healthapp.habits.presentation.HabitListScreen
import com.example.healthapp.habits.presentation.HabitListViewModel
import com.example.healthapp.ui.theme.AppGreen

object HabitsRoutes {
    const val LIST        = "habits_list"
    const val DETAIL      = "habits_detail/{habitId}"
    const val CREATE_EDIT = "habits_create_edit?habitId={habitId}"

    fun detail(id: String): String = "habits_detail/$id"
    fun edit(id: String): String   = "habits_create_edit?habitId=$id"
    val create: String             = "habits_create_edit"
}

/**
 * Self-contained navigation tree for the Habits bottom-nav tab. Mirrors
 * [com.example.healthapp.plans.navigation.PlansNavGraph]: owns its own
 * [rememberNavController], hoists a single [HabitListViewModel] scoped to the
 * nearest [androidx.lifecycle.ViewModelStoreOwner] (the MAIN backstack entry),
 * so toggles, list, detail, and stats all share state.
 */
@Composable
fun HabitsNavGraph(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val vm: HabitListViewModel = hiltViewModel()
    val state by vm.state.collectAsState()

    NavHost(
        navController = navController,
        startDestination = HabitsRoutes.LIST,
        modifier = modifier
    ) {
        composable(HabitsRoutes.LIST) {
            HabitListScreen(
                state = state,
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
            val createVm: CreateEditHabitViewModel = hiltViewModel()
            val createState by createVm.state.collectAsState()

            LaunchedEffect(habitId) {
                if (habitId != null && createState.editingHabitId != habitId) {
                    state.habits.find { it.id == habitId }?.let { createVm.loadForEdit(it) }
                }
            }

            LaunchedEffect(createState.isSaved) {
                if (createState.isSaved) {
                    vm.reloadAfterSave()
                    createVm.reset()
                    navController.popBackStack()
                }
            }

            CreateEditHabitScreen(
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
