package com.example.m_commerceapp.ui.fragments.profile

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.domain.model.User
import kotlinx.coroutines.flow.StateFlow

class ProfileContract {
    interface ViewModel {
        val states: StateFlow<State>
        val events: LiveData<Event>
        fun invokeAction(action: Action)
    }

    sealed class State {
        class Error(val message: String) : State()

        class Success(val userToken: String) : State()

        object Loading: State()
    }

    sealed class Action {
        class UserProfile : Action()

        class UpdateProfile() : Action()

        class LogoutClicked(val context: Context) : Action()

        class UpdateAddress : Action()

        class UpdatePassword : Action()
    }

    sealed class Event {
        class UpdatedSuccessfully(val message: String) : Event()
        class UpdatedFailed(val message: String) : Event()

    }
}