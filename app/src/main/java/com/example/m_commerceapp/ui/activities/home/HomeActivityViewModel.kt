package com.example.m_commerceapp.ui.activities.home

import android.content.Context
import android.util.Log
import android.widget.QuickContactBadge
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.common.ResultWrapper
import com.example.domain.model.Product
import com.example.domain.model.cart.CartProducts
import com.example.domain.useCases.CartUseCase
import com.example.domain.useCases.GetProductsUseCase
import com.example.domain.useCases.WishListUseCase
import com.example.m_commerceapp.ui.DispatchersModule
import com.example.m_commerceapp.ui.SharedPreferencesHelper
import com.example.m_commerceapp.ui.activities.productList.ProductsContract
import com.example.m_commerceapp.ui.authorization
import com.example.m_commerceapp.ui.fragments.cart.CartContract
import com.example.m_commerceapp.ui.fragments.favorite.WishListContract
import com.example.m_commerceapp.ui.fragments.home.HomeContract
import com.example.m_commerceapp.ui.numOfCartItems
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeActivityViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val cartUseCase: CartUseCase,
    private val wishListUseCase: WishListUseCase,
    @DispatchersModule.IODispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel(),ProductsContract.ViewModel, CartContract.ViewModel, WishListContract.ViewModel {

    private val _states = MutableStateFlow<ProductsContract.State>(
        ProductsContract.State.Loading
    )
    override val productStates = _states

    private val _events = MutableLiveData<ProductsContract.Event>()
    override val productEvents = _events

    private val _cartStates = MutableStateFlow<CartContract.State>(
        CartContract.State.Loading
    )
    override val cartStates = _cartStates

    private val _cartEvents = MutableLiveData<CartContract.Event>()
    override val cartEvents = _cartEvents

    private val _badge = MutableLiveData<Int>()
    val badge = _badge

    private val _wishStates = MutableStateFlow<WishListContract.State>(
        WishListContract.State.Loading
    )
    override val wishStates = _wishStates

    private val _wishEvents = MutableLiveData<WishListContract.Event>()
    override val wishEvents = _wishEvents

    private val _searchTextLiveData = MutableLiveData<String>()
    val searchTextLiveData: LiveData<String> get() = _searchTextLiveData

    // product search invoke
    override fun invokeAction(action: ProductsContract.Action) {
        when (action) {
            is ProductsContract.Action.LoadProduct -> TODO()
            is ProductsContract.Action.ProductClicked -> TODO()
            else -> {}
        }
    }

    // cart invoke
    override fun invokeAction(action: CartContract.Action) {
        when (action) {

            is CartContract.Action.GetAuthCart -> getAuthCart(action.context)

            else -> {}
        }
    }

    // wish invoke
    override fun invokeAction(action: WishListContract.Action) {
        when (action) {

            is WishListContract.Action.GetAuthWishList -> getAuthWishList(action.context)

            else -> {}
        }
    }

    fun getAuthCart(context: Context) {
        viewModelScope.launch(ioDispatcher) {
            cartUseCase.getAuthCart(authorization)
                .collect {  response ->
                    when (response) {
                        is ResultWrapper.Success -> {
                            _cartStates.emit(
                                CartContract.State.SuccessLoaded(
                                    response.data
                                )
                            )
                            _badge.postValue(response.numberOfCartItems?:0)
                            for (cartProduct in response.data?.products!!){
                                Log.i("isInCartList", cartProduct.product?.isInCart.toString())
                                cartProduct.product?.isInCart = true
                                SharedPreferencesHelper.saveCartStatus(context, cartProduct._id!!, true, cartProduct.count!!)

                            }
                        }

                        is ResultWrapper.Error -> {
                            _cartStates.emit(
                                CartContract.State.Error(
                                    response.error.localizedMessage ?: "Error"
                                )
                            )
                        }

                        is ResultWrapper.ServerError -> {
                            _cartStates.emit(CartContract.State.Error(response.error.serverMessage?:"Server Error"))
                        }

                        is ResultWrapper.Loading -> {
                            _cartStates.emit(CartContract.State.Loading)
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
                            _wishStates.emit(
                                WishListContract.State.SuccessLoaded(
                                    response.data ?: listOf()
                                )
                            )

                            for (wishProduct in response.data!!){
                                Log.i("isInWishList", wishProduct?.isInWishList.toString())
                                wishProduct?.isInWishList = true
                                SharedPreferencesHelper.saveWishlistStatus(context, wishProduct?._id!!, true)

                            }
                        }

                        is ResultWrapper.Error -> {
                            _wishStates.emit(
                                WishListContract.State.Error(
                                    response.error.localizedMessage ?: "Error"
                                )
                            )
                        }

                        is ResultWrapper.ServerError -> {
                            _wishStates.emit(WishListContract.State.Error(response.error.serverMessage?:"Server Error"))
                        }

                        is ResultWrapper.Loading -> {
                            _wishStates.emit(WishListContract.State.Loading)
                        }
                        else -> {}
                    }
                }
        }
    }

    fun getSearchedProduct(query: String){
        viewModelScope.launch(Dispatchers.IO){
            getProductsUseCase.invokeSearchedProducts(authorization)
                .collect { response->
                    when (response) {
                        is ResultWrapper.Success -> {
                            _states.emit(
                                ProductsContract.State.Success(
                                    getSearchedList(query, response.data)?: listOf()
                                )
                            )
                        }

                        is ResultWrapper.Error -> {
                            _states.emit(
                                ProductsContract.State.Error(
                                    response.error.localizedMessage ?: "Error"
                                )
                            )
                        }

                        is ResultWrapper.ServerError -> {
                            _states.emit(ProductsContract.State.Error(response.error.serverMessage?:"Server Error"))
                        }

                        is ResultWrapper.Loading -> {
                            _states.emit(ProductsContract.State.Loading)
                        }

                        else -> {}
                    }
                }
        }
    }

    private fun getSearchedList(query: String, productList: List<Product?>?): List<Product?>?{
        val searchedList = productList?.filter { product ->
            query.let {
                product?.title?.lowercase()?.contains(query.lowercase()) } == true
                    || query.let { product?.description?.lowercase()?.contains(query.lowercase()) } == true
                    || query.let { product?.category?.name?.lowercase()?.contains(query.lowercase()) } == true
                    || query.let { product?.brand?.name?.lowercase()?.contains(query.lowercase()) } == true

        }
        return searchedList
    }

    fun setSearchText(searchText: String) {
        _searchTextLiveData.value = searchText
    }

    fun setBadge(badge: Int){
        _badge.value = badge
    }

}