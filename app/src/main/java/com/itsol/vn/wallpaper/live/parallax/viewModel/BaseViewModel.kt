package com.itsol.vn.wallpaper.live.parallax.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

open class BaseViewModel: ViewModel() {

    val isLoading by lazy { MutableStateFlow(false) }

    // error message
    val errorMessage by lazy { MutableSharedFlow<String>() }


    private val isShowSwipeIntro by lazy { MutableStateFlow(false) }
    // optional flags
    val noInternetConnectionEvent by lazy { MutableSharedFlow<Unit>() }
    val connectTimeoutEvent by lazy { MutableSharedFlow<Unit>() }
    val unknownErrorEvent by lazy { MutableSharedFlow<Unit>() }

    private val exceptionHandler by lazy {
        CoroutineExceptionHandler { context, throwable ->
            viewModelScope.launch {
                onError(throwable)
            }
        }
    }
    protected val viewModelScopeExceptionHandler by lazy { viewModelScope + exceptionHandler }

    protected open suspend fun onError(throwable: Throwable) {
        when (throwable) {
            // case no internet connection
            is UnknownHostException -> {
                noInternetConnectionEvent.emit(Unit)
            }

            is ConnectException -> {
                noInternetConnectionEvent.emit(Unit)
            }
            // case request time out
            is SocketTimeoutException -> {
                connectTimeoutEvent.emit(Unit)
            }

            else -> unknownErrorEvent.emit(Unit)

        }
        hideLoading()
    }
    open suspend fun showError(e: Throwable) {
        errorMessage.emit(e.message ?: "")
    }

    fun showLoading() {
        isLoading.value = true
    }

    fun hideLoading() {
        isLoading.value = false
    }
}