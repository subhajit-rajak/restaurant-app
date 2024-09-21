package com.gloomdev.restaurantapp.ui.adapter

import android.content.Context
import android.content.Intent
import com.gloomdev.restaurantapp.ui.dataclass.MenuItems
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.gloomdev.restaurantapp.R
import com.gloomdev.restaurantapp.ui.dataclass.ItemDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.gloomdev.restaurantapp.ui.activities.MenuDetails


class MenuItemsAdapter(
    private var menuList: List<MenuItems>,
    private val requireContext: Context
) : Adapter<MenuItemsAdapter.MenuViewHolder>() {

    class MenuViewHolder(itemView: View) : ViewHolder(itemView) {
        val restaurantNameTxt: TextView = itemView.findViewById(R.id.searchNameTxt)
        val availableFoodTxt: TextView = itemView.findViewById(R.id.searchDescription)
        val priceTxt: TextView = itemView.findViewById(R.id.searchPriceTxt)
        val searchFoodImage: ImageView = itemView.findViewById(R.id.searchfoodImage)
        val searchAddToCart: Button = itemView.findViewById(R.id.searchAddToCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.searchview_item, parent, false)
        return MenuViewHolder(view)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menuItem = menuList[position]

        // Set food name, price, and description
        holder.restaurantNameTxt.text = menuItem.foodName
        holder.availableFoodTxt.text = menuItem.foodDescription
        holder.priceTxt.text = "₹" + menuItem.foodPrice

        Glide.with(holder.itemView.context)
            .load(menuItem.foodImage)
            .into(holder.searchFoodImage)

        // Handle Add to Cart functionality
        holder.searchAddToCart.setOnClickListener {
            val sharedPreferences = requireContext.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
            val mAuth = FirebaseAuth.getInstance()
            val userId = mAuth.currentUser?.uid
            val userIdOfRestaurant = sharedPreferences.getString("userIdOfRestaurant", null)

            if (userId != null && userIdOfRestaurant != null) {
                val database = FirebaseDatabase.getInstance().reference
                val cartItemRef = database.child("Customers").child("customerDetails")
                    .child(userId).child("CartItems").child(userIdOfRestaurant).push()

                // Create ItemDetails object
                val cartItem = ItemDetails(
                    userUid = userId,
                    foodName = menuItem.foodName,
                    foodPrice = menuItem.foodPrice,
                    foodPrices = menuItem.foodPrice?.toInt(),
                    foodImages = menuItem.foodImage,
                    RestuarantId = userIdOfRestaurant,
                    foodQuantities = 1 // Default quantity to 1
                )

                // Add the cart item to the Firebase Database
                cartItemRef.setValue(cartItem)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext, "Item added to cart!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext, "Failed to add item: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(requireContext, "User ID or Restaurant ID is null", Toast.LENGTH_SHORT).show()
            }
        }
    }
}


