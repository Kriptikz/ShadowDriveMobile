package com.kriptikz.shadowdrivemobile.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kriptikz.shadowdrivemobile.data.daos.StorageAccountDao
import com.kriptikz.shadowdrivemobile.data.daos.WalletDao
import com.kriptikz.shadowdrivemobile.data.entities.StorageAccount
import com.kriptikz.shadowdrivemobile.data.entities.Wallet

@Database(entities = [(Wallet::class), (StorageAccount::class)], version = 1)
abstract class AppRoomDatabase: RoomDatabase() {

    abstract fun walletDao(): WalletDao
    abstract fun storageAccountDao(): StorageAccountDao

    companion object {
        private var INSTANCE: AppRoomDatabase? = null

        fun getInstance(context: Context): AppRoomDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppRoomDatabase::class.java,
                        "app_database"
                    ).fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }

                instance.openHelper.writableDatabase

                return instance
            }
        }
    }
}