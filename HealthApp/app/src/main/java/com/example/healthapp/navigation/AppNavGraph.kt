package com.example.healthapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.healthapp.auth.presentation.AuthViewModel
import com.example.healthapp.auth.presentation.CreateProfileScreen
import com.example.healthapp.auth.presentation.EnterCodeScreen
import com.example.healthapp.auth.presentation.LoginScreen
import com.example.healthapp.auth.presentation.SignUpScreen
import com.example.healthapp.auth.presentation.SplashScreen
import com.example.healthapp.auth.presentation.VerificationCodeScreen
import com.example.healthapp.auth.presentation.WelcomeScreen
import com.example.healthapp.dashboard.presentation.PermissionsScreen
import com.example.healthapp.survey.presentation.SurveyActivitiesScreen
import com.example.healthapp.survey.presentation.SurveyGoalScreen
import com.example.healthapp.survey.presentation.SurveyMoodScreen
import com.example.healthapp.survey.presentation.SurveyViewModel
import com.example.healthapp.survey.presentation.SurveyWorriesScreen

private object Routes {
    const val SPLASH = "splash"
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val SIGN_UP = "sign_up"
    const val SEND_VERIFICATION = "send_verification/{type}/{flow}"
    const val ENTER_CODE = "enter_code/{type}/{flow}"
    const val CREATE_PROFILE = "create_profile"

    // Survey graph
    const val SURVEY_GRAPH = "survey_graph"
    const val SURVEY_GOAL = "survey_goal"
    const val SURVEY_WORRIES = "survey_worries"
    const val SURVEY_ACTIVITIES = "survey_activities"
    const val SURVEY_MOOD = "survey_mood"

    // Main app (with bottom nav)
    const val PERMISSIONS = "permissions"
    const val MAIN = "main"
}

@Composable
fun AppNavGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.state.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        modifier = modifier
    ) {

        // ── Splash ──────────────────────────────────────────────────────
        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateToWelcome = {
                    val dest = if (authState.isAuthenticated) Routes.MAIN else Routes.WELCOME
                    navController.navigate(dest) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // ── Auth ────────────────────────────────────────────────────────
        composable(Routes.WELCOME) {
            WelcomeScreen(
                onLoginClick = { navController.navigate(Routes.LOGIN) },
                onSignUpClick = { navController.navigate(Routes.SIGN_UP) },
                onSkipClick = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.WELCOME) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onGoBackClick = { navController.popBackStack() },
                onSkipClick = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.WELCOME) { inclusive = true }
                    }
                },
                onEmailClick = { navController.navigate("send_verification/email/login") },
                onPhoneClick = { navController.navigate("send_verification/phone/login") }
            )
        }

        composable(Routes.SIGN_UP) {
            SignUpScreen(
                onGoBackClick = { navController.popBackStack() },
                onEmailClick = { navController.navigate("send_verification/email/signup") },
                onPhoneClick = { navController.navigate("send_verification/phone/signup") }
            )
        }

        composable(
            route = Routes.SEND_VERIFICATION,
            arguments = listOf(
                navArgument("type") { type = NavType.StringType },
                navArgument("flow") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: "email"
            val flow = backStackEntry.arguments?.getString("flow") ?: "login"
            VerificationCodeScreen(
                verificationType = type,
                onGoBackClick = { navController.popBackStack() },
                onSendCodeClick = { input ->
                    if (type == "email") authViewModel.setPendingEmail(input)
                    else authViewModel.setPendingPhone(input)
                    navController.navigate("enter_code/$type/$flow")
                }
            )
        }

        composable(
            route = Routes.ENTER_CODE,
            arguments = listOf(
                navArgument("type") { type = NavType.StringType },
                navArgument("flow") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: "email"
            val flow = backStackEntry.arguments?.getString("flow") ?: "login"
            EnterCodeScreen(
                type = type,
                flow = flow,
                authState = authState,
                onGoBackClick = { navController.popBackStack() },
                onSignUp = { password -> authViewModel.signUp(authState.pendingEmail, password) },
                onSignIn = { password -> authViewModel.signIn(authState.pendingEmail, password) },
                onErrorDismiss = { authViewModel.clearError() },
                onAuthSuccess = {
                    if (flow == "login") {
                        navController.navigate(Routes.MAIN) {
                            popUpTo(Routes.WELCOME) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Routes.CREATE_PROFILE) {
                            popUpTo(Routes.WELCOME) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Routes.CREATE_PROFILE) {
            CreateProfileScreen(
                authState = authState,
                onSaveProfile = { name, phone, ageRange ->
                    authViewModel.saveProfile(name, phone, ageRange)
                },
                onUploadPhoto = { bytes -> authViewModel.uploadProfilePhoto(bytes) },
                onProfileSaved = {
                    navController.navigate(Routes.SURVEY_GRAPH) {
                        popUpTo(Routes.CREATE_PROFILE) { inclusive = true }
                    }
                },
                onErrorDismiss = { authViewModel.clearError() }
            )
        }

        // ── Survey (sub-grafo con ViewModel compartido) ─────────────────
        navigation(
            startDestination = Routes.SURVEY_GOAL,
            route = Routes.SURVEY_GRAPH
        ) {
            composable(Routes.SURVEY_GOAL) { backStackEntry ->
                val graphEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.SURVEY_GRAPH)
                }
                val surveyViewModel: SurveyViewModel = hiltViewModel(graphEntry)
                val surveyState by surveyViewModel.state.collectAsState()
                SurveyGoalScreen(
                    selectedGoals = surveyState.selectedGoals,
                    onToggleGoal = surveyViewModel::toggleGoal,
                    onBack = { navController.popBackStack() },
                    onNext = { navController.navigate(Routes.SURVEY_WORRIES) }
                )
            }

            composable(Routes.SURVEY_WORRIES) { backStackEntry ->
                val graphEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.SURVEY_GRAPH)
                }
                val surveyViewModel: SurveyViewModel = hiltViewModel(graphEntry)
                val surveyState by surveyViewModel.state.collectAsState()
                SurveyWorriesScreen(
                    selectedWorries = surveyState.selectedWorries,
                    onToggleWorry = surveyViewModel::toggleWorry,
                    onBack = { navController.popBackStack() },
                    onNext = { navController.navigate(Routes.SURVEY_ACTIVITIES) }
                )
            }

            composable(Routes.SURVEY_ACTIVITIES) { backStackEntry ->
                val graphEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.SURVEY_GRAPH)
                }
                val surveyViewModel: SurveyViewModel = hiltViewModel(graphEntry)
                val surveyState by surveyViewModel.state.collectAsState()
                SurveyActivitiesScreen(
                    selectedActivities = surveyState.selectedActivities,
                    onToggleActivity = surveyViewModel::toggleActivity,
                    onBack = { navController.popBackStack() },
                    onNext = { navController.navigate(Routes.SURVEY_MOOD) }
                )
            }

            composable(Routes.SURVEY_MOOD) { backStackEntry ->
                val graphEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.SURVEY_GRAPH)
                }
                val surveyViewModel: SurveyViewModel = hiltViewModel(graphEntry)
                val surveyState by surveyViewModel.state.collectAsState()

                LaunchedEffect(surveyState.isCompleted) {
                    if (surveyState.isCompleted) {
                        navController.navigate(Routes.PERMISSIONS) {
                            popUpTo(Routes.SURVEY_GRAPH) { inclusive = true }
                        }
                    }
                }

                SurveyMoodScreen(
                    selectedMood = surveyState.selectedMood,
                    onSelectMood = surveyViewModel::selectMood,
                    isLoading = surveyState.isLoading,
                    onBack = { navController.popBackStack() },
                    onNext = { surveyViewModel.submitSurvey() }
                )
            }
        }

        // ── Dashboard ────────────────────────────────────────────────────
        composable(Routes.PERMISSIONS) {
            PermissionsScreen(
                onContinue = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.PERMISSIONS) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.MAIN) {
            MainScreen(
                authState = authState,
                onSaveProfile = { name, phone, ageRange ->
                    authViewModel.saveProfile(name, phone, ageRange)
                },
                onUploadPhoto = { bytes -> authViewModel.uploadProfilePhoto(bytes) },
                onClearError = { authViewModel.clearError() },
                onResetProfileUpdated = { authViewModel.resetProfileUpdated() },
                onLogout = {
                    navController.navigate(Routes.WELCOME) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
