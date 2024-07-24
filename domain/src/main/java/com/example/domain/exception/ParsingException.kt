package com.example.domain.exception

class ParsingException(e: Throwable): Throwable(e.message, e)