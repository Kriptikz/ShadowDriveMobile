package com.kriptikz.shadowdrivemobile.data.services

import retrofit2.http.*

@kotlinx.serialization.Serializable
data class ListObjectsRequest(
    val storageAccount: String?
)

interface ShadowDriveApiService {
    @Headers("Content-Type: application/json")
    @POST("/list-objects")
    suspend fun listObjects(
        @Body listObjectsRequest: ListObjectsRequest
    ): ShadowFileNames
}
