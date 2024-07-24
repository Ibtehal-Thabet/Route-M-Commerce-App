package com.example.m_commerceapp.ui.fragments.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.domain.model.Product
import com.example.m_commerceapp.R
import com.example.m_commerceapp.databinding.FragmentFavBinding
import com.example.m_commerceapp.databinding.FragmentSearchBinding
import com.example.m_commerceapp.ui.activities.home.HomeActivityViewModel
import com.example.m_commerceapp.ui.activities.productDetails.ProductDetailsActivity
import com.example.m_commerceapp.ui.activities.productList.ProductViewModel
import com.example.m_commerceapp.ui.activities.productList.ProductsContract
import com.example.m_commerceapp.ui.authorization
import com.example.m_commerceapp.ui.fragments.favorite.WishListContract
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment: Fragment() {

    private var _viewBinding: FragmentSearchBinding? = null
    private val viewBinding get() = _viewBinding!!

    private lateinit var viewModel: HomeActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[HomeActivityViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        viewBinding.lifecycleOwner = viewLifecycleOwner
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private val adapter = SearchAdapter(null)
    private fun initView(){

        viewModel.searchTextLiveData.observe(viewLifecycleOwner){
            it.let {
                viewLifecycleOwner.lifecycleScope.launch {
                    repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.productStates.collect {
                            renderViewState(it)
                        }
                    }
                }
                viewModel.getSearchedProduct(it!!)
                Log.i("koko", it)
            }
        }

        adapter.onItemProductClickListener = SearchAdapter.OnItemProductClickListener { position, product ->
            product?.let {
                showProductActivity(product)
            }
        }
    }

    private fun renderViewState(state: ProductsContract.State) {
        when(state){
            is ProductsContract.State.Loading -> showLoading()
            is ProductsContract.State.Success -> bindSearchedList(state.products)
            is ProductsContract.State.Error -> showError(state.message)
            else -> {}
        }
    }

    private fun bindSearchedList(products: List<Product?>) {
        _viewBinding?.successView?.isVisible = true
        _viewBinding?.loadingView?.isVisible = false
        _viewBinding?.errorView?.isVisible = false

        _viewBinding?.recyclerSearch?.adapter = adapter
        adapter.bindProducts(products)
    }

    private fun showLoading() {
        _viewBinding?.loadingView?.isVisible = true
        _viewBinding?.successView?.isVisible = false
        _viewBinding?.errorView?.isVisible = false
    }

    private fun showError(message: String) {
        _viewBinding?.errorView?.isVisible = true
        _viewBinding?.loadingView?.isVisible = false
        _viewBinding?.successView?.isVisible = false
        _viewBinding?.errorText?.text= message
    }

    // navigate to product details
    private fun showProductActivity(product: Product) {
        val intent = Intent(activity, ProductDetailsActivity::class.java)
        intent.putExtra("product", product)
        intent.putExtra("authorization", authorization)
        startActivity(intent)
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Remove the observer when the Fragment's view is destroyed
//        viewModel.searchTextLiveData.removeObservers(viewLifecycleOwner)
    }
}