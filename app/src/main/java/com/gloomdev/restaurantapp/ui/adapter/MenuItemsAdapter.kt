package com.gloomdev.restaurantapp.ui.adapter

import android.content.Context
import android.content.Intent
import com.gloomdev.restaurantapp.ui.dataclass.MenuItems
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.gloomdev.restaurantapp.R
import com.gloomdev.restaurantapp.ui.activities.MenuDetails


class MenuItemsAdapter(private var menuList: List<MenuItems>, private val requireContext:Context) : Adapter<MenuItemsAdapter.MenuViewHolder>() {

    class MenuViewHolder(itemView: View) : ViewHolder(itemView) {
        val restaurantNameTxt: TextView = itemView.findViewById(R.id.searchNameTxt)
        val availableFoodTxt: TextView = itemView.findViewById(R.id.searchDescription)
        val priceTxt: TextView = itemView.findViewById(R.id.searchPriceTxt)
        val searchFoodImage: ImageView = itemView.findViewById(R.id.searchfoodImage)
        val dishCardView: CardView = itemView.findViewById(R.id.dishCardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.searchview_item, parent, false)
        return MenuViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  menuList.size;
    }


    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menuItem = menuList[position]

        // Set food name, price, and description
        holder.restaurantNameTxt.text = menuItem.foodName
        holder.availableFoodTxt.text = menuItem.foodDescription
        holder.priceTxt.text = "₹"+ menuItem.foodPrice

        Glide.with(holder.itemView.context)
            .load(menuItem.foodImage)
            .placeholder(com.denzcoskun.imageslider.R.drawable.default_loading) // A placeholder image until the actual image loads
            .into(holder.searchFoodImage)

        /* Tap to view menu details and add to cart logic
        holder.dishCardView.setOnClickListener {
            val intent = Intent(requireContext, MenuDetails::class.java)
            intent.putExtra("ITEM_KEY",menuItem.key)
            intent.putExtra("RESTAURANT_UID",menuItem.IdOfRestaurant)
            requireContext.startActivity(intent)
        }
         */
    }

}