package com.example.m_commerceapp.ui.fragments.favorite

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.common.ResultWrapper
import com.example.domain.model.Product
import com.example.domain.model.cart.CartItem
import com.example.domain.useCases.CartUseCase
import com.example.domain.useCases.WishListUseCase
import com.example.m_commerceapp.ui.DispatchersModule
import com.example.m_commerceapp.ui.SharedPreferencesHelper
import com.example.m_commerceapp.ui.authorization
import com.example.m_commerceapp.ui.fragments.cart.CartContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class WishListViewModel @Inject constructor(
    private val wishListUseCase: WishListUseCase,
    private val cartUseCase: CartUseCase,
    @DispatchersModule.IODispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel(), WishListContract.ViewModel {


    private val _states = MutableStateFlow<WishListContract.State>(
        WishListContract.State.Loading
    )
    override val wishStates = _states

    private val _events = MutableLiveData<WishListContract.Event>()
    override val wishEvents = _events


    override fun invokeAction(action: WishListContract.Action) {
        when (action) {
            is WishListContract.Action.AddToWishList -> addToWishList(action.product)

            is WishListContract.Action.RemoveFromWishList -> removeFromWishList(action.product)

            is WishListContract.Action.GetAuthWishList -> getAuthWishList(action.context)

            is WishListContract.Action.AddProductToCart -> addToCart(action.cartItem)

            is WishListContract.Action.RemoveProductFromCart -> removeFromCart(action.cartItem)

            is WishListContract.Action.UpdateProductCartQuantity -> updateProductQuantityInCart(action.cartItem, action.quantity)
        }
    }

    private fun addToWishList(product: Product?) {

        viewModelScope.launch(ioDispatcher) {

            wishListUseCase.addToWishList(product = product!!, authorization = authorization)
                .collect{ response ->
                    Log.i("adddddd", response.toString())
                    when(response) {
                        is ResultWrapper.Success -> {
                            Log.i("add response", response.data.toString())
                            _events.postValue(WishListContract.Event.ProductAddedSuccessfully("Product Added Successfully"))
//                    _states.emit(WishListContract.State.SuccessAdded(response.data ?: listOf()))
                        }

                        is ResultWrapper.Error -> {
                            _states.emit(
                                WishListContract.State.Error(
                                    response.error.localizedMessage ?: "Error"
                                )
                            )
                        }

                        is ResultWrapper.ServerError -> {
                            _states.emit(WishListContract.State.Error(response.error.serverMessage?:"Server Error"))
                        }

                        is ResultWrapper.Loading -> {
                            _states.emit(WishListContract.State.Loading)
                        }
                        else -> {}
                    }
                }
        }
    }

     fun removeFromWishList(product: Product?) {

        viewModelScope.launch(ioDispatcher) {

            wishListUseCase.removeFromWishList(product = product!!, authorization = authorization)
                .collect{ response ->
                    Log.i("rrrr", response.toString())
                    when(response) {
                        is ResultWrapper.Success -> {
                            Log.i("remove response", response.data.toString())
                            _events.postValue(WishListContract.Event.ProductRemovedSuccessfully("Product Removed Successfully"))
//                            getAuthWishList()
//                            _states.emit(WishListContract.State.SuccessRemoved(response.data))
                        }

                        is ResultWrapper.Error -> {
                            _states.emit(
                                WishListContract.State.Error(
                                    response.error.localizedMessage ?: "Error"
                                )
                            )
                        }

                        is ResultWrapper.ServerError -> {
                            _states.emit(WishListContract.State.Error(response.error.serverMessage?:"Server Error"))
                        }

                        is ResultWrapper.Loading -> {
                            _states.emit(WishListContract.State.Loading)
                        }
                        else -> {}
                    }
                }
        }
    }

    fun getAuthWishList(context: Context) {

        viewModelScope.launch(ioDispatcher) {
            wishListUseCase.getAuthWishList(authorization)
                .collect {  response ->
                    when (response) {
                        is ResultWrapper.Success -> {
                            _states.emit(
                                WishListContract.State.SuccessLoaded(
                                    response.data ?: listOf()
                                )
                            )

                            for (wishProduct in response.data!!){
                                if(wishProduct?.isInWishList!!){
                                    SharedPreferencesHelper.saveWishlistStatus(context, wishProduct._id!!, true)
                                }
                            }
                        }

                        is ResultWrapper.Error -> {
                            _states.emit(
                                WishListContract.State.Error(
                                    response.error.localizedMessage ?: "Error"
                                )
                            )
                        }

                        is ResultWrapper.ServerError -> {
                            _states.emit(WishListContract.State.Error(response.error.serverMessage?:"Server Error"))
                        }

                        is ResultWrapper.Loading -> {
                            _states.emit(WishListContract.State.Loading)
                        }
                        else -> {}
                    }
                }
        }
    }

    fun addToCart(cartItem: CartItem<String>?) {

        viewModelScope.launch(ioDispatcher) {

            cartUseCase.addToCart(cartItemId = cartItem?._id!!, authorization = authorization)
                .collect{ response ->
                    when(response) {
                        is ResultWrapper.Success -> {
                            Log.i("add response", response.data.toString())
                            _events.postValue(WishListContract.Event.AddedOrRemoveToCartSuccessfully("Product added successfully to your cart"))
                        }

                        is ResultWrapper.Error -> {
                            _states.emit(
                                WishListContract.State.Error(
                                    response.error.localizedMessage ?: "Error"
                                )
                            )
                        }

                        is ResultWrapper.ServerError -> {
                            _states.emit(WishListContract.State.Error(response.error.serverMessage?:"Server Error"))
                        }

                        is ResultWrapper.Loading -> {
                            _states.emit(WishListContract.State.Loading)
                        }
                        else -> {}
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
                            Log.i("remove wish response", response.data.toString())
                            _events.postValue(WishListContract.Event.AddedOrRemoveToCartSuccessfully("Product removed successfully from cart"))
//                            getAuthCart()
//                            _states.emit(WishListContract.State.SuccessRemoved(response.data))
                        }

                        is ResultWrapper.Error -> {
                            _states.emit(
                                WishListContract.State.Error(
                                    response.error.localizedMessage ?: "Error"
                                )
                            )
                        }

                        is ResultWrapper.ServerError -> {
                            _states.emit(WishListContract.State.Error(response.error.serverMessage?:"Server Error"))
                        }

                        is ResultWrapper.Loading -> {
                            _states.emit(WishListContract.State.Loading)
                        }
                        else -> {}
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

//                            _states.emit(CartContract.State.SuccessLoaded(response.data))
                        }

                        is ResultWrapper.Error -> {
                            _states.emit(
                                WishListContract.State.Error(
                                    response.error.localizedMessage ?: "Error"
                                )
                            )
                        }

                        is ResultWrapper.ServerError -> {
                            _states.emit(WishListContract.State.Error(response.error.serverMessage?:"Server Error"))
                        }

                        is ResultWrapper.Loading -> {
                            _states.emit(WishListContract.State.Loading)
                        }
                        else -> {}
                    }
                }
        }
    }
}