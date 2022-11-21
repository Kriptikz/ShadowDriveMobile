package com.kriptikz.shadowdrivemobile.data.repositories

import com.kriptikz.shadowdrivemobile.data.services.ListObjectsRequest
import com.kriptikz.shadowdrivemobile.data.services.ShadowDriveApiService
import com.kriptikz.shadowdrivemobile.data.services.ShadowFileNames

interface IShadowDriveRepository {
    suspend fun getDriveFileNames(storageAccount: String): ShadowFileNames
}

class ShadowDriveRepository(
    private val shadowDriveApiService: ShadowDriveApiService
) : IShadowDriveRepository {
    override suspend fun getDriveFileNames(storageAccount: String): ShadowFileNames {
        return shadowDriveApiService.listObjects(ListObjectsRequest(storageAccount = storageAccount))
    }
}