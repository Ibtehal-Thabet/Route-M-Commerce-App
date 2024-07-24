package com.example.data

import android.util.Log
import com.example.data.model.BaseResponse
import com.example.data.model.user.UserResponse
import com.example.domain.common.ResultWrapper
import com.example.domain.exception.ParsingException
import com.example.domain.exception.ServerError
import com.example.domain.exception.ServerTimeOutException
import com.example.domain.model.User
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import java.net.SocketTimeoutException


suspend fun <T> safeApiCall(apiCall : suspend ()-> BaseResponse<T>) : Flow<ResultWrapper<T>> =
    flow<ResultWrapper<T>> {
        emit(ResultWrapper.Loading)

        val result = apiCall.invoke()
        result.data?.let {
            emit(ResultWrapper.Success(result.data, result.message?:"Success", result.numOfCartItems?:0))
        }
        Log.i("safeApi", "success")
    }.flowOn(Dispatchers.IO)
        .catch{e ->
            when (e) {
                is SocketTimeoutException -> {
                    emit(ResultWrapper.Error(ServerTimeOutException(e.message?:"Timeout Exception", e)))
                    Log.i("safeApi", "timeout")
                }
                is IOException -> {
                    emit(ResultWrapper.Error(ServerTimeOutException(e.message?:"IOException", e)))
                    Log.i("safeApi", "ioex")
                }
                is HttpException -> {
                    val body = e.response()?.errorBody()?.string()
                    val response = Gson().fromJson(body, BaseResponse::class.java)
                    emit(ResultWrapper.ServerError(
                        ServerError(
                            response.statusMessage ?: "", response.message ?: "HTTP Exception", e.code()
                        )
                    ))
                    Log.i("safeApi", "http")
                }
                is JsonSyntaxException -> {
                    emit(ResultWrapper.Error(ParsingException(e)))
                    Log.i("safeApi", "parsing")
                }
                else -> { emit(ResultWrapper.Error(e))
                    Log.i("safeApi", "else")
                }
            }
        }


suspend fun userApiCall(apiCall : suspend ()-> UserResponse) : Flow<ResultWrapper<User>> =
    flow<ResultWrapper<User>> {
        emit(ResultWrapper.Loading)
        try {
            val result = apiCall.invoke()
            result.user?.let {
                result.user.token = result.token
                emit(
                    ResultWrapper.Success(
                        result.user.toUser(),
                        result.message ?: "User logged in successfully"
                    )
                )
            } ?: run {
                emit(ResultWrapper.Error(NullPointerException("User object is null")))
            }
        } catch (e: SocketTimeoutException) {
            emit(
                ResultWrapper.Error(
                    ServerTimeOutException(
                        e.message ?: "Timeout Exception", e
                    )
                )
            )
        } catch (e: IOException) {
            emit(ResultWrapper.Error(ServerTimeOutException(e.message ?: "IOException", e)))
        }catch (e: HttpException){
            if (e.code() == 401) {
                // Handle 401 Unauthorized error (e.g., token expired)
                // Optionally, refresh token and retry request
                val body = e.response()?.errorBody()?.string()
                val response = Gson().fromJson(body, UserResponse::class.java)
                emit(
                    ResultWrapper.ServerError(
                        ServerError(
                            status = response.statusMessage,
                            serverMessage = response.message,
                            ex = e
                        )
                    )
                )
            } else {
                val body = e.response()?.errorBody()?.string()
                val response = Gson().fromJson(body, BaseResponse::class.java)
                emit(
                    ResultWrapper.ServerError(ServerError(status = response.statusMessage, serverMessage = response.message, ex = e))
                )
            }
        }catch (e: JsonSyntaxException) {
            emit(ResultWrapper.Error(ParsingException(e)))
        } catch (e: Exception) {
            emit(ResultWrapper.Error(e))
        }
    }.flowOn(Dispatchers.IO)