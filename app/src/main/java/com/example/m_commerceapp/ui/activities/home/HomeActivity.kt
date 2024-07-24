package com.example.m_commerceapp.ui.activities.home

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.example.m_commerceapp.R
import com.example.m_commerceapp.databinding.ActivityHomeBinding
import com.example.m_commerceapp.ui.authorization
import com.example.m_commerceapp.ui.fragments.cart.CartFragment
import com.example.m_commerceapp.ui.fragments.favorite.WishListFragment
import com.example.m_commerceapp.ui.fragments.home.HomeFragment
import com.example.m_commerceapp.ui.fragments.profile.ProfileFragment
import com.example.m_commerceapp.ui.fragments.search.SearchFragment
import com.google.android.material.navigation.NavigationBarView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity(),
    NavigationBarView.OnItemSelectedListener{

    lateinit var viewBinding : ActivityHomeBinding
    private lateinit var viewModel: HomeActivityViewModel

    private lateinit var navController: NavController
    private var _listener: NavController.OnDestinationChangedListener? = null
    private val listener get() = _listener!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = DataBindingUtil.setContentView(this,R.layout.activity_home)
        viewModel = ViewModelProvider(this)[(HomeActivityViewModel::class.java)]
        setContentView(viewBinding.root)

        initViews(savedInstanceState)
    }

    private fun initViews(savedInstanceState: Bundle?) {

        // Get User Token
        val token = intent.getStringExtra("TOKEN")
        authorization = token?:""

        //default fragment will open
        if (savedInstanceState == null) {
            pushFragment(HomeFragment())
        }

        // nav click
        val menu = viewBinding.bottomNavHome.menu
        viewBinding.bottomNavHome.selectedItemId = R.id.bottom_nav_home

        val initialItemId = menu.getItem(0).itemId // Assuming first item is selected initially
        updateLabelVisibility(menu, initialItemId)

        // get cart items
        viewModel.getAuthCart(this)
        viewModel.badge.observe(this) {badge ->
            val cartBadge = viewBinding.bottomNavHome.getOrCreateBadge(R.id.ic_cart_nav)

            cartBadge.text = badge.toString()
            cartBadge.backgroundColor = getResources().getColor(R.color.transparent)
            cartBadge.isVisible = if (badge > 0) true else false
        }

        // get wish list products
        viewModel.getAuthWishList(this)

        viewBinding.bottomNavHome.setOnItemSelectedListener{ item ->
            updateLabelVisibility(menu, item.itemId)
            onNavigationItemSelected(item)
            true
        }

        activeSearch()
    }


    private fun activeSearch() {

        viewBinding.edtSearch.onFocusChangeListener = View.OnFocusChangeListener { view, isFocus ->
            val fragmentManager: FragmentManager = this.supportFragmentManager
            val currentFragment: Fragment? = fragmentManager.findFragmentById(R.id.fragment_home_container)
            if (isFocus) {
                pushFragment(SearchFragment())
                viewBinding.bottomNavHome.isVisible = false
                viewBinding.imgSearch.setImageResource(R.drawable.ic_arrow_back)
                viewBinding.imgSearch.setOnClickListener {
                    pushFragment(currentFragment?:HomeFragment())
                    viewBinding.edtSearch.text.clear()
                    view.clearFocus()
                    view.hideKeyboard()
                }
            }else{
                pushFragment(currentFragment?:HomeFragment())
                viewBinding.bottomNavHome.isVisible = true
                viewBinding.imgSearch.setImageResource(R.drawable.ic_search)
            }
        }
        viewBinding.edtSearch.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.setSearchText(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        // hide keyboard
        viewBinding.edtSearch.setOnKeyListener { view, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_BACK) {
                viewBinding.edtSearch.clearFocus()
                view.hideKeyboard()
            }
            true
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
//        NavigationUI.onNavDestinationSelected(item, navController)

        when (item.itemId) {
            R.id.ic_home_nav -> {
                pushFragment(HomeFragment())
                viewBinding.route.isVisible = true
                viewBinding.searchLayout.isVisible = true
                return true
            }
            R.id.ic_cart_nav -> {
                pushFragment(CartFragment())
                viewBinding.route.isVisible = true
                viewBinding.searchLayout.isVisible = true
                return true
            }
            R.id.ic_fav_nav -> {
                pushFragment(WishListFragment())
                viewBinding.route.isVisible = true
                viewBinding.searchLayout.isVisible = true
                return true
            }
            R.id.ic_profile_nav -> {
                viewBinding.route.isVisible = false
                viewBinding.searchLayout.isVisible = false
                pushFragment(ProfileFragment())
                return true
            }
        }
        return false
    }

    private fun pushFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_home_container, fragment)
            .commit()

    }

    private fun updateLabelVisibility(menu: Menu, selectedItemId: Int) {
        for (i in 0 until menu.size()) {
            val menuItem = menu.getItem(i)

            // Set label visibility based on selection
            if (menuItem.itemId == selectedItemId) {
                menuItem.title = "${menuItem.title}"
            } else {
                menuItem.title = menuItem.title.toString().replace(" ", "")
            }
        }
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    override fun onDestroy() {
//        _listener = null
        super.onDestroy()
    }
}