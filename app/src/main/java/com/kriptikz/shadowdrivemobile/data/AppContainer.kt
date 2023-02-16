package com.kriptikz.shadowdrivemobile.data

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.kriptikz.shadowdrivemobile.data.daos.WalletDao
import com.kriptikz.shadowdrivemobile.data.repositories.ISolanaRepository
import com.kriptikz.shadowdrivemobile.data.repositories.ShadowDriveRepository
import com.kriptikz.shadowdrivemobile.data.repositories.SolanaRepository
import com.kriptikz.shadowdrivemobile.data.repositories.WalletRepository
import com.kriptikz.shadowdrivemobile.data.services.ShadowDriveApiService
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

interface AppContainer {
    val shadowDriveRepository: ShadowDriveRepository
    val solanaRepository: ISolanaRepository
}

@OptIn(ExperimentalSerializationApi::class)
class DefaultAppContainer: AppContainer {

    override val solanaRepository: ISolanaRepository by lazy {
        SolanaRepository()
    }

    private val BASE_URL =
        "https://shadow-storage.genesysgo.net"

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(Json{ignoreUnknownKeys = true}.asConverterFactory(contentType = "application/json".toMediaType()))
        .baseUrl(BASE_URL)
        .build()

    private val retrofitService : ShadowDriveApiService by lazy {
        retrofit.create(ShadowDriveApiService::class.java)
    }

    override val shadowDriveRepository: ShadowDriveRepository by lazy {
        ShadowDriveRepository(retrofitService)
    }

}