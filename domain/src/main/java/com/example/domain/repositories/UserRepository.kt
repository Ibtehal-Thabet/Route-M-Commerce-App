package com.example.domain.repositories

import com.example.domain.common.ResultWrapper
import com.example.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun registerUser(user: User): Flow<ResultWrapper<User?>>

    suspend fun login(user: User): Flow<ResultWrapper<User?>>

    suspend fun updateProfile(userName: String? = null,
                              userEmail: String? = null,
                              userPhone: String? = null,
                              token: String): Flow<ResultWrapper<User?>>

    suspend fun updatePassword(currentPassword: String,
                               password: String,
                               rePassword: String,
                               token: String): Flow<ResultWrapper<User?>>
}