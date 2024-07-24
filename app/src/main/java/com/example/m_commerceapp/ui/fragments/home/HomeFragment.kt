package com.example.m_commerceapp.ui.fragments.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.domain.model.Brand
import com.example.domain.model.Category
import com.example.domain.model.Product
import com.example.m_commerceapp.ui.activities.productList.ProductsContract
import com.example.m_commerceapp.R
import com.example.m_commerceapp.databinding.FragmentHomeBinding
import com.example.m_commerceapp.ui.activities.productDetails.ProductDetailsActivity
import com.example.m_commerceapp.ui.activities.productList.ProductListActivity
import com.example.m_commerceapp.ui.activities.productList.ProductListFragment
import com.example.m_commerceapp.ui.authorization
import com.smarteist.autoimageslider.SliderView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var sliderView: SliderView
    private val images = listOf(R.drawable.slider_one, R.drawable.slider_two, R.drawable.slider_three)


    private var _viewBinding: FragmentHomeBinding?= null
    private val binding get() = _viewBinding!!

    private lateinit var viewModel: HomeViewModel

    var category = Category()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel= ViewModelProvider(this)[HomeViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _viewBinding = FragmentHomeBinding.inflate(inflater,container,false)
        return _viewBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()

    }

    private var categoriesAdapter = CategoryHomeAdapter(null)
    private val brandsAdapter = BrandHomeAdapter(null)
    private lateinit var sliderAdapter: SliderAdapter

    private fun initViews() {
        sliderView = binding.sliderViewImage
        sliderAdapter = SliderAdapter(images)
        sliderView.setSliderAdapter(sliderAdapter)
        sliderView.startAutoCycle()

        viewModel.event.observe(viewLifecycleOwner,::handelEvents)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect {
                    renderViewState(it)
                }
            }
        }
        viewModel.invokeAction(HomeContract.Action.LoadCategories)
        viewModel.invokeAction(HomeContract.Action.LoadBrands)

        // home product
        viewModel.productEvents.observe(viewLifecycleOwner,::handelProductEvents)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.productStates.collect {
                    renderProductState(it)
                }
            }
        }
        viewModel.invokeAction(ProductsContract.Action.LoadCategoryProducts(requireContext(), category))

    }

    private fun handelEvents(event: HomeContract.Event) {
        when(event){
            is HomeContract.Event.NavigateToProductsCategory -> TODO()
            else -> {}
        }
    }

    private fun renderViewState(state: HomeContract.State) {
        when(state){
            is HomeContract.State.Error -> showError(state.message)
            is HomeContract.State.Loading -> showLoading()
            is HomeContract.State.SuccessCategory -> bindCategories(state.categories)
            is HomeContract.State.SuccessBrands -> bindBrands(state.brands)

            else -> {}
        }
    }

    private fun bindCategories(categories: List<Category?>?) {
        _viewBinding?.successView?.isVisible = true
//        _viewBinding?.loadingView?.isVisible = false
        _viewBinding?.errorView?.isVisible = false

        categoriesAdapter.bindCategories(categories)
        binding.categoriesRv.adapter = categoriesAdapter

        categoriesAdapter.onItemCategoryClickListener = CategoryHomeAdapter.OnItemCategoryClickListener { position, category ->
            showCategoryProductActivity(category)
        }
    }

    private fun bindBrands(brands: List<Brand?>?) {
        _viewBinding?.successView?.isVisible = true
//        _viewBinding?.loadingView?.isVisible = false
        _viewBinding?.errorView?.isVisible = false

        brandsAdapter.bindBrands(brands)
        binding.brandsRv.adapter = brandsAdapter

        brandsAdapter.onItemBrandClickListener = BrandHomeAdapter.OnItemBrandClickListener { position, brand ->
            showBrandProductActivity(brand)
        }
    }

    private fun renderProductState(state: ProductsContract.State?) {
        when(state){
            is ProductsContract.State.Error -> showError(state.message)
            is ProductsContract.State.Loading -> showLoading()
            is ProductsContract.State.Success -> bindHomeProducts(state.products)

            else -> {}
        }
    }

    private fun handelProductEvents(event: ProductsContract.Event?) {
        when(event){
            is ProductsContract.Event.NavigateToProductDetails -> TODO()
            else -> {}
        }
    }

    private fun bindHomeProducts(products: List<Product?>? = null) {
        binding.successView.isVisible = true
//        binding.loadingView.isVisible = false
        binding.errorView.isVisible = false
        val homeProductAdapter = HomeProductAdapter(requireContext(), null)

        homeProductAdapter.bindHomeProducts(products)
        binding.homeProductsRv.adapter = homeProductAdapter
        binding.productNameTv.text = products?.get(1)?.category?.name

        homeProductAdapter.onItemProductClickListener = HomeProductAdapter.OnItemProductClickListener { position, product ->
            showProductActivity(product = product)
        }
    }

    private fun showLoading() {
//        _viewBinding?.loadingView?.isVisible = true
//        _viewBinding?.successView?.isVisible = false
//        _viewBinding?.errorView?.isVisible = false
//        _viewBinding?.loadingTxt?.text= message
    }

    private fun showError(message: String) {
        _viewBinding?.errorView?.isVisible = true
//        _viewBinding?.loadingView?.isVisible = false
        _viewBinding?.successView?.isVisible = false
        _viewBinding?.errorText?.text= message
        _viewBinding?.btnTryAgain?.setOnClickListener{
            viewModel.invokeAction(HomeContract.Action.LoadCategories)
        }
    }

    // navigate to product details
    private fun showProductActivity(product: Product) {
        val intent = Intent(activity, ProductDetailsActivity::class.java)
        intent.putExtra("product", product)
        intent.putExtra("authorization", authorization)
        startActivity(intent)
    }

    private fun showCategoryProductActivity(category: Category) {
        val intent = Intent(activity, ProductListActivity::class.java)
        intent.putExtra("category", category)
        startActivity(intent)
    }

    private fun showBrandProductActivity(brand: Brand) {
        val intent = Intent(activity, ProductListActivity::class.java)
        intent.putExtra("brand", brand)
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
//        _viewBinding?.executePendingBindings()
    }


    override fun onDestroyView() {
//        _viewBinding?.unbind()
//        _viewBinding= null
        super.onDestroyView()
    }
}