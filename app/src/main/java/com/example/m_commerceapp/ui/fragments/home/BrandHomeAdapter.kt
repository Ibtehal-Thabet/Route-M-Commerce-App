package com.example.m_commerceapp.ui.fragments.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.domain.model.Brand
import com.example.m_commerceapp.databinding.ItemHomeBrandBinding

class BrandHomeAdapter(private var brandList: List<Brand?>?) : RecyclerView.Adapter<BrandHomeAdapter.BrandHomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrandHomeViewHolder {
        val viewBinding= ItemHomeBrandBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false)
        return BrandHomeViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: BrandHomeViewHolder, position: Int) {
        val itemBrand= brandList?.get(position)!!
        holder.bind(itemBrand)

        onItemBrandClickListener?.let {
            holder.itemBinding.homeBrandLayout.setOnClickListener {
                onItemBrandClickListener?.onItemBrandClick(position, itemBrand)
            }
        }
    }


    override fun getItemCount(): Int = brandList?.size?:0

    fun bindBrands(brands: List<Brand?>?) {
        brandList= brands
        notifyDataSetChanged()
    }

    var onItemBrandClickListener: OnItemBrandClickListener? = null

    fun interface OnItemBrandClickListener {
        fun onItemBrandClick(position: Int, brand: Brand)
    }



    class BrandHomeViewHolder(val itemBinding: ItemHomeBrandBinding): RecyclerView.ViewHolder(itemBinding.root){
        fun bind(brand: Brand){
            itemBinding.brand = brand
            itemBinding.invalidateAll()
//            itemBinding.executePendingBindings()
        }
    }
}