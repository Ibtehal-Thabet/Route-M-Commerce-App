package com.example.m_commerceapp.ui.fragments.home

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.domain.model.Category
import com.example.m_commerceapp.databinding.ItemHomeCategoryBinding

class CategoryHomeAdapter(private var categoryList: List<Category?>?) : RecyclerView.Adapter<CategoryHomeAdapter.CategoryHomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHomeViewHolder {
        val viewBinding= ItemHomeCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false)
        return CategoryHomeViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: CategoryHomeViewHolder, position: Int) {
        val itemCategory= categoryList?.get(position)!!
        holder.bind(itemCategory)

        onItemCategoryClickListener?.let {
            holder.itemBinding.homeCategoryLayout.setOnClickListener {
                onItemCategoryClickListener?.onItemCategoryClick(position, itemCategory)
            }
        }
    }


    override fun getItemCount(): Int = categoryList?.size?:0

    fun bindCategories(categories: List<Category?>?) {
        categoryList= categories
        notifyDataSetChanged()
    }

    var onItemCategoryClickListener: OnItemCategoryClickListener? = null

    fun interface OnItemCategoryClickListener {
        fun onItemCategoryClick(position: Int, category: Category)
    }



    class CategoryHomeViewHolder(val itemBinding: ItemHomeCategoryBinding): RecyclerView.ViewHolder(itemBinding.root){
        fun bind(category: Category){
            itemBinding.category = category
            itemBinding.invalidateAll()
//            itemBinding.executePendingBindings()
        }
    }
}