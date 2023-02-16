package com.kriptikz.shadowdrivemobile.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kriptikz.shadowdrivemobile.data.entities.StorageAccount

@Dao
interface StorageAccountDao {
    @Insert
    fun insertStorageAccount(storageAccount: StorageAccount)

    @Query("DELETE FROM storageAccounts WHERE address = :address")
    fun deleteStorageAccount(address: String)

    @Query("DELETE FROM storageAccounts")
    fun deleteAllStorageAccounts()

    @Query("SELECT * FROM storageAccounts WHERE address = :address")
    fun findStorageAccountByAddress(address: String): List<StorageAccount>

    @Query("SELECT * FROM storageAccounts WHERE identifier = :identifier")
    fun findStorageAccountByIdentifier(identifier: String): List<StorageAccount>

    @Query("SELECT * FROM storageAccounts")
    fun getAllStorageAccounts(): List<StorageAccount>
}
