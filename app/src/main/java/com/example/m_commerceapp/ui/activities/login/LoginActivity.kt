package com.example.m_commerceapp.ui.activities.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.m_commerceapp.ui.showActivitySnackBar
import com.example.m_commerceapp.R
import com.example.m_commerceapp.databinding.ActivityLoginBinding
import com.example.m_commerceapp.ui.activities.home.HomeActivity
import com.example.m_commerceapp.ui.activities.register.RegisterActivity
import com.example.m_commerceapp.ui.userEmail
import com.example.m_commerceapp.ui.userName
import com.example.m_commerceapp.ui.userPhone
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        viewModel = ViewModelProvider(this)[(LoginViewModel::class.java)]

        getAnimImage()
        initViews()
    }

    private fun getAnimImage(){
        // Load the animation
        val anim = AnimationUtils.loadAnimation(this, R.anim.slide_up)

        // Apply the animation to the views
        viewBinding.route.startAnimation(anim)
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }

    private fun initViews() {
        viewBinding.lifecycleOwner = this
        viewBinding.vm = viewModel

        subscribeToLiveData()
    }

    private fun subscribeToLiveData() {

        viewModel.events.observe(this, ::handleEvents)

        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.states.collect {
                    handleViewStates(it)
                }
            }
        }

//        viewModel.toastMessage.observe(this) { message ->
//            message?.let {
//                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
//            }
//        }
    }

    private fun handleEvents(event: LoginContract.Event) {
        when (event) {
            is LoginContract.Event.NavigateToRegister -> navigateToRegister()
            is LoginContract.Event.NavigateToHome -> navigateToHome()
            is LoginContract.Event.showError -> showError(event.message)
        }
    }

    private fun handleViewStates(state: LoginContract.State) {
        val rootView: View = findViewById(android.R.id.content)
        when (state) {
            is LoginContract.State.Loading -> {
                showLoading()
            }
            is LoginContract.State.Success -> {
                Log.d("LoginActivity", "Success state")
//                try {
                    login(state.userToken)
                    rootView.showActivitySnackBar(state.message)
//                }catch (e: Exception){

//                }
            }
            is LoginContract.State.Error -> {
                showError(state.message)
            }
            else -> {}
        }
    }

    private fun showLoading() {
        viewModel.isLoading.observe(this){
            if (it){
                viewBinding.loginButton.visibility = View.GONE
                viewBinding.progressBar.visibility = View.VISIBLE
            }
        }
//        viewBinding.progressBar.visibility = View.VISIBLE
//        viewBinding.registerButton.visibility = View.GONE
//        viewBinding.successView.isVisible = false
//        viewBinding.loadingText.text = message
    }

    private fun showError(message: String) {
        val rootView: View = findViewById(android.R.id.content)
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        rootView.showActivitySnackBar(message)
        viewModel.isLoading.postValue(false)
        viewBinding.loginButton.visibility = View.VISIBLE
        viewBinding.progressBar.visibility = View.GONE
    }

    private fun login(token: String){
        viewModel.login()
        viewModel.name.observe(this){
            userName = it
        }
        viewModel.email.observe(this){
            userEmail = it
        }

        viewModel.phone.observe(this){
            if (it != null)
                userPhone = it
            else
                userPhone = ""
        }

        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("TOKEN", token)
        startActivity(intent)
        finish()
    }

    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
//        finish()
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

}