package com.example.m_commerceapp.ui.fragments.profile

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.domain.common.ResultWrapper
import com.example.domain.model.User
import com.example.domain.useCases.UserUseCase
import com.example.m_commerceapp.common.SingleLiveEvent
import com.example.m_commerceapp.ui.DispatchersModule
import com.example.m_commerceapp.ui.authorization
import com.example.m_commerceapp.ui.saveUserLogged
import com.example.m_commerceapp.ui.userEmail
import com.example.m_commerceapp.ui.userName
import com.example.m_commerceapp.ui.userPhone
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    application: Application,
    private val userUseCase: UserUseCase,
    @DispatchersModule.IODispatcher private val ioDispatcher: CoroutineDispatcher
) : AndroidViewModel(application), ProfileContract.ViewModel {

    private val _states = MutableStateFlow<ProfileContract.State>(
        ProfileContract.State.Loading
    )
    override val states = _states

    private val _events = MutableLiveData<ProfileContract.Event>()
    override val events = _events

    private val _user = MutableLiveData<User>()
    val user = _user

    private val _name = MutableLiveData<String>()
    val name = _name

    private val _email = MutableLiveData<String>()
    val email = _email

    private val _phone = MutableLiveData<String>()
    val phone = _phone

    val password = MutableLiveData<String>()
    val token = MutableLiveData<String>()

    val nameError = MutableLiveData<String?>()
    val emailError = MutableLiveData<String?>()
    val passwordError = MutableLiveData<String?>()

    val firstName = MutableLiveData<String>()

    private val _isUpdating = SingleLiveEvent<Boolean>()
    val isUpdating: LiveData<Boolean> get() = _isUpdating

    private val _isCanceling = SingleLiveEvent<Boolean>()
    val isCanceling: LiveData<Boolean> get() = _isCanceling

    private val _isDoneUpdating = SingleLiveEvent<Boolean>()
    val isDoneUpdating: LiveData<Boolean> get() = _isDoneUpdating


    override fun invokeAction(action: ProfileContract.Action) {

        when (action) {

            is ProfileContract.Action.UserProfile -> getUser()
            is ProfileContract.Action.LogoutClicked -> clearUserInfo(action.context)
            is ProfileContract.Action.UpdateProfile -> updateProfile()
            is ProfileContract.Action.UpdateAddress -> TODO()
            is ProfileContract.Action.UpdatePassword -> updatePassword()
            else -> {}
        }
    }

    private fun getUser() {

        setUser(User(name = userName, email = userEmail, phone = userPhone))
        _name.postValue(userName)
        _email.postValue(userEmail)
        _phone.postValue(userPhone)
        firstName.postValue(userName.split(" ").first().toString())

        _isUpdating.value = true
        _isCanceling.value = false
        _isDoneUpdating.value = false

        Log.i("signin", userName + "   " + userEmail+ "    "+ userPhone)
        saveUserLogged(application = getApplication(),
            user_name = user.value?.name?:"",
            user_email = email.value?:"",
            user_phone = "")
    }

    fun updateProfile(){
        viewModelScope.launch(ioDispatcher) {
            userUseCase.updateProfile(userName = _name.value.toString(),
                userPhone = _phone.value.toString(), token = authorization)
                .collect { response ->
                    when (response) {
                        is ResultWrapper.Success -> {
                            _isDoneUpdating.postValue(false)
                            _events.postValue(ProfileContract.Event.UpdatedSuccessfully(response.message?:"Profile Updated Successfully"))
                            setUser(response.data!!)
                            saveUserLogged(getApplication(),
                                response.data?.name!!,
                                response.data?.email!!,
                                response.data?.phone!!)
                            Log.i("name", response.data?.name!!)

//                            _states.emit(
//                                ProfileContract.State.Success(
//                                    response.data
//                                )
//                            )
                        }

                        is ResultWrapper.Error -> {
                            _states.emit(
                                ProfileContract.State.Error(
                                    response.error.localizedMessage ?: "Error"
                                )
                            )
                            _events.postValue(ProfileContract.Event.UpdatedSuccessfully(response.error.localizedMessage?:"Profile Updated Failed"))
                        }

                        is ResultWrapper.ServerError -> {
                            _states.emit(ProfileContract.State.Error(response.error.serverMessage?:"Server Error"))
                            _events.postValue(ProfileContract.Event.UpdatedSuccessfully(response.error.serverMessage?:"Profile Updated Failed"))
                        }

                        is ResultWrapper.Loading -> {
                            _states.emit(ProfileContract.State.Loading)
                        }
                        else -> {}
                    }
                }
        }
    }

    fun updatePassword(){
        viewModelScope.launch(ioDispatcher) {
            userUseCase.updatePassword(currentPassword = "", password = "", rePassword = "", token = authorization)
                .collect {  response ->
                    when (response) {
                        is ResultWrapper.Success -> {
                            _events.postValue(ProfileContract.Event.UpdatedSuccessfully(response.message?:"Profile Updated Successfully"))
//                            _states.emit(
//                                ProfileContract.State.Success(
//                                    response.data
//                                )
//                            )
                        }

                        is ResultWrapper.Error -> {
                            _states.emit(
                                ProfileContract.State.Error(
                                    response.error.localizedMessage ?: "Error"
                                )
                            )
                        }

                        is ResultWrapper.ServerError -> {
                            _states.emit(ProfileContract.State.Error(response.error.serverMessage?:"Server Error"))
                        }

                        is ResultWrapper.Loading -> {
                            _states.emit(ProfileContract.State.Loading)
                        }
                        else -> {}
                    }
                }
        }
    }

     fun clearUserInfo(context: Context) {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear().apply()
    }

    fun setUser(user: User){
        _user.value = (user)
        name.value = user.name
        email.value = user.email
        phone.value = user.phone
        firstName.value = user.name?.split(" ")?.first().toString()
    }

    fun editClick(){
        _isCanceling.value = true
        _isUpdating.value = false
        _isDoneUpdating.value = false
    }

    fun onEdit(){
        _isDoneUpdating.value = true
        _isCanceling.value = true
        _isUpdating.value = false
    }

    fun cancelClick(){
        _isUpdating.value = true
        _isCanceling.value = false
        _isDoneUpdating.value = false
        setUser(user.value!!)
    }

    fun setUpdatingDone(isDone: Boolean){
        _isDoneUpdating.value = isDone
    }

    fun setUserName(uName: String){
        _name.value = uName
    }

    fun setUserEmail(uEmail: String){
        _email.value = uEmail
    }

    fun setUserPhone(uPhone: String){
        _phone.value = uPhone
    }

}