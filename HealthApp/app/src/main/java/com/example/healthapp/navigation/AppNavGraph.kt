package com.example.healthapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.healthapp.auth.presentation.LoginScreen
import com.example.healthapp.auth.presentation.SignUpScreen
import com.example.healthapp.auth.presentation.SplashScreen
import com.example.healthapp.auth.presentation.WelcomeScreen

private object Routes {
    const val SPLASH = "splash"
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val SIGN_UP = "sign_up"
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
                onSkipClick = { /* TODO: navigate to home */ }
            )
        }
        composable(Routes.LOGIN) {
            LoginScreen(
                onGoBackClick = { navController.popBackStack() },
                onSkipClick = { /* TODO: navigate to home */ }
            )
        }
        composable(Routes.SIGN_UP) {
            SignUpScreen(
                onGoBackClick = { navController.popBackStack() }
            )
        }
    }
}
