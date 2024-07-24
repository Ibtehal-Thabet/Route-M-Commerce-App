package com.example.data.dataSourceImpl.userDataSoruceImpl

import android.util.Log
import com.example.data.api.WebService
import com.example.data.dataSourceContract.UserDataSource
import com.example.data.safeApiCall
import com.example.data.userApiCall
import com.example.domain.common.ResultWrapper
import com.example.domain.model.LoginRequest
import com.example.domain.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class UserOnlineDataSourceImpl @Inject constructor(
    private val webService: WebService
    ) : UserDataSource {

    override suspend fun registerUser(user: User): Flow<ResultWrapper<User?>> {
        val response = safeApiCall { webService.registerUser(user) }
        return response
    }

    override suspend fun login(user: User): Flow<ResultWrapper<User?>> {
        val response = userApiCall { webService.login(LoginRequest(email = user.email?:"", password = user.password?:"")) }
        Log.i("response", response.toString())
        return response
    }

    override suspend fun updateProfile(
        userName: String?,
        userEmail: String?,
        userPhone: String?,
        token: String
    )
    : Flow<ResultWrapper<User?>> {

        val response = userApiCall {
            webService.updateProfile(
                name = userName,
                email = userEmail,
                phone = userPhone,
                authorization = token
            )
        }
        return response
    }

    override suspend fun updatePassword(
        currentPassword: String,
        password: String,
        rePassword: String,
        token: String
    ): Flow<ResultWrapper<User?>> {
        val response = userApiCall {
            webService.updateAuthPassword(
                currentPassword = currentPassword,
                password = password,
                rePassword = rePassword,
                token = token
            )
        }
        return response
    }
}