package com.kriptikz.shadowdrivemobile.ui.screens.drive_screen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kriptikz.shadowdrivemobile.ui.theme.ShadowDriveMobileTheme

@Composable
fun DriveScreen() {
    Text(text = "This text")
}


@Preview(showBackground = true)
@Composable
fun DriveScreenPreview() {
    ShadowDriveMobileTheme {
        DriveScreen()
    }
}