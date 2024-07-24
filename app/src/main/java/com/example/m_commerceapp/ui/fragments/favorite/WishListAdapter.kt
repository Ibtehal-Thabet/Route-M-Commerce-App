package com.example.m_commerceapp.ui.fragments.favorite

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.domain.model.Product
import com.example.m_commerceapp.databinding.ItemFavProductBinding
import com.example.m_commerceapp.ui.SharedPreferencesHelper

class WishListAdapter(private val context: Context, private var wishList: List<Product?>?) :
    RecyclerView.Adapter<WishListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding =
            ItemFavProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemProduct = wishList!![position]
        holder.bind(itemProduct)

        onItemProductClickListener?.let {
            holder.itemBinding.wishProductCard.setOnClickListener {
                onItemProductClickListener?.onItemProductClick(position, itemProduct)
            }
        }

        onRemoveProductClickListener?.let {
            holder.itemBinding.unFav.setOnClickListener {
                onRemoveProductClickListener?.onRemoveProductClick(position, itemProduct)
                val newWishlistStatus =
                    !SharedPreferencesHelper.getWishlistStatus(context, itemProduct?._id!!)
                SharedPreferencesHelper.saveWishlistStatus(
                    context,
                    itemProduct._id!!,
                    newWishlistStatus
                )
                Log.i("wish list ad", newWishlistStatus.toString())
                // Update UI
                notifyItemChanged(position)
            }
        }
    }

    override fun getItemCount(): Int = wishList?.size ?: 0

    fun bindProducts(wishProducts: List<Product?>) {
        wishList = wishProducts
        notifyDataSetChanged()
    }


    var onItemProductClickListener: OnItemProductClickListener? = null

    fun interface OnItemProductClickListener {
        fun onItemProductClick(position: Int, product: Product?)
    }

    var onRemoveProductClickListener: OnRemoveProductClickListener? = null

    fun interface OnRemoveProductClickListener {
        fun onRemoveProductClick(position: Int, product: Product?)
    }

    class ViewHolder(val itemBinding: ItemFavProductBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(product: Product?) {
            itemBinding.product = product
            itemBinding.invalidateAll()
        }
    }
}