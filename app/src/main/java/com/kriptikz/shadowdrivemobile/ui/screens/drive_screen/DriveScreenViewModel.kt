package com.kriptikz.shadowdrivemobile.ui.screens.drive_screen

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.getInstance
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
    savedStateHandle: SavedStateHandle,
    private val shadowDriveRepository: ShadowDriveRepository
) : ViewModel() {
    var driveUiState: DriveUiState by mutableStateOf(DriveUiState.Loading)
        private set
    private val drivePublicKey: String = checkNotNull(savedStateHandle["drivePublicKey"])

    init {
        getDriveFiles()
    }

    fun getDriveFiles() {
        viewModelScope.launch {
            driveUiState = try {
                val publicKey = drivePublicKey
                val results = shadowDriveRepository.getDriveFileNames(publicKey).keys.filter { fileName ->
                    fileName.endsWith(".jpg")
                }

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
                DriveScreenViewModel(savedStateHandle = SavedStateHandle(mapOf("drivePublicKey" to "t5Cp1F6VcoeXxqNC7TrmYCJofT9U7iEPbziY252tPnX")), shadowDriveRepository = shadowDriveRepository)
            }
        }
    }
}
