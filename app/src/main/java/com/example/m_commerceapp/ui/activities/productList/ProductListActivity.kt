package com.example.m_commerceapp.ui.activities.productList

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.domain.model.Brand
import com.example.domain.model.Category
import com.example.m_commerceapp.R
import com.example.m_commerceapp.databinding.ActivityProductListBinding
import com.example.m_commerceapp.ui.fragments.search.SearchFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductListActivity: AppCompatActivity() {

    // to avoid the memory leak in viewBinding
    private var _viewBinding: ActivityProductListBinding? = null
    private val viewBinding get() = _viewBinding!!

    private lateinit var viewModel: ProductViewModel

    lateinit var category : Category
    lateinit var brand: Brand

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ProductViewModel::class.java]
        _viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_product_list)
        setContentView(viewBinding.root)


        initViews(savedInstanceState)

    }

    private fun initViews(savedInstanceState: Bundle?) {
        viewBinding.searchLayout.isVisible = false

        if (intent != null && intent.hasExtra("category")) {
            category = intent.getParcelableExtra("category")!!
            enableBackArrowButton(category.name!!)
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, ProductListFragment.getCategoryInstance(category))
                .addToBackStack(null)
                .commit()

            brand = Brand()
////            viewModel.setBrand(brand)
        }
        else if(intent != null && intent.hasExtra("brand")) {
            brand = intent.getParcelableExtra("brand")!!
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, ProductListFragment.getBrandInstance(brand))
                .addToBackStack(null)
                .commit()
//
////            viewModel.setCategory(null)
//            viewModel.setBrand(brand)
            enableBackArrowButton(brand.name!!)
            category = Category()
        }

        viewBinding.search.setOnClickListener {
            navigateToSearchFragment()
        }

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

    private fun navigateToSearchFragment() {
//        viewBinding.edtSearch.onFocusChangeListener = View.OnFocusChangeListener { view, isFocus ->
//            if (isFocus) {
        viewBinding.searchLayout.isVisible = true
        viewBinding.appBar.isVisible = false
        viewBinding.imgBack.setOnClickListener {
            getFragmentManager().popBackStack()
            viewBinding.edtSearch.text.clear()
            viewBinding.searchLayout.isVisible = false
            viewBinding.appBar.isVisible = true
            it.clearFocus()
            it.hideKeyboard()
        }
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, SearchFragment())
            .commit()
//        pushFragment()

        viewBinding.edtSearch.onFocusChangeListener = View.OnFocusChangeListener { view, isFocus ->
            if (view.requestFocus()) {
                val imm = getSystemService(InputMethodManager::class.java)
                imm.showSoftInput(view, 0)
                WindowCompat.getInsetsController(window, view).show(WindowInsetsCompat.Type.ime())
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
                _viewBinding?.edtSearch?.clearFocus()
                view.hideKeyboard()
            }
            true
        }
    }

    private fun pushFragment(fragment: Fragment) {


    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
//        _viewBinding = null
    }

}