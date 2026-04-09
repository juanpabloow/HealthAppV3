package com.example.healthapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.healthapp.auth.presentation.CreateProfileScreen
import com.example.healthapp.auth.presentation.EnterCodeScreen
import com.example.healthapp.auth.presentation.HomeScreen
import com.example.healthapp.auth.presentation.LoginScreen
import com.example.healthapp.auth.presentation.SignUpScreen
import com.example.healthapp.auth.presentation.SplashScreen
import com.example.healthapp.auth.presentation.VerificationCodeScreen
import com.example.healthapp.auth.presentation.WelcomeScreen

private object Routes {
    const val SPLASH = "splash"
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val SIGN_UP = "sign_up"
    const val SEND_VERIFICATION = "send_verification/{type}/{flow}"
    const val ENTER_CODE = "enter_code/{type}/{flow}"
    const val HOME = "home"
    const val CREATE_PROFILE = "create_profile"
}

@Composable
fun AppNavGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        modifier = modifier
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateToWelcome = {
                    navController.navigate(Routes.WELCOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.WELCOME) {
            WelcomeScreen(
                onLoginClick = { navController.navigate(Routes.LOGIN) },
                onSignUpClick = { navController.navigate(Routes.SIGN_UP) },
                onSkipClick = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.WELCOME) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onGoBackClick = { navController.popBackStack() },
                onSkipClick = {
                    navController.navigate(Routes.HOME) {
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
                onSendCodeClick = { _ ->
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
                onGoBackClick = { navController.popBackStack() },
                onCodeVerified = {
                    if (flow == "login") {
                        navController.navigate(Routes.HOME) {
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

        composable(Routes.HOME) {
            HomeScreen()
        }

        composable(Routes.CREATE_PROFILE) {
            CreateProfileScreen()
        }
    }
}
