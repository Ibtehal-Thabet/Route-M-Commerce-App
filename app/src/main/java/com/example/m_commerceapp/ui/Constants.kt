package com.example.m_commerceapp.ui

import android.app.Application
import android.content.Context
import android.util.Log

lateinit var authorization: String
var numOfCartItems: Int = 0

lateinit var userName: String
lateinit var userEmail: String
lateinit var userPhone: String


fun saveUserLogged(application: Application,
                   user_name: String,
                   user_email: String,
                   user_phone: String) {

    // Saving user login information
    val sharedPreferences = application.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("userName", user_name)
    editor.putString("userEmail", user_email)
    editor.putString("userPhone", user_phone)
    editor.putString("token", authorization)
    editor.putBoolean("isLoggedIn", true)
    editor.apply()

    // Retrieving user login information
    val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
    if (isLoggedIn) {
        userName = sharedPreferences.getString("userName", "")?:""
        userEmail = sharedPreferences.getString("userEmail", "")?:""
        userPhone = sharedPreferences.getString("userPhone", "")?:""
        // User is logged in, perform actions accordingly
    } else {
        // User is not logged in, redirect to login screen
    }
}


// save wish list
object SharedPreferencesHelper {
    private const val PREF_NAME = "wishlist_prefs"
    private const val PREF_CART_NAME = "cart_prefs"

    fun saveWishlistStatus(context: Context, productId: String, isInWishlist: Boolean) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("wishlist_$productId", isInWishlist).apply()
    }

    fun getWishlistStatus(context: Context, productId: String): Boolean {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(
            "wishlist_$productId",
            false
        )  // Default to false if not found
    }

    fun saveCartStatus(context: Context, productId: String, isInCart: Boolean, quantity: Int) {
        val sharedPreferences = context.getSharedPreferences(PREF_CART_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        with(editor) {
            putBoolean("cart_$productId", isInCart)
            putInt("quantity_${productId}", quantity)
            apply()
        }
    }

    fun getCartStatus(context: Context, productId: String): Boolean {
        val sharedPreferences = context.getSharedPreferences(PREF_CART_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(
            "cart_$productId",
            false
        )  // Default to false if not found
    }

    fun getItemQuantity(context: Context, productId: String): Int {
        val sharedPreferences = context.getSharedPreferences(PREF_CART_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getInt("quantity_${productId}", 0) // Default to 0 if not found
    }

    fun removeCart(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREF_CART_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(PREF_CART_NAME)
        editor.apply()
    }
}
