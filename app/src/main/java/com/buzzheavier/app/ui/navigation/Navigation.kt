package com.buzzheavier.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.buzzheavier.app.data.repository.BuzzHeavierRepository
import com.buzzheavier.app.ui.screens.FileManagerScreen
import com.buzzheavier.app.ui.screens.FileManagerViewModel
import com.buzzheavier.app.ui.screens.LoginScreen
import com.buzzheavier.app.ui.screens.LoginViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object FileManager : Screen("fileManager/{directoryId}") {
        fun createRoute(directoryId: String?) = "fileManager/${directoryId ?: "root"}"
    }
}

@Composable
fun BuzzHeavierNavigation(
    navController: NavHostController = rememberNavController(),
    repository: BuzzHeavierRepository
) {
    val context = LocalContext.current
    val loginViewModel: LoginViewModel = viewModel(
        factory = LoginViewModel.Factory(repository)
    )
    
    val isLoggedIn by loginViewModel.isLoggedIn.collectAsState()
    
    val startDestination = if (isLoggedIn) {
        Screen.FileManager.createRoute(null)
    } else {
        Screen.Login.route
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.FileManager.createRoute(null)) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.FileManager.route) { backStackEntry ->
            val directoryId = backStackEntry.arguments?.getString("directoryId")
            val fileManagerViewModel: FileManagerViewModel = viewModel(
                factory = FileManagerViewModel.Factory(repository)
            )
            FileManagerScreen(
                viewModel = fileManagerViewModel,
                directoryId = if (directoryId == "root") null else directoryId,
                onNavigateToDirectory = { newDirectoryId ->
                    navController.navigate(Screen.FileManager.createRoute(newDirectoryId))
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
