package com.example.m_commerceapp.ui.activities.productDetails

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.m_commerceapp.ui.showActivitySnackBar
import com.example.domain.model.cart.CartItem
import com.example.domain.model.Product
import com.example.domain.model.cart.CartProducts
import com.example.m_commerceapp.R
import com.example.m_commerceapp.databinding.ActivityProductDetailsBinding
import com.example.m_commerceapp.ui.SharedPreferencesHelper
import com.smarteist.autoimageslider.SliderView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductDetailsActivity : AppCompatActivity() {

    private var _viewBinding: ActivityProductDetailsBinding? = null
    private val viewBinding get() = _viewBinding!!

    private lateinit var viewModel: ProductDetailsViewModel

    lateinit var product: Product
    lateinit var authorization: String

    private lateinit var sliderView: SliderView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ProductDetailsViewModel::class.java]
        _viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_product_details)
        setContentView(viewBinding.root)

        initViews()
    }


    private lateinit var sliderAdapter: SliderAdapter
    private fun initViews() {

        product = intent.getParcelableExtra("product")!!
        authorization = intent.getParcelableExtra<Parcelable?>("authorization").toString()

        viewModel.productEvents.observe(this, ::handleEvents)

        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.productStates.collect {
                    handleViewStates(it)
                }
//            }
        }

        viewModel.invokeAction(ProductContract.Action.LoadProduct(this, product))

        enableBackArrowButton(product.category?.name!!)

//        addOrRemoveFromWishList()
//        addOrRemoveFromCart()

    }

    private fun handleEvents(event: ProductContract.Event) {
        val rootView: View = findViewById(android.R.id.content)
        when (event) {
            is ProductContract.Event.AddToCart -> rootView.showActivitySnackBar(event.message)
            is ProductContract.Event.AddToWishList -> rootView.showActivitySnackBar(event.message)
            is ProductContract.Event.RemoveFromCart -> rootView.showActivitySnackBar(event.message)
            is ProductContract.Event.RemoveFromWishList -> rootView.showActivitySnackBar(event.message)
            is ProductContract.Event.UpdatedInCart -> rootView.showActivitySnackBar(event.message)
            is ProductContract.Event.ShowErrorMessage -> rootView.showActivitySnackBar(event.message)
            else -> {}
        }
    }

    private fun handleViewStates(state: ProductContract.State) {
        when (state) {
            is ProductContract.State.Loading -> showLoading()
            is ProductContract.State.Success -> bindProduct()
            is ProductContract.State.Error -> showError(state.message)
            is ProductContract.State.SuccessAddedOrRemovedFromWishList -> addOrRemoveFromWishList()
            is ProductContract.State.SuccessAddedOrRemovedFromCart<*> -> addOrRemoveFromCart()
            else -> {}
        }
    }

    private fun bindProduct() {
        viewBinding.loadingView.isVisible = false
        viewBinding.errorView.isVisible = false
        viewBinding.successView.isVisible = true

        sliderView = viewBinding.productDetailsContent.sliderProductImages
        sliderAdapter = SliderAdapter(product.images?: listOf())
        sliderView.setSliderAdapter(sliderAdapter)
        sliderView.startAutoCycle()

        viewBinding.productDetailsContent.product = product

        addOrRemoveFromWishList()
        addOrRemoveFromCart()
    }

    private fun addOrRemoveFromWishList() {
        val wishlistIcon = viewBinding.productDetailsContent.favButtonDetail
        // Set icon based on wishlist status
        if (SharedPreferencesHelper.getWishlistStatus(this, product._id!!)) {
            wishlistIcon.setImageResource(R.drawable.ic_favorite)
        } else {
            wishlistIcon.setImageResource(R.drawable.ic_favorite_border)
        }

        viewBinding.productDetailsContent.favButtonDetail.setOnClickListener {
            val newWishlistStatus =
                !SharedPreferencesHelper.getWishlistStatus(this, product._id!!)
            SharedPreferencesHelper.saveWishlistStatus(
                this,
                product._id!!,
                newWishlistStatus
            )
            if (product.isInWishList){
                viewModel.invokeAction(ProductContract.Action.RemoveProductFromWishList(product = product))
//                viewModel.removeFromWishList(product)
            }else {
                viewModel.invokeAction(ProductContract.Action.AddProductToWishList(product = product))
//                viewModel.addToWishList(product)
            }
        }
    }


    private fun addOrRemoveFromCart() {
        val cartBtn = viewBinding.productDetailsContent.addToCartDetail
        val quantityLayout = viewBinding.productDetailsContent.updateQuantityLayout
        val cartItem = CartItem(_id = product._id, product = product, price = product.priceAfterDiscount)
        val cartItemForID = CartItem(_id = product._id, product = product._id, price = product.priceAfterDiscount)
//        val newCartQuantity = SharedPreferencesHelper.getItemQuantity(this, product._id!!)

        // Set icon based on cart status
        if (SharedPreferencesHelper.getCartStatus(this, product._id!!)) {
            cartBtn.isVisible = false
            quantityLayout.isVisible = true
            viewBinding.productDetailsContent.productCounter.text = CartProducts<Product>().products?.get(
                CartProducts<Product>().products?.indexOf(cartItem)?:0)?.count.toString()

        } else {
            cartBtn.isVisible = true
            quantityLayout.isVisible = false
            viewBinding.productDetailsContent.productCounter.text = cartItem.count.toString()
        }

        viewBinding.productDetailsContent.addToCartDetail.setOnClickListener {
            val newCartStatus =
                !SharedPreferencesHelper.getCartStatus(this, product._id!!)
            SharedPreferencesHelper.saveCartStatus(
                this,
                product._id!!,
                newCartStatus,
                1
            )
            if (!product.isInCart){
                viewModel.invokeAction(ProductContract.Action.AddProductToCart(cartItem = cartItemForID))
//                viewModel.addToCart(cartItemForID)
                cartBtn.isVisible = false
                quantityLayout.isVisible = true
                val newCartQuantity = SharedPreferencesHelper.getItemQuantity(this, product._id!!)
                viewBinding.productDetailsContent.productCounter.text =  newCartQuantity.toString()

            }
        }

        viewBinding.productDetailsContent.plusProduct.setOnClickListener {
            val newCartQuantity = SharedPreferencesHelper.getItemQuantity(this, product._id!!).plus(1)
            SharedPreferencesHelper.saveCartStatus(
                this,
                product._id!!,
                true,
                newCartQuantity
            )
            viewModel.invokeAction(ProductContract.Action.UpdateProductCartQuantity(cartItem = cartItem, newCartQuantity.toString()))
            viewBinding.productDetailsContent.productCounter.text =  newCartQuantity.toString()
        }

        viewBinding.productDetailsContent.minusProduct.setOnClickListener {
            var newCartQuantity = SharedPreferencesHelper.getItemQuantity(this, product._id!!)
            if (newCartQuantity == 1){
                val newCartStatus =
                    !SharedPreferencesHelper.getCartStatus(this, product._id!!)
                SharedPreferencesHelper.saveCartStatus(
                    this,
                    product._id!!,
                    newCartStatus,
                    0
                )
                cartBtn.isVisible = true
                quantityLayout.isVisible = false
                viewModel.invokeAction(ProductContract.Action.RemoveProductFromCart(cartItem = cartItem))
//                viewModel.removeFromCart(cartItem)
                return@setOnClickListener
            }

            newCartQuantity = SharedPreferencesHelper.getItemQuantity(this, product._id!!).minus(1)
            SharedPreferencesHelper.saveCartStatus(
                this,
                product._id!!,
                true,
                newCartQuantity
            )
            viewModel.invokeAction(ProductContract.Action.UpdateProductCartQuantity(cartItem = cartItem, newCartQuantity.toString()))
            viewBinding.productDetailsContent.productCounter.text =  newCartQuantity.toString()
        }
    }

    private fun showError(message: String) {
        viewBinding.loadingView.isVisible = false
        viewBinding.errorView.isVisible = true
        viewBinding.successView.isVisible = false
        viewBinding.errorText.text = message
        viewBinding.btnTryAgain.setOnClickListener {
            viewModel.invokeAction(ProductContract.Action.LoadProduct(this, product))
        }
    }

    private fun showLoading() {
        viewBinding.loadingView.isVisible = true
        viewBinding.errorView.isVisible = false
        viewBinding.successView.isVisible = false
    }

    private fun enableBackArrowButton(title: String) {
        val toolbar = viewBinding.toolbar
        toolbar.title = title

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener {
            this.onBackPressed()
        }
    }
}