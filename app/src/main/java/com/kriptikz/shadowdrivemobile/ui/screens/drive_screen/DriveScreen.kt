package com.kriptikz.shadowdrivemobile.ui.screens.drive_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kriptikz.shadowdrivemobile.ui.screens.SDMScaffold
import com.kriptikz.shadowdrivemobile.ui.theme.ShadowDriveMobileTheme

@Composable
fun DriveScreen(
    driveUiState: String,
    modifier: Modifier = Modifier
) {
    SDMScaffold(title = "Drives") {
        ResultScreen(driveUiState = driveUiState, modifier = modifier)
    }
}


@Preview(showBackground = true)
@Composable
fun DriveScreenPreview() {
    ShadowDriveMobileTheme {
        DriveScreen("Some State")
    }
}

@Composable
fun ResultScreen(driveUiState: String, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
      Text(text = driveUiState)
    }

}