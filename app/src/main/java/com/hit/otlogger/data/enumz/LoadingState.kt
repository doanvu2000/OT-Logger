package com.hit.otlogger.data.enumz

sealed class LoadingState {
    object Init : LoadingState()
    object Loading : LoadingState()
    object Success : LoadingState()
    object Failed : LoadingState()
}