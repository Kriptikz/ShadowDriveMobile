package com.kriptikz.shadowdrivemobile.data.repositories

import com.kriptikz.shadowdrivemobile.data.daos.StorageAccountDao
import com.kriptikz.shadowdrivemobile.data.entities.StorageAccount
import com.kriptikz.shadowdrivemobile.data.entities.Wallet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StorageAccountRepository(
    private val storageAccountDao: StorageAccountDao
) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun insertStorageAccount(storageAccount: StorageAccount) {
        coroutineScope.launch(Dispatchers.IO) {
            storageAccountDao.insertStorageAccount(storageAccount)
        }
    }

    suspend fun findStorageAccountByAddress(address: String): List<StorageAccount> {
        return storageAccountDao.findStorageAccountByAddress(address)
    }

    suspend fun findStorageAccountByIdentifier(identifier: String): List<StorageAccount> {
        return storageAccountDao.findStorageAccountByIdentifier(identifier)
    }

    suspend fun getAllStorageAccounts(): List<StorageAccount> {
        return storageAccountDao.getAllStorageAccounts()
    }

    fun deleteStorageAccount(address: String) {
        coroutineScope.launch(Dispatchers.IO) {
            storageAccountDao.deleteStorageAccount(address)
        }
    }

    fun deleteAllStorageAccounts() {
        coroutineScope.launch(Dispatchers.IO) {
            storageAccountDao.deleteAllStorageAccounts()
        }
    }
}