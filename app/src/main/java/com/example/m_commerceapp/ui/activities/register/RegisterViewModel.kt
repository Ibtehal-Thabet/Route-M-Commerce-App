package com.example.m_commerceapp.ui.activities.register

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.common.ResultWrapper
import com.example.domain.model.User
import com.example.domain.useCases.UserUseCase
import com.example.m_commerceapp.ui.DispatchersModule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userUseCase: UserUseCase,
    @DispatchersModule.IODispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel(), RegisterContract.ViewModel {


    // LiveData for holding the toast message
    private val _toastMessage = MutableLiveData<String>()
    val toastMessage get() = _toastMessage

    val isLoading = MutableLiveData<Boolean>(false)

    val userName = MutableLiveData<String>("")
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val passwordConfirmation = MutableLiveData<String>()

    val userNameError = MutableLiveData<String?>()
    val emailError = MutableLiveData<String?>()
    val passwordError = MutableLiveData<String?>()
    val passwordConfirmationError = MutableLiveData<String>()

    private val _states = MutableStateFlow<RegisterContract.State>(
        RegisterContract.State.Loading
    )
    override val states = _states

    private val _events = MutableLiveData<RegisterContract.Event>()
    override val events = _events


    override fun invokeAction(action: RegisterContract.Action) {

        when (action) {
            is RegisterContract.Action.CreateUser -> createUser()
            is RegisterContract.Action.LoginClicked -> TODO()
        }
    }

    fun createUser(){
        viewModelScope.launch(ioDispatcher) {
            if (!validForm())
                return@launch

            val user = User(name = userName.value, email = email.value, password = password.value, rePassword = passwordConfirmation.value)
            userUseCase.createUser(user)
                .collect{ response ->
                    when(response) {
                        is ResultWrapper.Success -> {
                            userName.postValue(response.data?.name)
                            email.postValue(response.data?.email)
                            password.postValue(response.data?.password)
                            passwordConfirmation.postValue(response.data?.rePassword)
                            Log.i("response", response.data.toString())
//                            isLoading.value = false
//                            showToast("User Registered Successfully")
                        }

                        is ResultWrapper.Error -> {
                            _states.emit(
                                RegisterContract.State.Error(
                                    response.error.localizedMessage ?: "Error"
                                )
                            )
                        }

                        is ResultWrapper.ServerError -> {
                            _states.emit(RegisterContract.State.Error(response.error.serverMessage?:"Server Error"))
                        }

                        is ResultWrapper.Loading -> {
                            _states.emit(RegisterContract.State.Loading)
                        }
                        else -> {}
                    }
                }
        }
    }

    private fun validForm(): Boolean {
        var isValid = true
        if (userName.value.isNullOrBlank()) {
            Log.d("tag", "Name is invalid")
            userNameError.postValue("Please enter user name")
            isValid = false
        } else {
            userNameError.postValue(null)
        }

        if (email.value.isNullOrBlank()) {
            Log.d("tag", "Email is invalid")
            emailError.postValue("Please enter email")
            isValid = false
        } else {
            emailError.postValue(null)
        }

        if (password.value.isNullOrBlank()) {
            Log.d("tag", "Password is invalid")
            passwordError.postValue("Please enter password")
            isValid = false
        } else {
            passwordError.postValue(null)
        }

        if (passwordConfirmation.value.isNullOrBlank()
            || passwordConfirmation.value != password.value
        ) {
            passwordConfirmationError.postValue("Password doesn't match")
            isValid = false
        } else {
            passwordConfirmationError.postValue(null)
        }
        return isValid
    }

    fun navigateToLogin(){
        events.postValue(RegisterContract.Event.NavigateToLogin())
    }

}