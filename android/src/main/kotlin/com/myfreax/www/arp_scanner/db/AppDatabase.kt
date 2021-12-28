package com.myfreax.www.arp_scanner.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MacVendor::class], version = 2, exportSchema = false)

abstract class AppDatabase : RoomDatabase() {
    abstract fun macVendorsDao(): MacVendorsDao

    companion object {
        @JvmStatic
        fun createInstance(context: Context): AppDatabase {
            return Room
                .databaseBuilder(
                    context,
                    AppDatabase::class.java, "www-myfreax-com"
                )
                .createFromAsset("mac_devices.db")
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}