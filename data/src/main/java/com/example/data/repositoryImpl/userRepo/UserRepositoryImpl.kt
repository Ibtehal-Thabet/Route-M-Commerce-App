package com.example.data.repositoryImpl.userRepo

import com.example.data.dataSourceContract.UserDataSource
import com.example.domain.common.ResultWrapper
import com.example.domain.model.User
import com.example.domain.repositories.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
     private val dataSource: UserDataSource
) : UserRepository {

    override suspend fun registerUser(user: User): Flow<ResultWrapper<User?>> {
        return dataSource.registerUser(user)
    }

    override suspend fun login(user: User): Flow<ResultWrapper<User?>> {
        return dataSource.login(user)
    }

    override suspend fun updateProfile(
        userName: String?,
        userEmail: String?,
        userPhone: String?,
        token: String
    ): Flow<ResultWrapper<User?>> {
        return dataSource.updateProfile(userName = userName, userEmail = userEmail, userPhone = userPhone, token = token)
    }

    override suspend fun updatePassword(
        currentPassword: String,
        password: String,
        rePassword: String,
        token: String
    ): Flow<ResultWrapper<User?>> {
        return dataSource.updatePassword(currentPassword = currentPassword, password = password, rePassword = rePassword, token = token)
    }
}