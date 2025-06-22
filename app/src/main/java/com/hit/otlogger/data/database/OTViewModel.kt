package com.hit.otlogger.data.database

import com.hit.otlogger.base.BaseViewModel
import com.hit.otlogger.data.model.OTModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class OTViewModel(private val repository: AppRepository) : BaseViewModel() {
    companion object {
        const val NAME_SUFFIX = "Guess Challenge"
    }

    suspend fun Any.await(): Any {
        return suspendCoroutine {
            it.resume(this)
        }
    }

    private val _allData = MutableStateFlow<List<OTModel>>(emptyList())
    val allData: Flow<List<OTModel>> = _allData.asStateFlow()

    fun getAllData() {
        launchSafe {
            _allData.update {
                repository.getAllData()
            }
        }
    }

    fun insertData(entity: OTModel) {
        launchSafe(Dispatchers.IO) {
            repository.insertGallery(entity)
        }
    }

    fun deleteData(entity: OTModel) {
        launchSafe(Dispatchers.IO) {
            repository.deleteGallery(entity)
        }
    }

    sealed class Event {
        data object ClickAdd : Event()
    }

    private val eventChannel = Channel<Event>()
    val event = eventChannel.receiveAsFlow()

    fun clickAdd() {
        sendEvent(eventChannel, Event.ClickAdd)
    }
}