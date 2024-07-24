package com.example.m_commerceapp.ui.activities.productList

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.domain.model.Product
import com.example.domain.model.cart.CartItem
import com.example.m_commerceapp.R
import com.example.m_commerceapp.databinding.ItemProductBinding
import com.example.m_commerceapp.ui.SharedPreferencesHelper

class ProductListAdapter(private val context: Context, var productList: List<Product?>?) :
    RecyclerView.Adapter<ProductListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding =
            ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemProduct = productList!![position]
        holder.bind(itemProduct)

        onItemProductClickListener?.let {
            holder.itemBinding.productCard.setOnClickListener {
                onItemProductClickListener?.onItemProductClick(position, itemProduct)
            }
        }

        val wishlistIcon = holder.itemBinding.favButton

        // Set icon based on wishlist status
        if (SharedPreferencesHelper.getWishlistStatus(context, itemProduct?._id!!)) {
            wishlistIcon.setImageResource(R.drawable.ic_favorite)
        } else {
            wishlistIcon.setImageResource(R.drawable.ic_favorite_border)
        }

        onAddToWishClickListener.let {
            wishlistIcon.setOnClickListener {
                onAddToWishClickListener?.onAddToWishClickListener(position, itemProduct)
                val newWishlistStatus =
                    !SharedPreferencesHelper.getWishlistStatus(context, itemProduct._id!!)
                itemProduct.isInWishList = newWishlistStatus
                SharedPreferencesHelper.saveWishlistStatus(
                    context,
                    itemProduct._id!!,
                    newWishlistStatus
                )
                // Update UI
                notifyItemChanged(position)
            }
        }

//        val cartButton = holder.itemBinding.addToCartBtn
//        val quantityLayout = holder.itemBinding.updateQuantityLayout
        // Set icon based on cart status
        if (SharedPreferencesHelper.getCartStatus(context, itemProduct._id!!)) {
            holder.itemBinding.addToCartBtn.visibility = View.GONE
            holder.itemBinding.updateQuantityLayout.visibility = View.VISIBLE
            val cartQuantity = SharedPreferencesHelper.getItemQuantity(context, itemProduct._id!!)
            holder.itemBinding.productCounter.text = cartQuantity.toString()
        } else {
            holder.itemBinding.addToCartBtn.visibility = View.VISIBLE
            holder.itemBinding.updateQuantityLayout.visibility = View.GONE
        }

        // add to cart
        onAddToCartClickListener.let {
            holder.itemBinding.addToCartBtn.setOnClickListener {
                onAddToCartClickListener?.onAddToCartClickListener(position, itemProduct)
                val newCartStatus =
                    !SharedPreferencesHelper.getCartStatus(context, itemProduct._id!!)
                itemProduct.isInCart = newCartStatus
                SharedPreferencesHelper.saveCartStatus(
                    context,
                    itemProduct._id!!,
                    newCartStatus,
                    1
                )
                // Update UI
                notifyItemChanged(position)

            }
        }

        // plus product click
        onAddOneProductClickListener?.let {
            holder.itemBinding.plusProduct.setOnClickListener {
                onAddOneProductClickListener?.onAddOneProductClick(position, itemProduct)
                val newCartQuantity = SharedPreferencesHelper.getItemQuantity(context, itemProduct._id!!).plus(1)
                SharedPreferencesHelper.saveCartStatus(
                    context,
                    itemProduct._id!!,
                    true,
                    newCartQuantity
                )
                holder.itemBinding.productCounter.text = newCartQuantity.toString()
                notifyItemChanged(position)
            }
        }

        // minus product click
        onRemoveOneProductClickListener?.let {
            holder.itemBinding.minusProduct.setOnClickListener {
                onRemoveOneProductClickListener?.onRemoveOneProductClick(position, itemProduct)

                var newCartQuantity = SharedPreferencesHelper.getItemQuantity(context, itemProduct._id!!)
                if (newCartQuantity == 1){
                    holder.itemBinding.addToCartBtn.visibility = View.VISIBLE
                    holder.itemBinding.updateQuantityLayout.visibility = View.GONE

                    val newCartStatus = !SharedPreferencesHelper.getCartStatus(context, itemProduct._id!!)
                    itemProduct.isInCart = newCartStatus
                    SharedPreferencesHelper.saveCartStatus(
                        context,
                        itemProduct._id!!,
                        newCartStatus,
                        0
                    )
                    notifyItemChanged(position)
                    return@setOnClickListener
                }
                newCartQuantity = SharedPreferencesHelper.getItemQuantity(context, itemProduct._id!!).minus(1)
                SharedPreferencesHelper.saveCartStatus(
                    context,
                    itemProduct._id!!,
                    true,
                    newCartQuantity
                )
                holder.itemBinding.productCounter.text = newCartQuantity.toString()
                notifyItemChanged(position)
//                }
            }
        }
    }

    override fun getItemCount(): Int = productList?.size ?: 0

    fun bindProducts(products: List<Product?>) {
        productList = products
        notifyDataSetChanged()
    }


    var onItemProductClickListener: OnItemProductClickListener? = null

    fun interface OnItemProductClickListener {
        fun onItemProductClick(position: Int, product: Product?)
    }

    // add to wish list click
    var onAddToWishClickListener: OnAddToWishClickListener? = null

    fun interface OnAddToWishClickListener {
        fun onAddToWishClickListener(position: Int, product: Product?)
    }

    // add to cart click
    var onAddToCartClickListener: OnAddToCartClickListener? = null

    fun interface OnAddToCartClickListener {
        fun onAddToCartClickListener(position: Int, product: Product?)
    }

    // plus product
    var onAddOneProductClickListener: OnAddOneProductClickListener? = null

    fun interface OnAddOneProductClickListener {
        fun onAddOneProductClick(position: Int, cartItem: Product?)
    }

    // minus product
    var onRemoveOneProductClickListener: OnRemoveOneProductClickListener? = null

    fun interface OnRemoveOneProductClickListener {
        fun onRemoveOneProductClick(position: Int, cartItem: Product?)
    }

    class ViewHolder(val itemBinding: ItemProductBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(product: Product?) {
            itemBinding.product = product
            itemBinding.invalidateAll()
        }
    }
}