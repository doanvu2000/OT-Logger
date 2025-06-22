package com.hit.otlogger.data.database

import androidx.lifecycle.asFlow
import com.hit.otlogger.base.BaseViewModel
import com.hit.otlogger.data.model.OTModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
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

    val allData: Flow<List<OTModel>> = repository.allData.asFlow()

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
        data object ClickChooseMonth : Event()
        data object ClickCopy : Event()
    }

    private val eventChannel = Channel<Event>()
    val event = eventChannel.receiveAsFlow()

    fun clickAdd() {
        sendEvent(eventChannel, Event.ClickAdd)
    }

    fun clickChooseMonth() {
        sendEvent(eventChannel, Event.ClickChooseMonth)
    }

    fun clickCopy() {
        sendEvent(eventChannel, Event.ClickCopy)
    }
}