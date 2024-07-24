package com.example.m_commerceapp.ui.activities.productList

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.common.ResultWrapper
import com.example.domain.model.Brand
import com.example.domain.model.cart.CartItem
import com.example.domain.model.Category
import com.example.domain.model.Product
import com.example.domain.model.SubCategory
import com.example.domain.useCases.CartUseCase
import com.example.domain.useCases.GetProductsUseCase
import com.example.domain.useCases.GetSubCategoriesUseCase
import com.example.domain.useCases.WishListUseCase
import com.example.m_commerceapp.ui.DispatchersModule
import com.example.m_commerceapp.ui.SharedPreferencesHelper
import com.example.m_commerceapp.ui.activities.productDetails.ProductContract
import com.example.m_commerceapp.ui.authorization
import com.example.m_commerceapp.ui.fragments.cart.CartContract
import com.example.m_commerceapp.ui.fragments.favorite.WishListContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val getSubCategoriesUseCase: GetSubCategoriesUseCase,
    private val wishListUseCase: WishListUseCase,
    private val cartUseCase: CartUseCase,
    @DispatchersModule.IODispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel(), ProductsContract.ViewModel{

    private val _states = MutableStateFlow<ProductsContract.State>(
        ProductsContract.State.Loading
    )
    override val productStates = _states

    private val _events = MutableLiveData<ProductsContract.Event>()
    override val productEvents = _events

    private val _searchTextLiveData = MutableLiveData<String>()
    val searchTextLiveData: LiveData<String> get() = _searchTextLiveData

    private val _subCategoriesList = MutableLiveData<List<SubCategory?>?>(null)
    val subCategoriesList = _subCategoriesList

    private val _categoryLiveData = MutableLiveData<Category?>(null)
    val categoryLiveData: LiveData<Category?> get() = _categoryLiveData


    private val _brandLiveData = MutableLiveData<Brand?>(null)
    val brandLiveData = _brandLiveData

    override fun invokeAction(action: ProductsContract.Action) {
        when (action) {
            is ProductsContract.Action.LoadSubCategory -> loadSubCategories(action.category)

            is ProductsContract.Action.LoadCategoryProducts -> loadProductsByCategory(action.context, action.category)

            is ProductsContract.Action.LoadSubCategoryProducts -> loadProductsBySubCategory(action.context, action.subCategory)

            is ProductsContract.Action.LoadBrandProducts -> loadProductsByBrand(action.context, action.brand)

            is ProductsContract.Action.ProductClicked -> {}

            is ProductsContract.Action.AddProductToWishList -> addToWishList(action.product)

            is ProductsContract.Action.RemoveProductFromWishList -> removeFromWishList(action.product)

            is ProductsContract.Action.AddProductToCart -> addToCart(action.cartItem)

            is ProductsContract.Action.RemoveProductFromCart -> removeFromCart(action.cartItem)

            is ProductsContract.Action.UpdateProductCartQuantity -> updateProductQuantityInCart(action.cartItem, action.quantity)
            else -> {}
        }
    }

    private fun loadSubCategories(category: Category) {

        viewModelScope.launch(ioDispatcher) {

            getSubCategoriesUseCase.invokeSubCategory(categoryId = category._id)
                .collect{ response ->
                    when(response) {
                        is ResultWrapper.Success -> {
                            _subCategoriesList.postValue(response.data)
                            Log.i("vm subcat", response.data.toString())
//                    _states.emit(ProductsContract.State.Success(response.data ?: listOf()))
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

    private fun loadProductsByCategory(context: Context, category: Category) {

        viewModelScope.launch(ioDispatcher) {
            getProductsUseCase.invokeCategoryProducts(categoryId = category._id, authorization = authorization)
                .collect {  response ->
                    when (response) {
                        is ResultWrapper.Success -> {
                            _states.emit(
                                ProductsContract.State.Success(
                                    response.data ?: listOf()
                                )
                            )

                            // Update SharedPreferences with fetched products and wishlist status
                            for (product in response.data!!) {
                                val isInWishlist = SharedPreferencesHelper.getWishlistStatus(context, product?._id!!)
                                product.isInWishList = isInWishlist
                                Log.i("isInWishListPref", isInWishlist.toString())
                                Log.i("isInWishList", product.isInWishList.toString())
                                // Optionally, save updated product to SharedPreferences (if needed)
                                SharedPreferencesHelper.saveWishlistStatus(context, product._id!!, isInWishlist)
                            }

                            // Update SharedPreferences with fetched products and cart status
                            for (product in response.data!!) {
                                val isInCart = SharedPreferencesHelper.getCartStatus(context, product?._id!!)
                                val itemQuantity = SharedPreferencesHelper.getItemQuantity(context, product._id!!)
                                Log.i("isInCartListPref", isInCart.toString())
                                product.isInCart = isInCart
                                Log.i("isInCartList", product.isInCart.toString())
//                                product.isInCart = isInCart
                                // Optionally, save updated product to SharedPreferences (if needed)
                                SharedPreferencesHelper.saveCartStatus(context, product._id!!, isInCart, itemQuantity)
                            }
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

    private fun loadProductsBySubCategory(context: Context, subCategory: SubCategory) {

        viewModelScope.launch(ioDispatcher) {
            getProductsUseCase.invokeSubCategoryProducts(subCategoryId = subCategory._id, authorization = authorization)
                .collect { response ->
                    when (response) {
                        is ResultWrapper.Success -> {
                            _states.emit(
                                ProductsContract.State.Success(
                                    response.data ?: listOf()
                                )
                            )
                            // Update SharedPreferences with fetched products and wishlist status
                            for (product in response.data!! ) {
                                val isInWishlist = SharedPreferencesHelper.getWishlistStatus(context, product?._id!!)
//                                product.isInWishList = isInWishlist
                                // Optionally, save updated product to SharedPreferences (if needed)
                                SharedPreferencesHelper.saveWishlistStatus(context, product._id!!, isInWishlist)
                            }

                            // Update SharedPreferences with fetched products and cart status
                            for (product in response.data!! ) {
                                val isInCart = SharedPreferencesHelper.getCartStatus(context, product?._id!!)
                                val itemQuantity = SharedPreferencesHelper.getItemQuantity(context, product._id!!)
//                                product.isInCart = isInCart
                                // Optionally, save updated product to SharedPreferences (if needed)
                                SharedPreferencesHelper.saveCartStatus(context, product._id!!, isInCart, itemQuantity)
                            }
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

    private fun loadProductsByBrand(context: Context, brand: Brand) {

        viewModelScope.launch(ioDispatcher) {
            getProductsUseCase.invokeBrandProducts(brandId = brand._id, authorization)
                .collect {  response ->
                    when (response) {
                        is ResultWrapper.Success -> {
                            _states.emit(
                                ProductsContract.State.Success(
                                    response.data ?: listOf()
                                )
                            )

                            // Update SharedPreferences with fetched products and wishlist status
                            for (product in response.data!! ) {
                                val isInWishlist = SharedPreferencesHelper.getWishlistStatus(context, product?._id!!)
//                                product.isInWishList = isInWishlist
                                // Optionally, save updated product to SharedPreferences (if needed)
                                SharedPreferencesHelper.saveWishlistStatus(context, product._id!!, isInWishlist)
                            }

                            // Update SharedPreferences with fetched products and cart status
                            for (product in response.data!! ) {
                                val isInCart = SharedPreferencesHelper.getCartStatus(context, product?._id!!)
                                val itemQuantity = SharedPreferencesHelper.getItemQuantity(context, product._id!!)
//                                product.isInCart = isInCart
                                // Optionally, save updated product to SharedPreferences (if needed)
                                SharedPreferencesHelper.saveCartStatus(context, product._id!!, isInCart, itemQuantity)
                            }
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

    fun addToWishList(product: Product) {

        viewModelScope.launch(ioDispatcher) {

            wishListUseCase.addToWishList(product = product, authorization = authorization)
                .collect{ response ->
                    when(response) {
                        is ResultWrapper.Success -> {
                            Log.i("Added succ to wishlist", response.data.toString())
                            _events.postValue(ProductsContract.Event.AddToWishList(response.message?:"Product added successfully to your wishlist"))
                            _states.emit(ProductsContract.State.SuccessAddedOrRemovedFromWishList(product))
                        }
                        else -> {
                            _events.postValue(ProductsContract.Event.ShowErrorMessage("Something Wrong!"))
                        }
                    }
                }
        }
    }

    fun removeFromWishList(product: Product) {

        viewModelScope.launch(ioDispatcher) {

            wishListUseCase.removeFromWishList(product = product, authorization = authorization)
                .collect{ response ->
                    when(response) {
                        is ResultWrapper.Success -> {
                            _events.postValue(ProductsContract.Event.RemoveFromWishList(response.message?:"Product removed successfully from your wishlist"))
                            _states.emit(ProductsContract.State.SuccessAddedOrRemovedFromWishList(product))
                        }
                        else -> {
                            _events.postValue(ProductsContract.Event.ShowErrorMessage("Something Wrong!"))
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
                            Log.i("add response", response.data.toString())
                            _events.postValue(ProductsContract.Event.AddToCart(response.message?:"Product added successfully to your cart"))
                            _states.emit(ProductsContract.State.SuccessAddedOrRemovedFromCart(cartItem))
                        }
                        else -> {
                            _events.postValue(ProductsContract.Event.ShowErrorMessage("Something Wrong!"))
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
                            _events.postValue(ProductsContract.Event.RemoveFromCart(response.message?:"Product removed successfully from cart"))
                            _states.emit(ProductsContract.State.SuccessAddedOrRemovedFromCart(cartItem))
                        }
                        else -> {
                            _events.postValue(ProductsContract.Event.ShowErrorMessage("Something Wrong!"))
                        }
                    }
                }
        }
    }

    fun updateProductQuantityInCart(cartItem: CartItem<Product>?, quantity: String) {

        viewModelScope.launch(ioDispatcher) {

            cartUseCase.updateCartProductQuantity(cartItemId = cartItem?.product?._id!!, quantity = quantity, authorization = authorization)
                .collect{ response ->
                    Log.i("update", response.toString())
                    when(response) {
                        is ResultWrapper.Success -> {
                            _events.postValue(ProductsContract.Event.UpdatedInCart("Product quantity updated successfully"))
                            _states.emit(ProductsContract.State.SuccessAddedOrRemovedFromCart(cartItem))
                            Log.i("quantity response", response.data.toString())
                        }
                        else -> {
                            _events.postValue(ProductsContract.Event.ShowErrorMessage("Something Wrong!"))
                        }
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
                product?.title?.lowercase()?.contains(query.lowercase())
            } == true ||
                    query.let {
                        product?.description?.lowercase()?.contains(query.lowercase())
                    } == true
        }
        return searchedList
    }

    fun setSearchText(searchText: String) {
        _searchTextLiveData.value = searchText
    }

}