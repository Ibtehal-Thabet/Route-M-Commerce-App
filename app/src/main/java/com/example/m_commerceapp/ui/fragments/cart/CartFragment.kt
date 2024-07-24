package com.example.m_commerceapp.ui.fragments.cart

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.m_commerceapp.ui.showSnackBar
import com.example.domain.model.Product
import com.example.domain.model.cart.CartProducts
import com.example.m_commerceapp.R
import com.example.m_commerceapp.databinding.FragmentCartBinding
import com.example.m_commerceapp.ui.activities.home.HomeActivity
import com.example.m_commerceapp.ui.activities.home.HomeActivityViewModel
import com.example.m_commerceapp.ui.activities.productDetails.ProductDetailsActivity
import com.example.m_commerceapp.ui.authorization
import com.example.m_commerceapp.ui.fragments.home.HomeFragment
import com.example.m_commerceapp.ui.numOfCartItems
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartFragment : Fragment() {

    private var _viewBinding: FragmentCartBinding? = null
    private val viewBinding get() = _viewBinding!!

    private lateinit var viewModel: CartViewModel
    private val homeSharedViewModel by viewModels<HomeActivityViewModel>({ requireActivity() })

    private lateinit var adapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[CartViewModel::class.java]
        adapter = CartAdapter(requireContext(), null)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _viewBinding = FragmentCartBinding.inflate(inflater, container, false)

        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeSharedViewModel.badge.observe(viewLifecycleOwner){
           numOfCartItems
        }
        initViews()

    }

    private fun initViews() {

        viewModel.cartEvents.observe(viewLifecycleOwner,::handelEvents)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.cartStates.collect {
                renderViewState(it)
            }
        }

        adapter.onItemProductClickListener = CartAdapter.OnItemProductClickListener { position, product ->
            product?.let {
                showProductActivity(product.product!!)
            }
        }

        viewModel.invokeAction(CartContract.Action.GetAuthCart(requireContext()))
        addOneProductItemToCart()
        removeOneProductItemFromCart()
    }

    private fun handelEvents(event: CartContract.Event) {
        when(event){
            is CartContract.Event.ProductRemovedSuccessfully -> showSnackBar(event.message)
            is CartContract.Event.ProductAddedSuccessfully -> showSnackBar(event.message)
            is CartContract.Event.ProductUpdatedSuccessfully -> showSnackBar(event.message)
        }
    }

    private fun renderViewState(state: CartContract.State) {
        when(state){
            is CartContract.State.Error -> showError(state.message)
            is CartContract.State.Loading -> showLoading()
            is CartContract.State.SuccessLoaded -> bindCart(state.cartItems)
            is CartContract.State.SuccessAdded -> addOneProductItemToCart()
            is CartContract.State.SuccessRemoved -> removeOneProductItemFromCart()
            else -> {}
        }
    }

    private fun bindCart(cartProducts: CartProducts<Product>?) {
        _viewBinding?.successView?.isVisible = true
        _viewBinding?.loadingView?.isVisible = false
        _viewBinding?.errorView?.isVisible = false

        if (numOfCartItems == 0){
            _viewBinding?.cartContent?.root?.isVisible = false
            _viewBinding?.emptyCartImg?.isVisible = true
            _viewBinding?.btnContinue?.isVisible = true
            _viewBinding?.btnContinue?.setOnClickListener{

                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_home_container, HomeFragment())
                transaction.addToBackStack(null)  // Optional: Add to back stack
                transaction.commit()

                // Update selected item in BottomNavigationView
                (requireActivity() as HomeActivity).viewBinding.bottomNavHome.selectedItemId = R.id.ic_home_nav
            }
            return
        }

        viewBinding.cartContent.productsRecyclerView.adapter = adapter
        cartProducts?.products?.let {
            adapter.bindProducts(it)
            homeSharedViewModel.setBadge(it.size)
        }

        viewBinding.cartContent.totalPrice.text = cartProducts?.totalCartPrice.toString()

        adapter.onItemProductClickListener = CartAdapter.OnItemProductClickListener{ position, product ->
            TODO()
        }
    }

    private fun addOneProductItemToCart(){

        adapter.onAddOneProductClickListener = CartAdapter.OnAddOneProductClickListener{ position, product ->
            viewModel.invokeAction(CartContract.Action.UpdateProductQuantityInCart(product, product?.count?.plus(1).toString()))
        }
    }

    private fun removeOneProductItemFromCart() {
        adapter.onRemoveOneProductClickListener = CartAdapter.OnRemoveOneProductClickListener{ position, product ->
            if (product?.count == 1){
                viewModel.invokeAction(CartContract.Action.RemoveItemFromCart(product))
                return@OnRemoveOneProductClickListener
            }
            viewModel.invokeAction(CartContract.Action.UpdateProductQuantityInCart(product, product?.count?.minus(1).toString()))
        }
    }


    private fun showLoading() {
        _viewBinding?.loadingView?.isVisible = true
        _viewBinding?.successView?.isVisible = false
        _viewBinding?.errorView?.isVisible = false
//        _viewBinding?.loadingText?.text= message
    }

    private fun showError(message: String) {
        _viewBinding?.errorView?.isVisible = true
        _viewBinding?.loadingView?.isVisible = false
        _viewBinding?.successView?.isVisible = false
        _viewBinding?.errorText?.text= message
        _viewBinding?.btnTryAgain?.setOnClickListener{
            viewModel.invokeAction(CartContract.Action.GetAuthCart(requireContext()))
        }
    }

    // navigate to product details
    private fun showProductActivity(product: Product) {
        val intent = Intent(activity, ProductDetailsActivity::class.java)
        intent.putExtra("product", product)
        intent.putExtra("authorization", authorization)
        startActivity(intent)
    }
}