package com.kriptikz.shadowdrivemobile.data.services

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import retrofit2.Retrofit
import retrofit2.http.*
import okhttp3.MediaType.Companion.toMediaType

@kotlinx.serialization.Serializable
data class ListObjectsRequest(
    val storageAccount: String?
)


private const val BASE_URL =
    "https://shadow-storage.genesysgo.net"

@OptIn(ExperimentalSerializationApi::class)
private val retrofit = Retrofit.Builder()
    .addConverterFactory(Json.asConverterFactory(contentType = "application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .build()

interface ShadowDriveApiService {
    @Headers("Content-Type: application/json")
    @POST("/list-objects")
    suspend fun listObjects(
        @Body storageAccount: ListObjectsRequest
    ): ShadowFileNames
}

object ShadowDriveApi {
    val retrofitService : ShadowDriveApiService by lazy {
        retrofit.create(ShadowDriveApiService::class.java)
    }
}
