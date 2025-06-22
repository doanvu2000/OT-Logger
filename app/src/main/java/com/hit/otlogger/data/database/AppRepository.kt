package com.hit.otlogger.data.database

import com.hit.otlogger.data.model.OTModel


class AppRepository(private val appDao: AppDAO) {
    suspend fun getAllData() = appDao.getAllData()

    suspend fun insertGallery(otModel: OTModel) {
        appDao.insertData(otModel)
    }

    suspend fun deleteGallery(otModel: OTModel) {
        appDao.deleteData(otModel)
    }
}