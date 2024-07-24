package com.example.m_commerceapp.ui.fragments.favorite

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.domain.model.Product
import com.example.domain.model.cart.CartItem
import com.example.m_commerceapp.ui.activities.productList.ProductsContract
import kotlinx.coroutines.flow.StateFlow

class WishListContract {
    interface ViewModel {
        val wishStates: StateFlow<State>
        val wishEvents: LiveData<Event>
        fun invokeAction(action: Action)
    }

    sealed class State {
        class Error(val message: String) : State()

        class SuccessLoaded(val products: List<Product?>) : State()

        class SuccessAdded(val product: Product?) : State()

        class SuccessRemoved(val product: Product?) : State()

        object Loading: State()
    }

    sealed class Action {
        class AddToWishList(val product: Product?) : Action()

        class RemoveFromWishList(val product: Product?) : Action()

        class GetAuthWishList(val context: Context) : Action()

        class AddProductToCart(val cartItem: CartItem<String>): Action()

        class RemoveProductFromCart(val cartItem: CartItem<Product>): Action()

        class UpdateProductCartQuantity(val cartItem: CartItem<Product>, val quantity: String): Action()

    }

    sealed class Event {
        class ProductRemovedSuccessfully(val message: String) : Event()
        class ProductAddedSuccessfully(val message: String) : Event()
        class AddedOrRemoveToCartSuccessfully(val message: String) : Event()

    }
}