package com.kriptikz.shadowdrivemobile.ui.screens.drive_screen

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.getInstance
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kriptikz.shadowdrivemobile.ShadowDriveMobileApplication
import com.kriptikz.shadowdrivemobile.data.repositories.ShadowDriveRepository
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface DriveUiState {
    data class Success(val fileNames: List<String>) : DriveUiState
    object Error : DriveUiState
    object Loading : DriveUiState
}

class DriveScreenViewModel(
    private val shadowDriveRepository: ShadowDriveRepository
) : ViewModel() {
    var driveUiState: DriveUiState by mutableStateOf(DriveUiState.Loading)
        private set


    init {
        getDriveFiles()
    }

    fun getDriveFiles() {
//        driveUiState = "Set the api response here!"
        viewModelScope.launch {
            driveUiState = try {
                val publicKey = "t5Cp1F6VcoeXxqNC7TrmYCJofT9U7iEPbziY252tPnX"
                val result = shadowDriveRepository.getDriveFileNames(publicKey)
                DriveUiState.Success(result.keys)
            } catch (e: IOException) {
                DriveUiState.Error
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as ShadowDriveMobileApplication)
                val shadowDriveRepository = application.container.shadowDriveRepository
                DriveScreenViewModel(shadowDriveRepository = shadowDriveRepository)
            }
        }
    }
}
