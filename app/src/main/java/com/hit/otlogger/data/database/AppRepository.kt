package com.hit.otlogger.data.database

import androidx.lifecycle.asLiveData
import com.hit.otlogger.data.model.OTModel


class AppRepository(private val appDao: AppDAO) {
    val allData = appDao.getAllData().asLiveData()

    suspend fun insertGallery(otModel: OTModel) {
        appDao.insertData(otModel)
    }

    suspend fun updateGallery(otModel: OTModel) {
        appDao.updateData(otModel)
    }

    suspend fun deleteGallery(otModel: OTModel) {
        appDao.deleteData(otModel)
    }
}