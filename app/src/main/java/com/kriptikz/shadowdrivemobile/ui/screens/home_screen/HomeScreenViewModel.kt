package com.kriptikz.shadowdrivemobile.ui.screens.home_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class HomeUiState(
    val usedStorage: Double,
    val totalStorage: Double,
    val drives: List<String>,
    val recentItems: List<RecentItem>
)

class HomeScreenViewModel : ViewModel() {
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
            )
        ))
        private set


    init {
        getDefaultState()
    }

    fun getDefaultState() {
        homeUiState = HomeUiState(
            usedStorage = 14.5,
            totalStorage = 40.0,
            drives = listOf("Photos", "Videos", "Pictures", "Temporary", "AnotherOne", "Ok"),
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
            )
        )
    }
}
