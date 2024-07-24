package com.example.m_commerceapp.ui.activities.productList

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.domain.model.Brand
import com.example.domain.model.cart.CartItem
import com.example.domain.model.Category
import com.example.domain.model.Product
import com.example.domain.model.SubCategory
import com.example.m_commerceapp.ui.activities.productDetails.ProductContract
import kotlinx.coroutines.flow.StateFlow

class ProductsContract {
    interface ViewModel {
        val productStates: StateFlow<State>
        val productEvents: LiveData<Event>
        fun invokeAction(action: Action)
    }

    sealed class State {
        class Error(val message: String) : State()

        class Success(val products: List<Product?>) : State()

        class SuccessAddedOrRemovedFromWishList(val product: Product?) : State()

        class SuccessAddedOrRemovedFromCart<T>(val cartItem: CartItem<T>?) : State()

        object Loading: State()
    }

    sealed class Action {
        class LoadSubCategory(val category: Category) : Action()

        class LoadProduct(val product: Product): Action()
        class LoadCategoryProducts(val context: Context, val category: Category) : Action()
        class LoadSubCategoryProducts(val context: Context, val subCategory: SubCategory) : Action()
        class LoadBrandProducts(val context: Context, val brand: Brand) : Action()

        class AddProductToWishList(val product: Product): Action()
        class RemoveProductFromWishList(val product: Product): Action()

        class AddProductToCart(val cartItem: CartItem<String>): Action()
        class RemoveProductFromCart(val cartItem: CartItem<Product>): Action()
        class UpdateProductCartQuantity(val cartItem: CartItem<Product>, val quantity: String): Action()

        class ProductClicked(val product: Product) : Action()
    }

    sealed class Event {
        class NavigateToProductDetails(val product: Product) : Event()

        class AddToWishList(val message: String) : Event()

        class RemoveFromWishList(val message: String) : Event()

        class AddToCart(val message: String) : Event()

        class RemoveFromCart(val message: String) : Event()

        class UpdatedInCart(val message: String) : Event()

        class ShowErrorMessage(val message: String) : Event()
    }
}