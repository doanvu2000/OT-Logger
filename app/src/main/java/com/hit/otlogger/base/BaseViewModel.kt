package com.hit.otlogger.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hit.otlogger.data.enumz.LoadingState
import com.hit.otlogger.util.Constants
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

open class BaseViewModel : ViewModel() {

    companion object {
        const val TAG = Constants.TAG
    }

    private val _loadingState: MutableStateFlow<LoadingState> = MutableStateFlow(LoadingState.Init)
    val loadingState: StateFlow<LoadingState> = _loadingState.asStateFlow()

    private var onExceptionHandler: ((throwable: Throwable) -> Unit)? = null
    fun setOnExceptionHandler(handler: (throwable: Throwable) -> Unit) {
        onExceptionHandler = handler
    }

    /**catch exception when using coroutine*/
    val exceptionHandler = CoroutineExceptionHandler { context, throwable ->
        _loadingState.value = LoadingState.Failed
        throwable.printStackTrace()
        handleException(throwable)
    }

    open fun handleException(throwable: Throwable) {
        //handle exception
        onExceptionHandler?.invoke(throwable)
    }

    fun launchSafe(
        context: CoroutineContext = EmptyCoroutineContext, block: suspend CoroutineScope.() -> Unit
    ) {
        viewModelScope.launch(context + exceptionHandler) { block() }
    }

    fun updateLoadingState(state: LoadingState) {
        launchSafe {
            _loadingState.value = state
        }
    }

    fun setStateIsLoading() {
        updateLoadingState(LoadingState.Loading)
    }

    fun setStateIsSuccess() {
        updateLoadingState(LoadingState.Success)
    }

    fun setStateIsFailed() {
        updateLoadingState(LoadingState.Failed)
    }

    fun <T> sendEvent(channel: Channel<T>, value: T) {
        launchSafe {
            channel.send(value)
        }
    }
}