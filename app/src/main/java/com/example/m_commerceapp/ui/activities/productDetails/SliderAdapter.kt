package com.example.m_commerceapp.ui.activities.productDetails

import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.bumptech.glide.Glide
import com.example.m_commerceapp.R
import com.example.m_commerceapp.databinding.ItemSliderBinding
import com.smarteist.autoimageslider.SliderViewAdapter;


class SliderAdapter(private val sliderList: List<String?>):
    SliderViewAdapter<SliderAdapter.SliderAdapterViewHolder>() {


    // We are inflating the slider_layout
    // inside on Create View Holder method.
    override fun onCreateViewHolder(parent: ViewGroup?): SliderAdapterViewHolder {
        return SliderAdapterViewHolder(ItemSliderBinding.inflate(LayoutInflater.from(parent?.context)))
    }

        // Inside on bind view holder we will
        // set data to item of Slider View.
        override fun onBindViewHolder(viewHolder: SliderAdapterViewHolder, position: Int) {
            val myImage = sliderList[position]
            viewHolder.bind(myImage?:"")

        }

        // this method will return
        // the count of our list.
        override fun getCount(): Int {
            return sliderList.size
        }

         class SliderAdapterViewHolder(private val itemSliderBinding: ItemSliderBinding): ViewHolder(itemSliderBinding.root) {
            // Adapter class for initializing
            // the views of our slider view.
            fun bind(sliderImage: String){
                Glide.with(itemSliderBinding.root)
                    .load(sliderImage)
                    .placeholder(R.drawable.splash) // Placeholder image while loading
                    .into(itemSliderBinding.sliderImage)
                itemSliderBinding.executePendingBindings()
            }
        }

}