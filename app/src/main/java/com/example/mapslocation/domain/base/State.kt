package com.example.mapslocation.domain.base

sealed class State<out T : Any> {
    data class Success<out T : Any>(val data: T) : State<T>()

    data class Failure(
        val exception: Throwable,
        val message: String = exception.message ?: ErrorHandler.UNKNOWN_ERROR
    ) : State<Nothing>()

    data class Progress(val isLoading: Boolean) : State<Nothing>()
}

object ErrorHandler {
    private const val TAG = "ErrorHandler";
    private const val EMPTY_RESPONSE = "Server returned empty response."
    const val NETWORK_ERROR_MESSAGE = "Please check your internet connectivity and try again!"
    const val NO_SUCH_DATA = "Data not found in the database"
    const val UNKNOWN_ERROR = "An unknown error occurred!"
}