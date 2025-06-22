package com.hit.otlogger.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ot_table")
data class OTModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: Long,
    val timeStart: Long,
    val timeEnd: Long
)
