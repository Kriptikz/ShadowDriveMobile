package com.kriptikz.shadowdrivemobile.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kriptikz.shadowdrivemobile.ui.screens.drive_screen.DriveScreen
import com.kriptikz.shadowdrivemobile.ui.screens.drive_screen.DriveScreenViewModel
import com.kriptikz.shadowdrivemobile.ui.screens.home_screen.HomeScreen
import com.kriptikz.shadowdrivemobile.ui.screens.home_screen.HomeScreenViewModel

@Composable
fun ShadowDriveMobileApp(
    homeScreenViewModel: HomeScreenViewModel,
    driveScreenViewModel: DriveScreenViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "homeScreen") {
        composable("homeScreen") {
            HomeScreen(
                homeUiState = homeScreenViewModel.homeUiState,
                onNavigateToDrive = { drivePublicKey: String ->
                    navController.navigate("driveScreen/${drivePublicKey}")
                    driveScreenViewModel.getDriveFiles(drivePublicKey)
                }
            )
        }
        composable(
            route = "driveScreen/{drivePublicKey}",
        ) {
            DriveScreen(
                driveUiState = driveScreenViewModel.driveUiState,
            )
        }
    }

}
