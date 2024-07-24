package com.example.m_commerceapp.ui.fragments.cart

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.common.ResultWrapper
import com.example.domain.model.Product
import com.example.domain.model.cart.CartItem
import com.example.domain.useCases.CartUseCase
import com.example.m_commerceapp.ui.DispatchersModule
import com.example.m_commerceapp.ui.SharedPreferencesHelper
import com.example.m_commerceapp.ui.activities.productList.ProductsContract
import com.example.m_commerceapp.ui.authorization
import com.example.m_commerceapp.ui.numOfCartItems
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartUseCase: CartUseCase,
    @DispatchersModule.IODispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel(), CartContract.ViewModel {


    private val _states = MutableStateFlow<CartContract.State>(
        CartContract.State.Loading
    )
    override val cartStates = _states

    private val _events = MutableLiveData<CartContract.Event>()
    override val cartEvents = _events

    private val _quantity = MutableLiveData<Int>()
    val quantity = _quantity


    override fun invokeAction(action: CartContract.Action) {
        when (action) {
            is CartContract.Action.UpdateProductQuantityInCart -> updateProductQuantityInCart(action.cartItem, action.quantity)

            is CartContract.Action.RemoveItemFromCart -> removeFromCart(action.cartItem)

            is CartContract.Action.GetAuthCart -> getAuthCart(action.context)

            else -> {}
        }
    }

    private fun updateProductQuantityInCart(cartItem: CartItem<Product>?, quantity: String) {

        viewModelScope.launch(ioDispatcher) {

            cartUseCase.updateCartProductQuantity(cartItemId = cartItem?.product?._id!!, quantity = quantity, authorization = authorization)
                .collect{ response ->
                    Log.i("update", response.toString())
                    when(response) {
                        is ResultWrapper.Success -> {
                            _events.postValue(CartContract.Event.ProductUpdatedSuccessfully(response.message?:"Product quantity updated successfully"))
                        }

                        is ResultWrapper.Error -> {
                            _states.emit(
                                CartContract.State.Error(
                                    response.error.localizedMessage ?: "Error"
                                )
                            )
                        }

                        is ResultWrapper.ServerError -> {
                            _states.emit(CartContract.State.Error(response.error.serverMessage?:"Server Error"))
                        }

                        is ResultWrapper.Loading -> {
                            _states.emit(CartContract.State.Loading)
                        }
                        else -> {}
                    }
                }
        }
    }

     private fun removeFromCart(cartItem: CartItem<Product>?) {

        viewModelScope.launch(ioDispatcher) {

            cartUseCase.removeFromCart(cartItemId = cartItem?.product?._id!!, authorization = authorization)
                .collect{ response ->
                    when(response) {
                        is ResultWrapper.Success -> {
                            _events.postValue(CartContract.Event.ProductRemovedSuccessfully(response.message?:"Product removed successfully from cart"))
                        }

                        is ResultWrapper.Error -> {
                            _states.emit(
                                CartContract.State.Error(
                                    response.error.localizedMessage ?: "Error"
                                )
                            )
                        }

                        is ResultWrapper.ServerError -> {
                            _states.emit(CartContract.State.Error(response.error.serverMessage?:"Server Error"))
                        }

                        is ResultWrapper.Loading -> {
                            _states.emit(CartContract.State.Loading)
                        }
                        else -> {}
                    }
                }
        }
    }

    private fun getAuthCart(context: Context) {

        viewModelScope.launch(ioDispatcher) {
            cartUseCase.getAuthCart(authorization)
                .collect {  response ->
                    when (response) {
                        is ResultWrapper.Success -> {
                            _states.emit(
                                CartContract.State.SuccessLoaded(
                                    response.data
                                )
                            )
                            numOfCartItems = response.numberOfCartItems?:0

//                            for (cartProduct in response.data?.products!!){
//                                if(cartProduct.product?.isInCart!!){
//                                    SharedPreferencesHelper.saveCartStatus(context, cartProduct._id!!, true, cartProduct.count!!)
//                                }
//                            }
                        }

                        is ResultWrapper.Error -> {
                            _states.emit(
                                CartContract.State.Error(
                                    response.error.localizedMessage ?: "Error"
                                )
                            )
                        }

                        is ResultWrapper.ServerError -> {
                            _states.emit(CartContract.State.Error(response.error.serverMessage?:"Server Error"))
                        }

                        is ResultWrapper.Loading -> {
                            _states.emit(CartContract.State.Loading)
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
                            _events.postValue(CartContract.Event.ProductAddedSuccessfully(response.message?:"Product added successfully to your cart"))
//                            _states.emit(CartContract.State.SuccessAddedOrRemovedFromCart(cartItem))
//                            getAuthCart()
                        }

                        is ResultWrapper.Error -> {
                            _states.emit(
                                CartContract.State.Error(
                                    response.error.localizedMessage ?: "Error"
                                )
                            )
                        }

                        is ResultWrapper.ServerError -> {
                            _states.emit(CartContract.State.Error(response.error.serverMessage?:"Server Error"))
                        }

                        is ResultWrapper.Loading -> {
                            _states.emit(CartContract.State.Loading)
                        }
                        else -> {}
                    }
                }
        }
    }
}