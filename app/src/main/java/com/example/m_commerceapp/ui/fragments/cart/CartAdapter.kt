package com.example.m_commerceapp.ui.fragments.cart

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.domain.model.Product
import com.example.domain.model.cart.CartItem
import com.example.m_commerceapp.R
import com.example.m_commerceapp.databinding.ItemCartProductBinding
import com.example.m_commerceapp.ui.SharedPreferencesHelper
import com.example.m_commerceapp.ui.bindImageWithUrl

class CartAdapter(private val context: Context, private var cartList: List<CartItem<Product>?>?) :
    RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding =
            ItemCartProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemProduct = cartList!![position]
        holder.bind(context, itemProduct)

        onItemProductClickListener?.let {
            holder.itemBinding.cartCard.setOnClickListener {
                onItemProductClickListener?.onItemProductClick(position, itemProduct)
            }
        }

        // plus product click
        onAddOneProductClickListener?.let {
            holder.itemBinding.plusProduct.setOnClickListener {
                onAddOneProductClickListener?.onAddOneProductClick(position, itemProduct)
                itemProduct?.count?.plus(1)
                val productCount = itemProduct?.count?.plus(1)
                holder.itemBinding.productCounter.text = productCount.toString()
                SharedPreferencesHelper.saveCartStatus(
                    context,
                    itemProduct?._id!!,
                    true,
                    productCount!!
                )
                notifyItemChanged(position)
            }
        }

        // minus product click
        onRemoveOneProductClickListener?.let {
            holder.itemBinding.minusProduct.setOnClickListener {
                onRemoveOneProductClickListener?.onRemoveOneProductClick(position, itemProduct)
                if (itemProduct?.count!! > 1) {
                    itemProduct.count?.minus(1)
                    val productCount = itemProduct.count?.minus(1)
                    holder.itemBinding.productCounter.text = productCount.toString()
                    SharedPreferencesHelper.saveCartStatus(
                        context,
                        itemProduct._id!!,
                        true,
                        productCount!!
                    )
                    notifyItemChanged(position)
                }else{
                    val newCartStatus = !SharedPreferencesHelper.getCartStatus(context, itemProduct._id!!)
                    SharedPreferencesHelper.saveCartStatus(
                        context,
                        itemProduct._id!!,
                        newCartStatus,
                        itemProduct.count!!
                    )
                    notifyItemChanged(position)
                }
            }
        }

    }

    override fun getItemCount(): Int = cartList?.size ?: 0


    fun bindProducts(cartProducts: List<CartItem<Product>?>) {
        cartList = cartProducts
        notifyDataSetChanged()
    }


    // item click
    var onItemProductClickListener: OnItemProductClickListener? = null

    fun interface OnItemProductClickListener {
        fun onItemProductClick(position: Int, cartItem: CartItem<Product>?)
    }

    // plus product
    var onAddOneProductClickListener: OnAddOneProductClickListener? = null

    fun interface OnAddOneProductClickListener {
        fun onAddOneProductClick(position: Int, cartItem: CartItem<Product>?)
    }

    // minus product
    var onRemoveOneProductClickListener: OnRemoveOneProductClickListener? = null

    fun interface OnRemoveOneProductClickListener {
        fun onRemoveOneProductClick(position: Int, cartItem: CartItem<Product>?)
    }

    class ViewHolder(val itemBinding: ItemCartProductBinding) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(context: Context, cartItem: CartItem<Product>?) {
            bindImageWithUrl(itemBinding.productImage, cartItem?.product?.imageCover!!, ContextCompat.getDrawable(context, R.drawable.splash))
            itemBinding.productName.text = cartItem.product?.title
            itemBinding.productPrice.text = cartItem.price.toString()
            itemBinding.productQuantity.text = cartItem.product?.quantity.toString()
            itemBinding.productRate.text = cartItem.product?.ratingsAverage.toString()
            itemBinding.productCounter.text = cartItem.count.toString()
            itemBinding.invalidateAll()
        }
    }
}