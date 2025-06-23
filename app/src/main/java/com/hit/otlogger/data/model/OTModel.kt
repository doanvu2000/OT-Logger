package com.hit.otlogger.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ot_table")
data class OTModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val day: Int = 0,
    val month: Int = 0,
    val year: Int = 0,
    val hourStart: Int = 0,
    val minutesStart: Int = 0,
    val hourEnd: Int = 0,
    val minutesEnd: Int = 0
) {
    fun getTimeStart(): String {
        return String.format("%02d:%02d", hourStart, minutesStart)
    }

    fun getTimeEnd(): String {
        return String.format("%02d:%02d", hourEnd, minutesEnd)
    }
}
