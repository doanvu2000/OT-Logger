package com.hit.otlogger.data.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hit.otlogger.data.model.OTModel

const val DATABASE_NAME = "ot_logger_database_1"

@Database(
    entities = [OTModel::class],
    version = 3,
    exportSchema = true,
    autoMigrations = [AutoMigration(from = 1, to = 3)]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): AppDAO

    companion object {
        // app/src/main/java/com/example/data/Migrations.kt
        val MIGRATION_1_3 = object : Migration(1, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // thêm cột description với NOT NULL và default là chuỗi rỗng
                db.execSQL(
                    """
      ALTER TABLE ot_table
      ADD COLUMN description TEXT NOT NULL DEFAULT ''
    """.trimIndent()
                )
            }
        }

        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, AppDatabase::class.java, DATABASE_NAME
                ).addMigrations(MIGRATION_1_3)
                    .allowMainThreadQueries().build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}