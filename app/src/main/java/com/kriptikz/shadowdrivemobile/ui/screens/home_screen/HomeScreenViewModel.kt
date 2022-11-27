package com.kriptikz.shadowdrivemobile.ui.screens.home_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kriptikz.shadowdrivemobile.ShadowDriveMobileApplication
import com.kriptikz.shadowdrivemobile.data.repositories.ISolanaRepository
import kotlinx.coroutines.launch

data class HomeUiState(
    val usedStorage: Double,
    val totalStorage: Double,
    val drives: List<String>,
    val recentItems: List<RecentItem>,
)

class HomeScreenViewModel(
    private val solanaRepository: ISolanaRepository
) : ViewModel() {
    var homeUiState: HomeUiState by mutableStateOf(HomeUiState(
            usedStorage = 14.5,
            totalStorage = 40.0,
            drives = listOf("Photos", "Videos", "Pictures", "Temporary", "AnotherOne", "Ok"),
            recentItems = listOf(
                RecentItem(
                    name = "picture.jpg",
                    day = "yesterday",
                    size = "420.0GB",
                ),
            ),
        ))
        private set


    init {
        getDefaultState()
    }

    fun getDefaultState() {
        viewModelScope.launch {

            val drives = solanaRepository.getAllStorageAccounts("6HE1JdihWH71nT18CPUCghahVLRqAGYkmBJWZAPE3ggU")


            homeUiState = HomeUiState(
                usedStorage = 14.5,
                totalStorage = 40.0,
                drives = drives,
                recentItems = listOf(
                    RecentItem(
                        name = "file.txt",
                        day = "today",
                        size = "10.0MB",
                    ),
                    RecentItem(
                        name = "file.txt",
                        day = "today",
                        size = "10.0MB",
                    ),
                    RecentItem(
                        name = "file.txt",
                        day = "today",
                        size = "10.0MB",
                    ),
                    RecentItem(
                        name = "file.txt",
                        day = "today",
                        size = "10.0MB",
                    ),
                    RecentItem(
                        name = "file.txt",
                        day = "today",
                        size = "10.0MB",
                    ),
                    RecentItem(
                        name = "omarsName.txt",
                        day = "today",
                        size = "10.0GB",
                    ),
                    RecentItem(
                        name = "picture.png",
                        day = "yesterday",
                        size = "420.0MB",
                    ),
                    RecentItem(
                        name = "picture.jpg",
                        day = "yesterday",
                        size = "420.0GB",
                    ),
                ),
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ShadowDriveMobileApplication)
                val solanaRepository = application.container.solanaRepository
                HomeScreenViewModel(solanaRepository = solanaRepository)
            }
        }
    }
}
