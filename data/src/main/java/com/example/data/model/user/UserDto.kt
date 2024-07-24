package com.example.data.model.user

import com.example.domain.model.User
import com.google.gson.annotations.SerializedName

data class UserDto(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("_id")
	val _id: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("phone")
	val phone: String? = null,

	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("rePassword")
	val rePassword: String? = null,

	@field:SerializedName("token")
	var token: String? = null,
){
	fun toUser(): User{
		return User(
			name = name,
			email = email,
			phone = phone,
			token = token
			)
	}
}
