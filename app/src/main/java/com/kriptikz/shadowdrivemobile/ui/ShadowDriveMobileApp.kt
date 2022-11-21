package com.kriptikz.shadowdrivemobile.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kriptikz.shadowdrivemobile.ui.screens.drive_screen.DriveScreen
import com.kriptikz.shadowdrivemobile.ui.screens.drive_screen.DriveScreenViewModel

@Composable
fun ShadowDriveMobileApp(modifier: Modifier = Modifier) {
    val driveScreenViewModel: DriveScreenViewModel = viewModel()
    DriveScreen(
        driveUiState = driveScreenViewModel.driveUiState
    )
//    HomeScreen()

}