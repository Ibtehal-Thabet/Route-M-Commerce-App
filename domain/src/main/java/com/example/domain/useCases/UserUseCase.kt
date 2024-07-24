package com.example.domain.useCases

import com.example.domain.common.ResultWrapper
import com.example.domain.model.User
import com.example.domain.repositories.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserUseCase @Inject constructor(
    private val repository: UserRepository
) {

    //those will used in the view model
    suspend fun createUser(user: User): Flow<ResultWrapper<User?>> {
        return repository.registerUser(user)
    }

    suspend fun login(user: User): Flow<ResultWrapper<User?>> {
        return repository.login(user)
    }

    suspend fun updateProfile(userName: String? = null, userEmail: String? = null,
                              userPhone: String? = null, token: String): Flow<ResultWrapper<User?>> {

        return repository.updateProfile(userName = userName, userEmail = userEmail, userPhone = userPhone, token = token)
    }

    suspend fun updatePassword(currentPassword: String, password: String,
                               rePassword: String, token: String): Flow<ResultWrapper<User?>> {

        return repository.updatePassword(currentPassword = currentPassword, password = password, rePassword = rePassword, token = token)
    }
}