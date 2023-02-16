package com.kriptikz.shadowdrivemobile.data.repositories

import com.kriptikz.shadowdrivemobile.data.entities.StorageAccount
import com.kriptikz.shadowdrivemobile.data.services.*

interface IShadowDriveRepository {
    suspend fun getDriveFileNames(storageAccount: String): ShadowFileNames
    suspend fun getStorageAccountInfo(storageAccount: String): StorageAccount
    suspend fun uploadFile(fileUri: String)
}

class ShadowDriveRepository(
    private val shadowDriveApiService: ShadowDriveApiService
) : IShadowDriveRepository {
    override suspend fun getDriveFileNames(storageAccount: String): ShadowFileNames {
        return shadowDriveApiService.listObjects(ListObjectsRequest(storageAccount = storageAccount))
    }

    override suspend fun getStorageAccountInfo(storageAccount: String): StorageAccount {
        val storageAccountInfo = shadowDriveApiService.getStorageAccountInfo(StorageAccountInfoRequest(storageAccount = storageAccount))

        return StorageAccount(
            address = storageAccount,
            reservedBytes = storageAccountInfo.reservedBytes,
            currentUsage = storageAccountInfo.currentUsage,
            identifier = storageAccountInfo.identifier,
            version = storageAccountInfo.version,
        )
    }

    override suspend fun uploadFile(fileUri: String) {
        TODO("Not yet implemented")
    }
}