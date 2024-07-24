package com.example.m_commerceapp.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.example.m_commerceapp.R
import com.example.m_commerceapp.ui.activities.login.LoginActivity

//@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        startLoginActivity()
    }

    private fun startLoginActivity() {
        // Load the animation
//        val anim = AnimationUtils.loadAnimation(this, R.anim.slide_up)

        // Apply the animation to the ImageView
//        routeImg.startAnimation(anim)

        // Set a listener to navigate to the login screen when the animation finishes
//        anim.setAnimationListener(object : Animation.AnimationListener {
//            override fun onAnimationStart(animation: Animation?) {}
//
//            override fun onAnimationEnd(animation: Animation?) {
//                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
//                finish() // Finish splash activity
//            }
//
//            override fun onAnimationRepeat(animation: Animation?) {}
//        })

        Handler(Looper.getMainLooper())
            .postDelayed({
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }, 2000)
    }
}