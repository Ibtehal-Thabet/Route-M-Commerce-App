package com.example.m_commerceapp.ui.activities.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.common.ResultWrapper
import com.example.domain.model.User
import com.example.domain.useCases.UserUseCase
import com.example.m_commerceapp.ui.DispatchersModule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userUseCase: UserUseCase,
    @DispatchersModule.IODispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel(), LoginContract.ViewModel {

    val isLoading = MutableLiveData<Boolean>(false)

    val name = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val phone = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val token = MutableLiveData<String>()

    val emailError = MutableLiveData<String?>()
    val passwordError = MutableLiveData<String?>()

    private val _states = MutableStateFlow<LoginContract.State>(
        LoginContract.State.Loading
    )
    override val states: StateFlow<LoginContract.State> = _states

    private val _events = MutableLiveData<LoginContract.Event>()
    override val events = _events


    override fun invokeAction(action: LoginContract.Action) {

        when (action) {
            is LoginContract.Action.LoginUser -> login()
            is LoginContract.Action.RegisterClicked -> navigateToRegister()
            else -> {}
        }
    }


    fun login() {
        if (!validForm())
            return

        isLoading.value = true
        viewModelScope.launch {
                val user = User(
                    email = email.value,
                    password = password.value,
                )
                userUseCase.login(user)
                    .collect { response ->
                        when (response) {
                            is ResultWrapper.Success -> {
                                name.postValue(response.data?.name)
                                email.postValue(response.data?.email)
                                phone.postValue(response.data?.phone)
                                password.postValue(response.data?.password)
                                token.postValue(response.data?.token)
//                                loginResult.value = response
                                    _states.emit(
                                        LoginContract.State.Success(
                                            response.data?.token ?: "", response.message?:""
                                        )
                                    )

                                    Log.i("login", response.data.toString())

                            }

                            is ResultWrapper.Error -> {
                                _states.emit(
                                    LoginContract.State.Error(
                                        response.error.localizedMessage ?: "Error"
                                    )
                                )
                            }

                            is ResultWrapper.ServerError -> {
                                _states.emit(LoginContract.State.Error(response.error.serverMessage?:"Server Error"))
                                Log.i("Errorrrrrrr", response.error.serverMessage?:"error:(")
                            }

                            is ResultWrapper.Loading -> {
                                _states.emit(LoginContract.State.Loading)
                            }
                            else -> {}
                        }
                    }
        }
    }

    private fun validForm(): Boolean {
        var isValid = true

        if (email.value.isNullOrBlank()) {
            emailError.postValue("Please enter email")
            isValid = false
        } else {
            emailError.postValue(null)
        }

        if (password.value.isNullOrBlank()) {
            passwordError.postValue("Please enter password")
            isValid = false
        } else {
            passwordError.postValue(null)
        }

        return isValid
    }

    fun navigateToRegister(){
        events.postValue(LoginContract.Event.NavigateToRegister())
    }
}