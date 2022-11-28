package com.kriptikz.shadowdrivemobile

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.GuardedBy
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kriptikz.shadowdrivemobile.ui.ShadowDriveMobileApp
import com.kriptikz.shadowdrivemobile.ui.screens.drive_screen.DriveScreenViewModel
import com.kriptikz.shadowdrivemobile.ui.screens.home_screen.HomeScreenViewModel
import com.kriptikz.shadowdrivemobile.ui.theme.ShadowDriveMobileTheme

class MainActivity : ComponentActivity() {

    private val activityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            intentSender.onActivityComplete()
        }

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
                    driveScreenViewModel = driveScreenViewModel,
                    intentSender = intentSender,
                )
            }
        }
    }

    private val intentSender = object : HomeScreenViewModel.StartActivityForResultSender {
        @GuardedBy("this")
        private var callback: (() -> Unit)? = null

        override fun startActivityForResult(
            intent: Intent,
            onActivityCompleteCallback: () -> Unit
        ) {
            synchronized(this) {
                check(callback == null) { "Received an activity start request while another is pending" }
                callback = onActivityCompleteCallback
            }
            activityResultLauncher.launch(intent)
        }

        fun onActivityComplete() {
            synchronized(this) {
                callback?.let { it() }
                callback = null
            }
        }
    }

}

