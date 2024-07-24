package com.example.m_commerceapp.ui.activities.productDetails

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.common.ResultWrapper
import com.example.domain.model.cart.CartItem
import com.example.domain.model.Product
import com.example.domain.useCases.CartUseCase
import com.example.domain.useCases.GetProductsUseCase
import com.example.domain.useCases.WishListUseCase
import com.example.m_commerceapp.ui.DispatchersModule
import com.example.m_commerceapp.ui.SharedPreferencesHelper
import com.example.m_commerceapp.ui.activities.productList.ProductsContract
import com.example.m_commerceapp.ui.authorization
import com.example.m_commerceapp.ui.fragments.favorite.WishListContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val wishListUseCase: WishListUseCase,
    private val cartUseCase: CartUseCase,
    @DispatchersModule.IODispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel(), ProductContract.ViewModel {

    private val _states = MutableStateFlow<ProductContract.State>(
        ProductContract.State.Loading
    )
    override val productStates = _states

    private val _events = MutableLiveData<ProductContract.Event>()
    override val productEvents = _events

    override fun invokeAction(action: ProductContract.Action) {
        when (action) {
            is ProductContract.Action.LoadProduct -> loadProduct(action.context, action.product)

            is ProductContract.Action.AddProductToWishList -> addToWishList(action.product)

            is ProductContract.Action.AddProductToCart -> addToCart(action.cartItem)

            is ProductContract.Action.RemoveProductFromWishList -> removeFromWishList(action.product)
            is ProductContract.Action.RemoveProductFromCart -> removeFromCart(action.cartItem)
            is ProductContract.Action.UpdateProductCartQuantity -> updateProductQuantityInCart(action.cartItem, action.quantity)
            else -> {}
        }

    }

    private fun loadProduct(context: Context, product: Product) {

        viewModelScope.launch(ioDispatcher) {
            getProductsUseCase.invokeProduct(productId = product._id, authorization)
                .collect {  response ->
                    when (response) {
                        is ResultWrapper.Success -> {
                            _states.emit(ProductContract.State.Success(response.data?: listOf()))

                            // Update SharedPreferences with fetched products and wishlist status
                            for (product in response.data!! ) {
                                val isInWishlist = SharedPreferencesHelper.getWishlistStatus(context, product?._id!!)
                                product.isInWishList = isInWishlist
                                // Optionally, save updated product to SharedPreferences (if needed)
                                SharedPreferencesHelper.saveWishlistStatus(context, product._id!!, isInWishlist)
                            }

                            // Update SharedPreferences with fetched products and cart status
                            for (product in response.data!!) {
                                val isInCart = SharedPreferencesHelper.getCartStatus(context, product?._id!!)
                                val itemQuantity = SharedPreferencesHelper.getItemQuantity(context, product._id!!)
                                product.isInCart = isInCart
                                // Optionally, save updated product to SharedPreferences (if needed)
                                SharedPreferencesHelper.saveCartStatus(context, product._id!!, isInCart, itemQuantity)
                            }
                        }

                        is ResultWrapper.Error -> {
                            _states.emit(
                                ProductContract.State.Error(
                                    response.error.localizedMessage ?: "Error"
                                )
                            )
                        }

                        is ResultWrapper.ServerError -> {
                            _states.emit(ProductContract.State.Error(response.error.serverMessage?:"Server Error"))
                        }

                        is ResultWrapper.Loading -> {
                            _states.emit(ProductContract.State.Loading)
                        }
                        else -> {}
                    }
                }
        }
    }

    fun addToWishList(product: Product) {

        viewModelScope.launch(ioDispatcher) {
            Log.i("auth", authorization)

            wishListUseCase.addToWishList(product = product, authorization = authorization)
                .collect{ response ->

                    when(response) {
                        is ResultWrapper.Success -> {
                            Log.i("response WISH DETAIL", response.data.toString())
                            _events.postValue(ProductContract.Event.AddToWishList(response.message?:"Product added successfully"))
                            _states.emit(ProductContract.State.SuccessAddedOrRemovedFromWishList(product))
                        }
                        else -> {
                            _events.postValue(ProductContract.Event.ShowErrorMessage("Something Wrong!"))
                        }
                    }
                }
        }
    }

    fun removeFromWishList(product: Product?) {

        viewModelScope.launch(ioDispatcher) {

            wishListUseCase.removeFromWishList(product = product!!, authorization = authorization)
                .collect{ response ->
                    when(response) {
                        is ResultWrapper.Success -> {
                            _events.postValue(ProductContract.Event.RemoveFromWishList(response.message?:"Product removed successfully"))
                            _states.emit(ProductContract.State.SuccessAddedOrRemovedFromWishList(product))
                        }
                        else -> {
                            _events.postValue(ProductContract.Event.ShowErrorMessage("Something Wrong!"))
                        }
                    }
                }
        }
    }

    fun addToCart(cartItem: CartItem<String>?) {

        viewModelScope.launch(ioDispatcher) {

            cartUseCase.addToCart(cartItemId = cartItem?._id?:"", authorization = authorization)
                .collect{ response ->
                    when(response) {
                        is ResultWrapper.Success -> {
                            _events.postValue(ProductContract.Event.AddToCart(response.message?:"Product added successfully to cart"))
                            _states.emit(ProductContract.State.SuccessAddedOrRemovedFromCart(cartItem))
                        }
                        else -> {
                            _events.postValue(ProductContract.Event.ShowErrorMessage("Something Wrong!"))
                        }
                    }
                }
        }
    }

    fun removeFromCart(cartItem: CartItem<Product>?) {

        viewModelScope.launch(ioDispatcher) {

            cartUseCase.removeFromCart(cartItemId = cartItem?.product?._id!!, authorization = authorization)
                .collect{ response ->
                    when(response) {
                        is ResultWrapper.Success -> {
                            _events.postValue(ProductContract.Event.RemoveFromCart(response.message?:"Product removed successfully from cart"))
                            _states.emit(ProductContract.State.SuccessAddedOrRemovedFromCart(cartItem))
                        }

                        else -> {
                            _events.postValue(ProductContract.Event.ShowErrorMessage("Something Wrong!"))
                        }
                    }
                }
        }
    }

    fun updateProductQuantityInCart(cartItem: CartItem<Product>?, quantity: String) {

        viewModelScope.launch(ioDispatcher) {

            cartUseCase.updateCartProductQuantity(cartItemId = cartItem?.product?._id!!, quantity = quantity, authorization = authorization)
                .collect{ response ->
                    when(response) {
                        is ResultWrapper.Success -> {
                            _events.postValue(ProductContract.Event.UpdatedInCart(response.message?:"Product quantity updated successfully"))
                            _states.emit(ProductContract.State.SuccessAddedOrRemovedFromCart(cartItem))
                        }

                        else -> {
                            _events.postValue(ProductContract.Event.ShowErrorMessage("Something Wrong!"))
                        }
                    }
                }
        }
    }

}