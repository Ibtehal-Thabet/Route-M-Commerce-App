package com.example.m_commerceapp.ui.activities.productList

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.m_commerceapp.ui.showSnackBar
import com.example.domain.model.Brand
import com.example.domain.model.Category
import com.example.domain.model.Product
import com.example.domain.model.SubCategory
import com.example.domain.model.cart.CartItem
import com.example.m_commerceapp.R
import com.example.m_commerceapp.databinding.FragmentProductListBinding
import com.example.m_commerceapp.ui.SharedPreferencesHelper
import com.example.m_commerceapp.ui.activities.productDetails.ProductDetailsActivity
import com.example.m_commerceapp.ui.fragments.home.HomeFragment
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import java.util.Arrays

@AndroidEntryPoint
class ProductListFragment: Fragment() {

    // to avoid the memory leak in viewBinding
    private var _viewBinding: FragmentProductListBinding? = null
    private val viewBinding get() = _viewBinding!!

    private lateinit var viewModel: ProductViewModel
    private lateinit var adapter: ProductListAdapter

    var category : Category? = null
    var brand: Brand? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[ProductViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_product_list,container, false)
        viewBinding.lifecycleOwner = viewLifecycleOwner
//        initViews()

        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ProductListAdapter(requireContext(), null)
        initViews()

    }

    private fun initViews() {

        if (category != null) {
//            category = arguments?.getParcelable("category")!!
            viewModel.subCategoriesList.observe(viewLifecycleOwner){
                bindTabs(it)
            }
            viewModel.invokeAction(ProductsContract.Action.LoadSubCategory(category!!))
            viewModel.invokeAction(
                ProductsContract.Action.LoadCategoryProducts(
                    requireContext(),
                    category!!
                )
            )
//            brand = Brand()

        } else if(brand != null){
            viewModel.invokeAction(
                ProductsContract.Action.LoadBrandProducts(
                    requireActivity(),
                    brand!!
                )
            )
        }

        viewModel.productEvents.observe(viewLifecycleOwner, ::handleEvents)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.productStates.collect {
                handleViewStates(it)
            }
//            }
        }

        adapter.onItemProductClickListener = ProductListAdapter.OnItemProductClickListener{ position, product ->
            product?.let {
                navigateToProductDetails(product)
            }
        }



        addOrRemoveFromWishList()
        addOrRemoveFromCart()
    }

    companion object {
        fun getCategoryInstance(category: Category): ProductListFragment {
            val productListFragment = ProductListFragment()
            productListFragment.category = category
            return productListFragment
        }

        fun getBrandInstance(brand: Brand): ProductListFragment {
            val productListFragment = ProductListFragment()
            productListFragment.brand = brand
            return productListFragment
        }
    }

    private fun handleEvents(event: ProductsContract.Event) {
        when (event) {
            is ProductsContract.Event.NavigateToProductDetails -> {}
            is ProductsContract.Event.AddToCart -> showSnackBar(event.message)
            is ProductsContract.Event.AddToWishList -> showSnackBar(event.message)
            is ProductsContract.Event.RemoveFromCart -> showSnackBar(event.message)
            is ProductsContract.Event.RemoveFromWishList -> showSnackBar(event.message)
            is ProductsContract.Event.UpdatedInCart -> showSnackBar(event.message)
            is ProductsContract.Event.ShowErrorMessage -> showSnackBar(event.message)
        }
    }

    private fun handleViewStates(state: ProductsContract.State) {
        when (state) {
            is ProductsContract.State.Loading -> showLoading()
            is ProductsContract.State.Success -> bindProducts(state.products)
            is ProductsContract.State.Error -> showError(state.message)
            is ProductsContract.State.SuccessAddedOrRemovedFromWishList -> addOrRemoveFromWishList()
            is ProductsContract.State.SuccessAddedOrRemovedFromCart<*> -> addOrRemoveFromCart()
            else -> {}
        }
    }

    private fun addOrRemoveFromWishList() {
        adapter.onAddToWishClickListener = ProductListAdapter.OnAddToWishClickListener { position, product ->
            product?.let {
                if (product.isInWishList){
                    viewModel.invokeAction(ProductsContract.Action.RemoveProductFromWishList(product = product))
//                    viewModel.removeFromWishList(product)
                }else {
                    viewModel.invokeAction(ProductsContract.Action.AddProductToWishList(product = product))
//                    viewModel.addToWishList(product)
                }
            }
        }

    }

    private fun addOrRemoveFromCart() {
        adapter.onAddToCartClickListener = ProductListAdapter.OnAddToCartClickListener { position, product ->
            product?.let {
                val cartItem = CartItem(_id = product._id, price = product.priceAfterDiscount, product = product)
                val cartItemForId = CartItem(_id = product._id, price = product.priceAfterDiscount, product = product._id)
                if (product.isInCart){
                    viewModel.invokeAction(ProductsContract.Action.RemoveProductFromCart(cartItem))

                }else {
                    viewModel.invokeAction(ProductsContract.Action.AddProductToCart(cartItemForId))
//                    viewModel.addToCart(cartItemForId)
                }
            }
        }

        adapter.onAddOneProductClickListener = ProductListAdapter.OnAddOneProductClickListener { position, product ->
            product?.let {
                val newCartQuantity = SharedPreferencesHelper.getItemQuantity(requireActivity(), product._id!!).plus(1)
                val cartItem = CartItem(_id = product._id, price = product.priceAfterDiscount, product = product, count = newCartQuantity)
                viewModel.invokeAction(ProductsContract.Action.UpdateProductCartQuantity(cartItem = cartItem, cartItem.count.toString()))
//                    viewModel.removeFromWishList(product)

            }
        }

        adapter.onRemoveOneProductClickListener = ProductListAdapter.OnRemoveOneProductClickListener { position, product ->
            product?.let {
                val newCartQuantity = SharedPreferencesHelper.getItemQuantity(requireContext(), product._id!!)
                val cartItem = CartItem(_id = product._id, price = product.priceAfterDiscount, product = product, count = newCartQuantity)
                if (newCartQuantity == 1){
                    viewModel.invokeAction(ProductsContract.Action.RemoveProductFromCart(cartItem = cartItem))
                    return@OnRemoveOneProductClickListener
                }
                viewModel.invokeAction(ProductsContract.Action.UpdateProductCartQuantity(cartItem = cartItem, cartItem.count.toString()))
            }
        }
    }

    private fun showError(message: String) {
        viewBinding.loadingView.isVisible = false
        viewBinding.errorView.isVisible = true
        viewBinding.successView.isVisible = false
        viewBinding.errorText.text = message
        viewBinding.btnTryAgain.setOnClickListener {
            viewModel.subCategoriesList.observe(viewLifecycleOwner) { subCategories ->
                bindTabs(subCategories)
            }
//            viewModel.invokeAction(ProductsContract.Action.LoadCategoryProducts(this, category))
        }
    }

    private fun showLoading() {
        viewBinding.loadingView.isVisible = true
        viewBinding.errorView.isVisible = false
        viewBinding.successView.isVisible = false
//        viewBinding.loadingText.text = message
    }

    private fun bindProducts(products: List<Product?>) {
        viewBinding.loadingView.isVisible = false
        viewBinding.errorView.isVisible = false
        viewBinding.successView.isVisible = true

        viewBinding.productsRecyclerView.adapter = adapter
        adapter.bindProducts(products)
    }


    private fun bindTabs(subCategory: List<SubCategory?>?) {
        if (subCategory == null)
            return

        var tab = viewBinding.tabLayout.newTab()
        tab.text = "All"
        tab.tag = "all"
        viewBinding.tabLayout.addTab(tab)
        var layoutParams = LinearLayout.LayoutParams(tab.view.layoutParams)
        layoutParams.marginStart = 12
        layoutParams.marginEnd = 12
        layoutParams.topMargin = 18
        layoutParams.bottomMargin = 12
        tab.view.layoutParams = layoutParams

        subCategory.forEach { subCategory ->
            tab = viewBinding.tabLayout.newTab()
            tab.text = subCategory?.name
            tab.tag = subCategory
            viewBinding.tabLayout.addTab(tab)
            layoutParams = LinearLayout.LayoutParams(tab.view.layoutParams)
            layoutParams.marginStart = 12
            layoutParams.marginEnd = 12
            layoutParams.topMargin = 18
            layoutParams.bottomMargin = 12
            tab.view.layoutParams = layoutParams
        }
        viewBinding.tabLayout.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    val subCategory =if (viewBinding.tabLayout.selectedTabPosition != 0) {
                        tab?.tag as SubCategory
                    }else {
                        tab?.tag
//                        viewModel.loadProductsByCategory(category)
                    }

                    if (viewBinding.tabLayout.selectedTabPosition != 0) {
                        viewModel.invokeAction(ProductsContract.Action.LoadSubCategoryProducts(requireActivity(), tab?.tag as SubCategory))
//                        viewModel.loadProductsBySubCategory(applicationContext, tab?.tag as SubCategory)
                    }
                    else{
                        viewModel.invokeAction(ProductsContract.Action.LoadCategoryProducts(requireActivity(), category!!))
//                        viewModel.loadProductsByCategory(applicationContext, category)
                    }

                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {

                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    val subCategory = if (viewBinding.tabLayout.selectedTabPosition != 0) {
                        tab?.tag as SubCategory
                    }else {
                        tab?.tag
//                        viewModel.loadProductsByCategory(category)
                    }

                    if (viewBinding.tabLayout.selectedTabPosition != 0) {
                        viewModel.invokeAction(ProductsContract.Action.LoadSubCategoryProducts(requireActivity(), tab?.tag as SubCategory))
//                        viewModel.loadProductsBySubCategory(applicationContext, tab?.tag as SubCategory)
                    }
                    else{
                        viewModel.invokeAction(ProductsContract.Action.LoadCategoryProducts(requireActivity(), category!!))
//                        viewModel.loadProductsByCategory(applicationContext, category)
                    }
                }
            }
        )
        viewBinding.tabLayout.getTabAt(0)?.select()
    }

    private fun navigateToProductDetails(product: Product) {
        // go to product details activity
        val intent = Intent(activity, ProductDetailsActivity::class.java)
        intent.putExtra("product", product)
        startActivity(intent)
    }
}