package com.example.domain.exception

import java.io.IOException

class ServerTimeOutException(message: String, ex:Throwable):IOException(message, ex)