package com.kriptikz.shadowdrivemobile.ui.screens.drive_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class DriveScreenViewModel : ViewModel() {
    var driveUiState: String by mutableStateOf("")
        private set


    init {
        getDriveFiles()
    }

    fun getDriveFiles() {
        driveUiState = "Set the api response here!"
    }
}