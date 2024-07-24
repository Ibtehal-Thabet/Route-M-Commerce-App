package com.example.data.api

import com.example.data.model.BaseResponse
import com.example.data.model.cart.CartItemDto
import com.example.data.model.cart.CartProductsByIdDto
import com.example.data.model.cart.CartProductsDto
import com.example.data.model.user.UserResponse
import com.example.domain.model.Brand
import com.example.domain.model.cart.CartItem
import com.example.domain.model.Category
import com.example.domain.model.LoginRequest
import com.example.domain.model.Product
import com.example.domain.model.SubCategory
import com.example.domain.model.User
import com.example.domain.model.cart.CartProducts
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface WebService {

    @POST("api/v1/auth/signup")
    suspend fun registerUser(
        @Body user: User): BaseResponse<User>

    @POST("api/v1/auth/signin")
    suspend fun login(
        @Body request: LoginRequest
    ): UserResponse

    @FormUrlEncoded
    @PUT("api/v1/users/updateMe/")
    suspend fun updateProfile(
        @Field("name") name: String? = null,
        @Field("email") email: String? = null,
        @Field("phone") phone: String? = null,
        @Header("token") authorization: String
    ): UserResponse

    @FormUrlEncoded
    @PUT("/api/v1/users/changeMyPassword")
    suspend fun updateAuthPassword(
        @Field("currentPassword") currentPassword: String,
        @Field("password") password: String,
        @Field("rePassword") rePassword: String,
        @Header("token") token: String
    ): UserResponse


    // data
    @GET("api/v1/categories")
    suspend fun getCategories(): BaseResponse<List<Category>>

    @GET("api/v1/subcategories")
    suspend fun getSubCategoriesFromCategory(
        @Query("category") category: String?): BaseResponse<List<SubCategory>>

    @GET("api/v1/products")
    suspend fun getProducts(
        @Query("_id") id: String? = null,
        @Query("category") category: String? = null,
        @Query("subcategory") subcategory: String? = null,
        @Query("brand") brand: String? = null,
        @Query("q") query: String? = null,
        @Header("token") authorization: String): BaseResponse<List<Product>>

    @GET("api/v1/brands")
    suspend fun getBrands(): BaseResponse<List<Brand>>

    // Wish list
    @FormUrlEncoded
    @POST("api/v1/wishlist")
    suspend fun addToWishList(
        @Field("productId") productId: String,
        @Header("token") authorization: String): BaseResponse<List<String>>

    @DELETE("api/v1/wishlist/{productId}")
    suspend fun removeFromWishList(
        @Path("productId") productId: String,
        @Header("token") authorization: String): BaseResponse<List<String>>

    @GET("api/v1/wishlist")
    suspend fun getAuthWishList(
        @Header("token") authorization: String
    ): BaseResponse<List<Product>>

    // Cart
    @FormUrlEncoded
    @POST("api/v1/cart")
    suspend fun addToCart(
        @Field("productId") productId: String,
        @Header("token") authorization: String): BaseResponse<CartProducts<String>>

    @DELETE("api/v1/cart/{productId}")
    suspend fun removeFromCart(
        @Path("productId") productId: String,
        @Header("token") authorization: String): BaseResponse<CartProducts<Product>>

    @GET("api/v1/cart")
    suspend fun getAuthCart(
        @Header("token") authorization: String
    ): BaseResponse<CartProducts<Product>>

    @FormUrlEncoded
    @PUT("api/v1/cart/{cartProductId}")
    suspend fun updateCartProductQuantity(
        @Path("cartProductId") cartProductId: String,
        @Field("count") quantity: String,
        @Header("token") authorization: String
    ): BaseResponse<CartProducts<Product>>

    @DELETE("api/v1/cart")
    suspend fun removeAllCart(
        @Header("token") authorization: String
    ): BaseResponse<List<CartItemDto>>

}