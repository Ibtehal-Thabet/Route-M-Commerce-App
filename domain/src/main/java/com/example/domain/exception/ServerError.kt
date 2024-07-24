package com.example.domain.exception

data class ServerError(
    val status: String? = null,
    val serverMessage: String? = null,
    val statusCode: Int? = null,
    val ex: Throwable? = null
    ) : Throwable(serverMessage, ex)