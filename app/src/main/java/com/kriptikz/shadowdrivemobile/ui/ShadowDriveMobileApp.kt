package com.kriptikz.shadowdrivemobile.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kriptikz.shadowdrivemobile.ui.screens.drive_screen.DriveScreen
import com.kriptikz.shadowdrivemobile.ui.screens.drive_screen.DriveScreenViewModel
import com.kriptikz.shadowdrivemobile.ui.screens.home_screen.HomeScreen
import com.kriptikz.shadowdrivemobile.ui.screens.home_screen.HomeScreenViewModel

@Composable
fun ShadowDriveMobileApp(driveScreenViewModel: DriveScreenViewModel, modifier: Modifier = Modifier) {
    DriveScreen(
        driveUiState = driveScreenViewModel.driveUiState
    )

//    val homeScreenViewModel: HomeScreenViewModel = viewModel()
//    HomeScreen(
//        homeUiState = homeScreenViewModel.homeUiState
//    )
}
