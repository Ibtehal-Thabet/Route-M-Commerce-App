package com.example.domain.common

sealed class ResultWrapper<out T> {

    object Loading : ResultWrapper<Nothing>()

    data class Success<T>(val data : T, val message: String? = null, val numberOfCartItems: Int? = null) : ResultWrapper<T>()

    data class ServerError(val error:com.example.domain.exception.ServerError): ResultWrapper<Nothing>()

    data class Error(val error: Throwable):ResultWrapper<Nothing>()
}