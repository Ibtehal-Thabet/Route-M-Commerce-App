package com.example.data.model.category

import com.example.domain.model.Category
import com.google.gson.annotations.SerializedName

data class CategoryDto(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("_id")
	val _id: String? = null,

	@field:SerializedName("slug")
	val slug: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
){
	fun categoryId(name: String?): String{
		if (name.equals("Music"))
			return "6439d61c0049ad0b52b90051"

		if (name.equals("Men's Fashion"))
			return "6439d5b90049ad0b52b90048"

		if (name.equals("Women's Fashion"))
			return "6439d58a0049ad0b52b9003f"

		if (name.equals("SuperMarket"))
			return "6439d41c67d9aa4ca97064d5"

		if (name.equals("Baby & Toys"))
			return "6439d40367d9aa4ca97064cc"

		if (name.equals("Home"))
			return "6439d3e067d9aa4ca97064c3"

		if (name.equals("Books"))
			return "6439d3c867d9aa4ca97064ba"

		if (name.equals("Beauty & Health"))
			return "6439d30b67d9aa4ca97064b1"

		if (name.equals("Mobiles"))
			return "6439d2f467d9aa4ca97064a8"

		if (name.equals("Electronics"))
			return "6439d2d167d9aa4ca970649f"

		return ""
	}
}