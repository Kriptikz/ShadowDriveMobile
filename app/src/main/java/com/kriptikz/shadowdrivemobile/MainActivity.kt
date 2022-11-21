package com.kriptikz.shadowdrivemobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.kriptikz.shadowdrivemobile.ui.ShadowDriveMobileApp
import com.kriptikz.shadowdrivemobile.ui.theme.ShadowDriveMobileTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShadowDriveMobileTheme {
                // create a view model and pass it on here
                ShadowDriveMobileApp()
            }
        }
    }
}

