package com.example.m_commerceapp.ui.fragments.home

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.common.ResultWrapper
import com.example.domain.model.Category
import com.example.domain.model.Product
import com.example.domain.useCases.GetBrandsUseCase
import com.example.domain.useCases.GetCategoriesUseCase
import com.example.domain.useCases.GetProductsUseCase
import com.example.m_commerceapp.ui.DispatchersModule
import com.example.m_commerceapp.ui.SharedPreferencesHelper
import com.example.m_commerceapp.ui.activities.productList.ProductsContract
import com.example.m_commerceapp.ui.authorization
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val categoryUseCase: GetCategoriesUseCase,
    private val getProductsUseCase: GetProductsUseCase,
    private val getBrandUseCase: GetBrandsUseCase,
    @DispatchersModule.IODispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel(), HomeContract.ViewModel, ProductsContract.ViewModel {

    private lateinit var productList: MutableList<Product?>

    private val _state = MutableStateFlow<HomeContract.State>(
        HomeContract.State.Loading
    )
    override val state = _state

    private val _event = MutableLiveData<HomeContract.Event>()
    override val event = _event

    // home product
    private val _productStates = MutableStateFlow<ProductsContract.State>(
        ProductsContract.State.Loading
    )
    override val productStates = _productStates

    private val _productEvents = MutableLiveData<ProductsContract.Event>()
    override val productEvents = _productEvents


    override fun invokeAction(action: HomeContract.Action) {
        when (action) {
            is HomeContract.Action.LoadCategories -> loadCategories()

            is HomeContract.Action.CategoryClicked -> TODO()

            is HomeContract.Action.LoadBrands -> loadBrands()
            else -> {}
        }
    }

    private fun loadCategories(){
        viewModelScope.launch(ioDispatcher) {
//            _state.postValue(HomeContract.State.Loading(message = "Loading..."))
            categoryUseCase.invoke()
                .collect { response ->
                    when (response) {
                        is ResultWrapper.Success -> {
                            _state.emit(HomeContract.State.SuccessCategory(categories = response.data ?: listOf()))
                        }

                        is ResultWrapper.Error -> _state.emit(
                            HomeContract.State.Error(
                                response.error.localizedMessage ?: "Error"
                            )
                        )

                        is ResultWrapper.ServerError -> _state.emit(
                            HomeContract.State.Error(
                                response.error.serverMessage?:"Server Error"
                            )
                        )

                        is ResultWrapper.Loading -> {
                            _state.emit(HomeContract.State.Loading)
                        }

                        else -> {}
                    }
                }
        }
    }

    private fun loadBrands(){
        viewModelScope.launch(ioDispatcher) {
//            _state.postValue(HomeContract.State.Loading(message = "Loading..."))
            getBrandUseCase.invokeBrands()
                .collect { response ->
                    when (response) {
                        is ResultWrapper.Success -> {
                            _state.emit(HomeContract.State.SuccessBrands(brands = response.data ?: listOf()))
                        }

                        is ResultWrapper.Error -> _state.emit(
                            HomeContract.State.Error(
                                response.error.localizedMessage ?: "Error"
                            )
                        )

                        is ResultWrapper.ServerError -> _state.emit(
                            HomeContract.State.Error(
                                response.error.serverMessage?:"Server Error"
                            )
                        )

                        is ResultWrapper.Loading -> {
                            _state.emit(HomeContract.State.Loading)
                        }

                        else -> {}
                    }
                }
        }
    }


    // invoke home product
    override fun invokeAction(action: ProductsContract.Action) {
        when (action) {
            is ProductsContract.Action.LoadCategoryProducts -> {
                loadHomeProducts(action.context)
            }
            is ProductsContract.Action.ProductClicked -> {

            }

            else -> {}
        }
    }

    private fun loadHomeProducts(context: Context) {
        viewModelScope.launch(ioDispatcher) {
            val electronicsId = "6439d2d167d9aa4ca970649f"
            getProductsUseCase.invokeCategoryProducts(categoryId = electronicsId, authorization)
                .collect {  response ->
                    when (response) {
                        is ResultWrapper.Success -> {
                            productList = response.data?.toMutableList()!!
                            _productStates.emit(ProductsContract.State.Success(getProducts()))
                            
                            // Update SharedPreferences with fetched products and wishlist status
                            for (product in response.data!!) {
                                val isInWishlist = SharedPreferencesHelper.getWishlistStatus(context, product?._id!!)
//                                product.isInWishList = isInWishlist
                                // Optionally, save updated product to SharedPreferences (if needed)
                                SharedPreferencesHelper.saveWishlistStatus(context, product._id!!, isInWishlist)
                            }

                        }

                        is ResultWrapper.Error -> {
                            _productStates.emit(
                                ProductsContract.State.Error(
                                    response.error.localizedMessage ?: "Error"
                                )
                            )
                        }
                        is ResultWrapper.ServerError -> {
                            _productStates.emit(ProductsContract.State.Error(response.error.serverMessage?:"Server Error"))
                        }
                        is ResultWrapper.Loading -> {
                            _state.emit(HomeContract.State.Loading)
                        }

                        else -> {}
                    }
                }
        }
    }

    private fun getProducts(): List<Product?> {
        return productList.toList()
    }

}