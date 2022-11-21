package com.kriptikz.shadowdrivemobile.ui.screens.drive_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kriptikz.shadowdrivemobile.ui.screens.SDMScaffold
import com.kriptikz.shadowdrivemobile.ui.theme.ShadowDriveMobileTheme
import com.kriptikz.shadowdrivemobile.R

@Composable
fun DriveScreen(
    driveUiState: DriveUiState,
    modifier: Modifier = Modifier
) {
    SDMScaffold(title = "Drives") {
        when (driveUiState) {
            is DriveUiState.Loading -> LoadingScreen(modifier)
            is DriveUiState.Success -> ResultScreen(fileNames = driveUiState.fileNames, modifier = modifier)
            is DriveUiState.Error -> ErrorScreen(modifier)
        }
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            modifier = Modifier.size(200.dp),
            painter = painterResource(id = R.drawable.loading_img),
            contentDescription = stringResource(id = R.string.loading)
        )
    }
}

@Composable
fun ErrorScreen(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Text(stringResource(id = R.string.loading_failed))
    }
}

@Preview(showBackground = true)
@Composable
fun DriveScreenPreview() {
    ShadowDriveMobileTheme {
        DriveScreen(DriveUiState.Success(listOf("Some State")))
    }
}

@Composable
fun ResultScreen(fileNames: List<String>, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
      Text(text = fileNames.toString())
    }

}
