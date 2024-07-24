package com.example.m_commerceapp.ui.fragments.favorite

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
import com.example.m_commerceapp.ui.showSnackBar
import com.example.domain.model.Product
import com.example.m_commerceapp.R
import com.example.m_commerceapp.databinding.FragmentFavBinding
import com.example.m_commerceapp.ui.SharedPreferencesHelper
import com.example.m_commerceapp.ui.activities.home.HomeActivity
import com.example.m_commerceapp.ui.activities.productDetails.ProductDetailsActivity
import com.example.m_commerceapp.ui.authorization
import com.example.m_commerceapp.ui.fragments.home.HomeFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WishListFragment : Fragment() {

    private var _viewBinding: FragmentFavBinding? = null
    private val viewBinding get() = _viewBinding!!

    private lateinit var viewModel: WishListViewModel

    private lateinit var adapter: WishListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[WishListViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _viewBinding = FragmentFavBinding.inflate(inflater, container, false)
        adapter = WishListAdapter( requireContext(), null)

        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()

    }

    private fun initViews() {

        viewModel.wishEvents.observe(viewLifecycleOwner,::handelEvents)
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.wishStates.collect {
                    renderViewState(it)
                }
            }
        }

        adapter.onItemProductClickListener = WishListAdapter.OnItemProductClickListener { position, product ->
            product?.let {
                showProductActivity(product)
            }
        }

        viewModel.getAuthWishList(requireContext())
        removeProductFromWishList()
    }

    private fun handelEvents(event: WishListContract.Event) {
        when(event){
            is WishListContract.Event.AddedOrRemoveToCartSuccessfully -> showSnackBar(event.message)
            is WishListContract.Event.ProductRemovedSuccessfully -> showSnackBar(event.message)
            else -> {}
        }
    }

    private fun renderViewState(state: WishListContract.State) {
        when(state){
            is WishListContract.State.Error -> showError(state.message)
            is WishListContract.State.Loading -> showLoading()
            is WishListContract.State.SuccessLoaded -> bindWishList(state.products)
            is WishListContract.State.SuccessAdded -> TODO()
            is WishListContract.State.SuccessRemoved -> removeProductFromWishList()
        }
    }


    private fun bindWishList(products: List<Product?>) {
        _viewBinding?.successView?.isVisible = true
        _viewBinding?.loadingView?.isVisible = false
        _viewBinding?.errorView?.isVisible = false

        adapter.bindProducts(products)
        viewBinding.productsRecyclerView.adapter = adapter

        if (adapter.itemCount == 0){
            _viewBinding?.emptyWishlistImg?.isVisible = true
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
            viewModel.invokeAction(WishListContract.Action.GetAuthWishList(requireContext()))
        }
    }

    private fun removeProductFromWishList() {

        adapter.onRemoveProductClickListener =
            WishListAdapter.OnRemoveProductClickListener { position, product ->
                viewModel.invokeAction(WishListContract.Action.RemoveFromWishList(product))
                viewModel.removeFromWishList(product)

                val newWishlistStatus =
                    !SharedPreferencesHelper.getWishlistStatus(requireContext(), product?._id!!)
                SharedPreferencesHelper.saveWishlistStatus(
                    requireContext(),
                    product._id!!,
                    newWishlistStatus
                )
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