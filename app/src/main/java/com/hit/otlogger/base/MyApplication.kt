package com.hit.otlogger.base

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.hit.otlogger.data.database.AppDatabase
import com.hit.otlogger.data.database.AppRepository
import com.hit.otlogger.util.isDebugMode

class MyApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { AppRepository(database.dao()) }

    override fun onCreate() {
        super.onCreate()

        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = !isDebugMode()
    }
}