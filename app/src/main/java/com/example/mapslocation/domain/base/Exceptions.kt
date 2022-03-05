package com.example.mapslocation.domain.base

class NoResponseException(message: String? = ErrorHandler.UNKNOWN_ERROR) : Exception(message)

class NoDataException(message: String? = ErrorHandler.NO_SUCH_DATA) : Exception(message)

class NetworkConnectionException(message: String? = ErrorHandler.NETWORK_ERROR_MESSAGE) : Exception(message)