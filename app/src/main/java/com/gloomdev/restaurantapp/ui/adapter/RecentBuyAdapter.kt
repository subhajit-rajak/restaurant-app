package com.gloomdev.restaurantapp.ui.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gloomdev.restaurantapp.databinding.ItemRecentBuyBinding


class RecentBuyAdapter(
    private var context: Context,
    private var foodNameList: ArrayList<String>,
    private var foodImageList: ArrayList<String>,
    private var foodPricelist: ArrayList<String>,
    private var foodQuantityList: ArrayList<Int>
): RecyclerView.Adapter<RecentBuyAdapter. RecentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentViewHolder {

        val binding = ItemRecentBuyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentViewHolder(binding)
    }

    override fun getItemCount(): Int = foodNameList.size

    override fun onBindViewHolder(holder: RecentViewHolder, position: Int) {
        holder.bind(position)

    }


    inner class RecentViewHolder(private val binding: ItemRecentBuyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {

            binding.apply {
                recentfoodName.text = foodNameList[position]
                recentfoodPrice.text = foodPricelist[position]
                recentfoodQuantity.text = foodQuantityList[position].toString()
                val uriString: String = foodImageList[position]
                val uri = Uri.parse(uriString)
                Glide.with(context)
                    .load(uri)
                     .placeholder(com.denzcoskun.imageslider.R.drawable.default_loading)
                    .into(foodImage)

            }

        }
    }
}