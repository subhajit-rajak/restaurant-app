package com.gloomdev.restaurantapp.ui.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gloomdev.restaurantapp.databinding.ItemBuyAgainBinding

class BuyAgainAdapter(private val buyAgainFoodName: MutableList<String>, private val buyAgainFoodPrice:MutableList<String>, private val buyAgainFoodImage: MutableList<String>,
    private var requireContext:Context): RecyclerView.Adapter<BuyAgainAdapter.BuyAgainViewHolder>(){

    override fun onBindViewHolder (holder: BuyAgainViewHolder, position: Int) {
        holder.bind(buyAgainFoodName[position], buyAgainFoodPrice[position], buyAgainFoodImage [position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuyAgainViewHolder {
        val binding = ItemBuyAgainBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return BuyAgainViewHolder(binding)
    }
    override fun getItemCount():
            Int = buyAgainFoodName.size

   inner class BuyAgainViewHolder(private val binding: ItemBuyAgainBinding): RecyclerView.ViewHolder
        (binding.root) {
        fun bind(foodName: String, foodPrice: String, foodImage: String) {
            binding.buyAgainFoodName.text=foodName
            binding.buyAgainFoodPrice.text =foodPrice
            val uriString = foodImage
            val uri = Uri.parse(uriString)
            Glide.with(requireContext)
                .load(uri)
                .placeholder(com.denzcoskun.imageslider.R.drawable.default_loading)
                .into(binding.buyAgainFoodImage)
        }
    }
}