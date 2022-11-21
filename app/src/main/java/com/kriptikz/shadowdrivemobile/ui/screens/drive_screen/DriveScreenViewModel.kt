package com.kriptikz.shadowdrivemobile.ui.screens.drive_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kriptikz.shadowdrivemobile.data.services.ListObjectsRequest
import com.kriptikz.shadowdrivemobile.data.services.ShadowDriveApi
import com.kriptikz.shadowdrivemobile.data.services.ShadowFileNames
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface DriveUiState {
    data class Success(val fileNames: List<String>) : DriveUiState
    object Error : DriveUiState
    object Loading : DriveUiState
}

class DriveScreenViewModel : ViewModel() {
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
                val result = ShadowDriveApi.retrofitService.listObjects(ListObjectsRequest(storageAccount = publicKey))
                DriveUiState.Success(result.keys)
            } catch (e: IOException) {
                DriveUiState.Error
            }
        }
    }
}
