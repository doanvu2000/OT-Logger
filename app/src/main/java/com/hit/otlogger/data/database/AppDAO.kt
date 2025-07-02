package com.hit.otlogger.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hit.otlogger.data.model.OTModel
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDAO {
    @Query("SELECT * FROM ot_table")
    fun getAllData(): Flow<List<OTModel>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertData(otModel: OTModel): Long

    @Update
    suspend fun updateData(otModel: OTModel)

    @Delete
    suspend fun deleteData(otModel: OTModel)
}