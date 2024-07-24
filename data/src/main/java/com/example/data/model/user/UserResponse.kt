package com.example.data.model.user

import com.google.gson.annotations.SerializedName

data class UserResponse(

	@field:SerializedName("metadata")
	val metadata: Metadata? = null,

	@field:SerializedName("totalUsers")
	val totalUsers: Int? = null,

	@SerializedName("statusMsg")
	val statusMessage: String?=null,

	@SerializedName("message")
	val message: String?= null,

	@SerializedName("user")
	val user: UserDto?= null,

	@SerializedName("token")
	val token: String?= null

//	@field:SerializedName("users")
//	val users: List<UserDto?>? = null

)