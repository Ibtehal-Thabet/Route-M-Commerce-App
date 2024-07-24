package com.example.m_commerceapp.ui.fragments.cart

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.domain.model.Product
import com.example.domain.model.cart.CartItem
import com.example.domain.model.cart.CartProducts
import kotlinx.coroutines.flow.StateFlow

class CartContract {
    interface ViewModel {
        val cartStates: StateFlow<State>
        val cartEvents: LiveData<Event>
        fun invokeAction(action: Action)
    }

    sealed class State {
        class Error(val message: String) : State()

        class SuccessLoaded(val cartItems: CartProducts<Product>?) : State()

        class SuccessAdded(val cartItem: CartItem<String>?) : State()

        class SuccessRemoved(val cartItem: CartItem<Product>?) : State()

        object Loading: State()
    }

    sealed class Action {
        class UpdateProductQuantityInCart(val cartItem: CartItem<Product>?, val quantity: String) : Action()

        class RemoveItemFromCart(val cartItem: CartItem<Product>?) : Action()

        class GetAuthCart(val context: Context) : Action()

    }

    sealed class Event {
        class ProductRemovedSuccessfully(val message: String) : Event()
        class ProductAddedSuccessfully(val message: String) : Event()
        class ProductUpdatedSuccessfully(val message: String) : Event()

    }
}