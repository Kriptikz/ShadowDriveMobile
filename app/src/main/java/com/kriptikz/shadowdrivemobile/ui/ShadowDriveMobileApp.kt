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
//    DriveScreen(
//        driveUiState = driveScreenViewModel.driveUiState
//    )

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "homeScreen") {
        composable("homeScreen") {
            HomeScreen(
                homeUiState = homeScreenViewModel.homeUiState,
                navController = navController,
                onNavigateToDrive = { drivePublicKey: String ->
                    println("DRIVE PUBLIC KEY: ${drivePublicKey}")
                    navController.navigate("driveScreen/9dyf8t2ia2QwerZsaM7DeZPovgA32Joyzw2kdYmRcoto")
                }
            )
        }
        composable(
            route = "driveScreen/{drivePublicKey}",
            arguments = listOf(navArgument("drivePublicKey") { defaultValue = "t5Cp1F6VcoeXxqNC7TrmYCJofT9U7iEPbziY252tPnX" })
        ) {
            DriveScreen(
                driveUiState = driveScreenViewModel.driveUiState,
                it.arguments?.getString("drivePublicKey")
            )
        }
    }

}
