package com.example.diplomaapplication.databases.medicines_database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Medicine::class], version = 4)
abstract class MedicinesDatabase : RoomDatabase() {

    abstract fun medicineDao(): MedicineDao

    companion object {
        @Volatile
        private var instance: MedicinesDatabase? = null

        fun getDatabase(context: Context): MedicinesDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): MedicinesDatabase {
            fun destroyInstance() {
                instance = null
            }
            return Room.databaseBuilder(
                context.applicationContext,
                MedicinesDatabase::class.java,
                "medicines_database"
            ).fallbackToDestructiveMigration().build()
        }


        fun clearDatabase(context: Context) {
            getDatabase(context).clearAllTables()
        }
    }

}
