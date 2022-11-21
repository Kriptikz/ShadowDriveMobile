package com.kriptikz.shadowdrivemobile.data.services

import retrofit2.http.POST

interface ShadowDriveApiService {
    @POST("/list-objects")
    suspend fun listObjects(): List<ShadowFileNames>
}