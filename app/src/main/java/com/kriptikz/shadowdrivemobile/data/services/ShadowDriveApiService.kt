package com.kriptikz.shadowdrivemobile.data.services

import kotlinx.serialization.SerialName
import retrofit2.http.*

@kotlinx.serialization.Serializable
data class ListObjectsRequest(
    val storageAccount: String
)

@kotlinx.serialization.Serializable
data class StorageAccountInfoRequest(
    @SerialName(value = "storage_account")
    val storageAccount: String
)

interface ShadowDriveApiService {
    @Headers("Content-Type: application/json")
    @POST("/list-objects")
    suspend fun listObjects(
        @Body listObjectsRequest: ListObjectsRequest
    ): ShadowFileNames

    @Headers("Content-Type: application/json")
    @POST("/storage-account-info")
    suspend fun getStorageAccountInfo(
        @Body storageAccountInfoRequest: StorageAccountInfoRequest
    ): StorageAccountInfoResponse
}
