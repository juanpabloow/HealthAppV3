package com.example.healthapp.plans.navigation

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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.healthapp.auth.domain.model.User
import com.example.healthapp.plans.presentation.CreatePlanStep1Screen
import com.example.healthapp.plans.presentation.CreatePlanStep2Screen
import com.example.healthapp.plans.presentation.CreatePlanStep3Screen
import com.example.healthapp.plans.presentation.CreatePlanViewModel
import com.example.healthapp.plans.presentation.EditPlanScreen
import com.example.healthapp.plans.presentation.PlanCalendarScreen
import com.example.healthapp.plans.presentation.PlanDetailScreen
import com.example.healthapp.plans.presentation.PlanListScreen
import com.example.healthapp.plans.presentation.PlanViewModel
import com.example.healthapp.ui.theme.AppGreen

private object PlanRoutes {
    const val LIST         = "plan_list"
    const val DETAIL       = "plan_detail/{planId}"
    const val CALENDAR     = "plan_calendar/{planId}"
    const val EDIT         = "edit_plan/{planId}"
    const val CREATE_GRAPH = "create_graph"
    const val CREATE_1     = "create_1"
    const val CREATE_2     = "create_2"
    const val CREATE_3     = "create_3"

    fun detail(id: String)   = "plan_detail/$id"
    fun calendar(id: String) = "plan_calendar/$id"
    fun edit(id: String)     = "edit_plan/$id"
}

@androidx.compose.runtime.Composable
fun PlansNavGraph(
    modifier: Modifier = Modifier,
    user: User? = null,
    onProfileClick: () -> Unit = {}
) {
    val navController = rememberNavController()

    // Single shared PlanViewModel — all routes read from and write to the same instance
    // so edits, deletes, and creates are immediately visible everywhere.
    val planVm: PlanViewModel = hiltViewModel()
    val state by planVm.state.collectAsState()

    NavHost(
        navController = navController,
        startDestination = PlanRoutes.LIST,
        modifier = modifier
    ) {

        // ── Plan list ────────────────────────────────────────────────
        composable(route = PlanRoutes.LIST) {
            PlanListScreen(
                state          = state,
                user           = user,
                onPlanClick    = { planId -> navController.navigate(PlanRoutes.detail(planId)) },
                onCreateClick  = { navController.navigate(PlanRoutes.CREATE_GRAPH) },
                onDeletePlan   = { planId -> planVm.deletePlan(planId) },
                onProfileClick = onProfileClick
            )
        }

        // ── Plan detail ──────────────────────────────────────────────
        composable(
            route = PlanRoutes.DETAIL,
            arguments = listOf(navArgument("planId") { type = NavType.StringType })
        ) { entry: NavBackStackEntry ->
            val planId = entry.arguments?.getString("planId") ?: return@composable

            LaunchedEffect(planId) { planVm.loadSessions(planId) }

            val plan = state.plans.find { it.id == planId }

            when {
                state.isLoading || (plan == null && state.error == null) -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AppGreen)
                    }
                }
                plan != null -> {
                    val sessions = state.sessions[planId] ?: emptyList()
                    val metrics  = planVm.computeMetrics(plan, sessions)
                    PlanDetailScreen(
                        plan       = plan,
                        sessions   = sessions,
                        metrics    = metrics,
                        onBack     = { navController.popBackStack() },
                        onEdit     = { navController.navigate(PlanRoutes.edit(planId)) },
                        onDelete   = { planVm.deletePlan(planId); navController.popBackStack() },
                        onCalendar = { navController.navigate(PlanRoutes.calendar(planId)) }
                    )
                }
            }
        }

        // ── Calendar ─────────────────────────────────────────────────
        composable(
            route = PlanRoutes.CALENDAR,
            arguments = listOf(navArgument("planId") { type = NavType.StringType })
        ) { entry: NavBackStackEntry ->
            val planId = entry.arguments?.getString("planId") ?: return@composable

            val plan = state.plans.find { it.id == planId }
            if (plan == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AppGreen)
                }
            } else {
                PlanCalendarScreen(plan = plan, onBack = { navController.popBackStack() })
            }
        }

        // ── Edit plan ─────────────────────────────────────────────────
        composable(
            route = PlanRoutes.EDIT,
            arguments = listOf(navArgument("planId") { type = NavType.StringType })
        ) { entry: NavBackStackEntry ->
            val planId   = entry.arguments?.getString("planId") ?: return@composable
            val createVm: CreatePlanViewModel = hiltViewModel()
            val editState by createVm.state.collectAsState()

            val plan = state.plans.find { it.id == planId }
            if (plan != null) {
                LaunchedEffect(editState.isSaved) {
                    if (editState.isSaved) {
                        planVm.reloadAfterSave()   // refreshes the shared VM → list updates instantly
                        createVm.reset()
                        navController.popBackStack()
                    }
                }
                EditPlanScreen(
                    plan     = plan,
                    onBack   = { navController.popBackStack() },
                    onSave   = { updated -> createVm.loadForEdit(updated); createVm.save() },
                    isSaving = editState.isLoading
                )
            }
        }

        // ── Create (3-step sub-graph) ─────────────────────────────────
        navigation(
            startDestination = PlanRoutes.CREATE_1,
            route            = PlanRoutes.CREATE_GRAPH
        ) {
            composable(route = PlanRoutes.CREATE_1) { entry: NavBackStackEntry ->
                val graphEntry = remember(entry) {
                    navController.getBackStackEntry(PlanRoutes.CREATE_GRAPH)
                }
                val createVm: CreatePlanViewModel = hiltViewModel(graphEntry)
                val createState by createVm.state.collectAsState()

                CreatePlanStep1Screen(
                    state               = createState,
                    onNameChange        = createVm::setName,
                    onIconSelect        = createVm::setIcon,
                    onDescriptionChange = createVm::setDescription,
                    onClose             = {
                        createVm.reset()
                        navController.popBackStack(PlanRoutes.LIST, inclusive = false)
                    },
                    onNext = {
                        createVm.nextStep()
                        navController.navigate(PlanRoutes.CREATE_2)
                    }
                )
            }

            composable(route = PlanRoutes.CREATE_2) { entry: NavBackStackEntry ->
                val graphEntry = remember(entry) {
                    navController.getBackStackEntry(PlanRoutes.CREATE_GRAPH)
                }
                val createVm: CreatePlanViewModel = hiltViewModel(graphEntry)
                val createState by createVm.state.collectAsState()

                CreatePlanStep2Screen(
                    state       = createState,
                    onToggleApp = createVm::toggleBlockedApp,
                    onBack      = { createVm.prevStep(); navController.popBackStack() },
                    onNext      = { createVm.nextStep(); navController.navigate(PlanRoutes.CREATE_3) }
                )
            }

            composable(route = PlanRoutes.CREATE_3) { entry: NavBackStackEntry ->
                val graphEntry = remember(entry) {
                    navController.getBackStackEntry(PlanRoutes.CREATE_GRAPH)
                }
                val createVm: CreatePlanViewModel = hiltViewModel(graphEntry)
                val createState by createVm.state.collectAsState()

                LaunchedEffect(createState.isSaved) {
                    if (createState.isSaved) {
                        planVm.reloadAfterSave()   // same shared VM → list sees new plan immediately
                        createVm.reset()
                        navController.popBackStack(PlanRoutes.LIST, inclusive = false)
                    }
                }
                CreatePlanStep3Screen(
                    state              = createState,
                    onToggleDay        = createVm::toggleDay,
                    onStartHourChange  = createVm::setStartHour,
                    onEndHourChange    = createVm::setEndHour,
                    onStrictnessChange = createVm::setStrictness,
                    onBack             = { createVm.prevStep(); navController.popBackStack() },
                    onSave             = { createVm.save() }
                )
            }
        }
    }
}
