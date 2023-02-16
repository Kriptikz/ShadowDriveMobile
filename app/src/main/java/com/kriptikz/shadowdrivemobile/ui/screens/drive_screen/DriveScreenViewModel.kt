package com.kriptikz.shadowdrivemobile.ui.screens.drive_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kriptikz.shadowdrivemobile.ShadowDriveMobileApplication
import com.kriptikz.shadowdrivemobile.data.repositories.ShadowDriveRepository
import kotlinx.coroutines.*
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

    var drivePk by mutableStateOf("t5Cp1F6VcoeXxqNC7TrmYCJofT9U7iEPbziY252tPnX")

    init {
        getDriveFiles(drivePk)
        getStorageAccountInfo(drivePk)
    }

    private fun getStorageAccountInfo(publicKey: String) {
        viewModelScope.launch {
            try {
                val storageAccount = shadowDriveRepository.getStorageAccountInfo(publicKey)
                println("LOGC: Storage Account Info ${storageAccount.identifier}")
            } catch (e: IOException) {
                println("LOGC: ERROR")
            }
        }
    }

    fun getDriveFiles(publicKey: String) {
        driveUiState = DriveUiState.Loading
        viewModelScope.launch {
            driveUiState = try {
                val results = shadowDriveRepository.getDriveFileNames(publicKey).keys

                val baseUrl = "https://shdw-drive.genesysgo.net/"
                val urls = results.map { fileName -> "$baseUrl$publicKey/$fileName" }

                DriveUiState.Success(urls)
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
