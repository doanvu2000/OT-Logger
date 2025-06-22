package com.hit.otlogger.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hit.otlogger.data.model.OTModel

const val DATABASE_NAME = "ot_logger_database"

@Database(entities = [OTModel::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): AppDAO

    companion object {
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, AppDatabase::class.java, DATABASE_NAME
                ).allowMainThreadQueries().build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}