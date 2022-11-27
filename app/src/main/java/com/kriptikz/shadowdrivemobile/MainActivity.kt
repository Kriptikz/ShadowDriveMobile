package com.kriptikz.shadowdrivemobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kriptikz.shadowdrivemobile.ui.ShadowDriveMobileApp
import com.kriptikz.shadowdrivemobile.ui.screens.drive_screen.DriveScreenViewModel
import com.kriptikz.shadowdrivemobile.ui.screens.home_screen.HomeScreenViewModel
import com.kriptikz.shadowdrivemobile.ui.theme.ShadowDriveMobileTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShadowDriveMobileTheme {
                // create a view model and pass it on here
                val driveScreenViewModel: DriveScreenViewModel =
                    viewModel(factory = DriveScreenViewModel.Factory)
                val homeScreenViewModel: HomeScreenViewModel =
                    viewModel(factory = HomeScreenViewModel.Factory)
                ShadowDriveMobileApp(
                    homeScreenViewModel = homeScreenViewModel,
                    driveScreenViewModel = driveScreenViewModel
                )
            }
        }
    }
}

