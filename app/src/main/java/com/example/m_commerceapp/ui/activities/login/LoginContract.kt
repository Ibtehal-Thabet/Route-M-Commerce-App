package com.example.m_commerceapp.ui.activities.login

import androidx.lifecycle.LiveData
import com.example.domain.model.User
import kotlinx.coroutines.flow.StateFlow

class LoginContract {
    interface ViewModel {
        val states: StateFlow<State>
        val events: LiveData<Event>
        fun invokeAction(action: Action)
    }

    sealed class State {
        class Error(val message: String) : State()

        class Success(val userToken: String, val message: String) : State()

        object Loading: State()
    }

    sealed class Action {
        class LoginUser(val user: User) : Action()

        class RegisterClicked : Action()
    }

    sealed class Event {
        class NavigateToRegister : Event()
        class NavigateToHome : Event()
        class showError(val message: String): Event()
    }
}