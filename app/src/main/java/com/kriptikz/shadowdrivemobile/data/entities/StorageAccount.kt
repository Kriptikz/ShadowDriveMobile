package com.kriptikz.shadowdrivemobile.data.entities

import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "storageAccounts")
class StorageAccount {
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "address")
    lateinit var address: String
    var reservedBytes: Int = 0
    var currentUsage: Int = 0

    @ColumnInfo(name = "identifier")
    var identifier: String = ""

    @ColumnInfo(name = "version")
    var version: String = ""

    constructor()

    constructor(
        address: String,
        reservedBytes: Int,
        currentUsage: Int,
        identifier: String,
        version: String
    ) {
        this.address = address
        this.reservedBytes = reservedBytes
        this.currentUsage = currentUsage
        this.identifier = identifier
        this.version = version
    }
}
