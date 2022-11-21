package com.kriptikz.shadowdrivemobile

import android.app.Application
import com.kriptikz.shadowdrivemobile.data.DefaultAppContainer
import com.kriptikz.shadowdrivemobile.data.AppContainer

class ShadowDriveMobileApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}