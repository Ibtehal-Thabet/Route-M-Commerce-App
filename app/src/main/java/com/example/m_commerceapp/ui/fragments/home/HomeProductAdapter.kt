package com.example.m_commerceapp.ui.fragments.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.domain.model.Product
import com.example.m_commerceapp.R
import com.example.m_commerceapp.databinding.ItemHomeProductBinding
import com.example.m_commerceapp.ui.SharedPreferencesHelper

class HomeProductAdapter(val context: Context, private var productList: List<Product?>?) : RecyclerView.Adapter<HomeProductAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding= ItemHomeProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false)
        return ViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemProduct = productList?.get(position)!!
        holder.bind(itemProduct)

        val wishlistIcon = holder.itemBinding.favButton

        // Set icon based on wishlist status
        if (SharedPreferencesHelper.getWishlistStatus(context, itemProduct._id!!)) {
            wishlistIcon.setImageResource(R.drawable.ic_favorite)
        } else {
            wishlistIcon.setImageResource(R.drawable.ic_favorite_border)
        }

        wishlistIcon.setOnClickListener {
            val newWishlistStatus = !SharedPreferencesHelper.getWishlistStatus(context, itemProduct._id!!)
            itemProduct.isInWishList = newWishlistStatus
            SharedPreferencesHelper.saveWishlistStatus(context, itemProduct._id!!, newWishlistStatus)
            // Update UI
            notifyItemChanged(position)
        }

//        val wishlistIcon = if (itemProduct.isInWishList) R.drawable.ic_favorite
//        else R.drawable.ic_favorite_border
//        Log.i("is in wishList", itemProduct.isInWishList.toString())
//        holder.itemBinding.favButton.setImageResource(wishlistIcon)
//
//        holder.itemBinding.favButton.setOnClickListener {
//            toggleWishlist(itemProduct._id?:"")
//            updateProduct(productList)
//            notifyItemChanged(position)
//        }

        onItemProductClickListener?.let {
            holder.itemBinding.productCard.setOnClickListener {
                onItemProductClickListener?.onItemProductClick(position, itemProduct)
            }
        }
    }


    override fun getItemCount(): Int = productList?.size?:0

    fun bindHomeProducts(products: List<Product?>?) {
        productList= products
        notifyDataSetChanged()
    }

    fun toggleWishlist(productId: String) {
        val product = productList?.find { it?._id == productId }
        product?.isInWishList = !product?.isInWishList!!
    }

    var onItemProductClickListener: OnItemProductClickListener? = null

    fun interface OnItemProductClickListener {
        fun onItemProductClick(position: Int, product: Product)
    }

    class ViewHolder(val itemBinding: ItemHomeProductBinding): RecyclerView.ViewHolder(itemBinding.root){
        fun bind(product: Product){
            itemBinding.product = product
            // Set the wishlist icon based on the isInWishlist status
            itemBinding.invalidateAll()
            itemBinding.executePendingBindings()
        }
    }
}