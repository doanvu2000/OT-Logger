package com.hit.otlogger.util

import com.hit.otlogger.BuildConfig

fun isDebugMode() = BuildConfig.DEBUG

fun now() = System.currentTimeMillis()

fun tryCatch(tryBlock: () -> Unit, catchBlock: ((e: Exception) -> Unit)? = null) {
    try {
        tryBlock()
    } catch (e: Exception) {
        catchBlock?.invoke(e)
    }
}