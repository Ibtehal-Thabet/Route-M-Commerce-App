package com.example.m_commerceapp.ui.activities.register

import androidx.lifecycle.LiveData
import com.example.domain.model.User
import kotlinx.coroutines.flow.StateFlow

class RegisterContract {
    interface ViewModel {
        val states: StateFlow<State>
        val events: LiveData<Event>
        fun invokeAction(action: Action)
    }

    sealed class State {
        class Error(val message: String) : State()

        class Success(val user: User) : State()

        object Loading: State()
    }

    sealed class Action {
        class CreateUser(val user: User) : Action()

        class LoginClicked : Action()
    }

    sealed class Event {
        class NavigateToLogin() : Event()
    }
}