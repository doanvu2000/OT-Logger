package com.hit.otlogger.data.database

import android.app.Activity
import android.app.Application
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hit.otlogger.base.MyApplication

class AppViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OTViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OTViewModel(repository) as T
        }
        throw kotlin.IllegalArgumentException("Unknown ViewModel class")
    }
}

fun getViewModelFactory(application: Application): AppViewModelFactory {
    return AppViewModelFactory((application as MyApplication).repository)
}

fun Activity.getViewModelFactory(): AppViewModelFactory {
    return getViewModelFactory(application)
}

fun Fragment.getViewModelFactory(): AppViewModelFactory {
    return getViewModelFactory(requireActivity().application)
}