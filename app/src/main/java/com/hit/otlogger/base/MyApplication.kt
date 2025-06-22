package com.hit.otlogger.base

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.hit.otlogger.util.isDebugMode

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = !isDebugMode()
    }
}