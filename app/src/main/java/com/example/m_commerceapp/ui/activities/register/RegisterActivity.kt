package com.example.m_commerceapp.ui.activities.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.m_commerceapp.R
import com.example.m_commerceapp.databinding.ActivityRegisterBinding
import com.example.m_commerceapp.ui.activities.home.HomeActivity
import com.example.m_commerceapp.ui.activities.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityRegisterBinding

    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        viewModel = ViewModelProvider(this)[(RegisterViewModel::class.java)]

        initViews()
    }

    private fun initViews() {
        viewBinding.lifecycleOwner = this
        viewBinding.vm = viewModel

        subscribeToLiveData()
    }

    private fun subscribeToLiveData() {
//        viewModel.messageLiveData.observe(this) { message ->
//            showMessage(
//                message = message.message ?: "Something went wrong",
//                posActionName = "Ok",
//                posAction = message.onPosActionClick,
//                negActionName = message.negActionName,
//                negAction = message.onNegActionClick,
//                isCancelable = message.isCancelable
//            )
//
//        }
        viewModel.events.observe(this, ::handleEvents)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.states.collect {
                    handleViewStates(it)
                }
            }
        }


    }

    private fun handleEvents(event: RegisterContract.Event) {
        when (event) {
            is RegisterContract.Event.NavigateToLogin -> navigateToLogin()
        }

    }

    private fun handleViewStates(state: RegisterContract.State) {
        when (state) {
            is RegisterContract.State.Loading -> showLoading()
            is RegisterContract.State.Success -> createUser()
            is RegisterContract.State.Error -> showError(state.message)
        }
    }

    private fun showError(message: String) {
        viewModel.toastMessage.observe(this) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

        }
//        viewBinding.loadingView.isVisible = false
//        viewBinding.errorView.isVisible = true
//        viewBinding.successView.isVisible = false
//        viewBinding.errorText.text = message
//        viewBinding.btnTryAgain.setOnClickListener {
//            viewModel.invokeAction(ProductsContract.Action.LoadCategoryProducts(category))
//        }
    }

    private fun showLoading() {
        viewModel.isLoading.observe(this){
            if (it){
                viewBinding.registerButton.visibility = View.GONE
                viewBinding.progressBar.visibility = View.VISIBLE
            }
        }
//        viewBinding.progressBar.visibility = View.VISIBLE
//        viewBinding.registerButton.visibility = View.GONE
//        viewBinding.successView.isVisible = false
//        viewBinding.loadingText.text = message
    }

    private fun createUser(){
        viewModel.createUser()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }


    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
//        finish()
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}