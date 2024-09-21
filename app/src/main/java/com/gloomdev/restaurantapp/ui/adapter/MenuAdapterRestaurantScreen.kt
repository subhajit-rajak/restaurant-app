package com.gloomdev.restaurantapp.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gloomdev.restaurantapp.databinding.EachRestuarntProfileMenuBinding
import com.gloomdev.restaurantapp.ui.activities.MenuDetails
import com.gloomdev.restaurantapp.ui.dataclass.MenuRestaurantScreen


class MenuAdapterRestaurantScreen(private val context: Context?,private val menu: List<MenuRestaurantScreen>):RecyclerView.Adapter<MenuAdapterRestaurantScreen.MenuViewHolder>() {
    inner class MenuViewHolder(val binding: EachRestuarntProfileMenuBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: MenuRestaurantScreen) {
            binding.foodName.text = note.foodName
            binding.foodPriceTextView.text = "₹"+note.foodPrice
            Glide.with(context!!).load(note.foodImage).into(binding.orderedFoodItemImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = EachRestuarntProfileMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return menu.size
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menuu = menu[position]
        holder.bind(menuu)

        holder.binding.seeDetailsButton.setOnClickListener {
            val intent = Intent(context, MenuDetails::class.java)

            intent.putExtra("ITEM_KEY",menu[position].key)
            intent.putExtra("RESTAURANT_UID",menu[position].IdOfRestaurant)


            // Start the next activity
            context?.startActivity(intent)
        }
    }


}