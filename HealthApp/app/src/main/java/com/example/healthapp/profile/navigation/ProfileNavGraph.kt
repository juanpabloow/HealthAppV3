package com.example.healthapp.profile.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.healthapp.auth.presentation.AuthUiState
import com.example.healthapp.profile.presentation.EditProfileScreen
import com.example.healthapp.profile.presentation.ProfileScreen

private object ProfileRoutes {
    const val MAIN = "profile_main"
    const val EDIT = "profile_edit"
}

@Composable
fun ProfileNavGraph(
    modifier: Modifier = Modifier,
    authState: AuthUiState,
    onSaveProfile: (name: String, phone: String?, ageRange: String?) -> Unit,
    onUploadPhoto: (ByteArray) -> Unit,
    onClearError: () -> Unit,
    onResetProfileUpdated: () -> Unit,
    onLogout: () -> Unit,
    onHabitsClick: () -> Unit = {}
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = ProfileRoutes.MAIN,
        modifier = modifier
    ) {
        composable(route = ProfileRoutes.MAIN) { _: NavBackStackEntry ->
            ProfileScreen(
                user = authState.user,
                onEditProfile = { navController.navigate(ProfileRoutes.EDIT) },
                onHabits = onHabitsClick,
                onLogout = onLogout
            )
        }

        composable(route = ProfileRoutes.EDIT) { _: NavBackStackEntry ->
            EditProfileScreen(
                authState = authState,
                onSaveProfile = onSaveProfile,
                onUploadPhoto = onUploadPhoto,
                onClearError = onClearError,
                onResetProfileUpdated = onResetProfileUpdated,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
