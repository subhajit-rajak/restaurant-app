package com.gloomdev.restaurantapp.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.gloomdev.restaurantapp.R
import com.gloomdev.restaurantapp.ui.dataclass.MenuItems
import com.gloomdev.restaurantapp.ui.dataclass.RestaurantList
import com.gloomdev.restaurantapp.ui.activities.RestaurantProfile
import com.gloomdev.restaurantapp.ui.dataclass.RestaurantListHome


class HomeAdapter(private val context: Context?, val List: MutableList<RestaurantListHome>) : Adapter<HomeAdapter.HomeViewHolder>() {

    class HomeViewHolder(itemView: View) : ViewHolder(itemView) {
        var pic = itemView.findViewById<ImageView>(R.id.restaurantImage)
        var RestaurantName= itemView.findViewById<TextView>(R.id.restaurantNameTxt)
//        var Rating = itemView.findViewById<TextView>(R.id.ratingtxt)
//        var DeliveryTime = itemView.findViewById<TextView>(R.id.DeliveryTimeTxt)
        var AvailableFood = itemView.findViewById<TextView>(R.id.availableFoodTxt)
        var Address = itemView.findViewById<TextView>(R.id.addressTxt)
//        var Distance = itemView.findViewById<TextView>(R.id.DistanceTxt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_restaurant, parent, false)
        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return List.size;
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
//        Glide.with(holder.itemView.context)
//            .load(List.get(position).pic)
//            .transform(CenterCrop(), RoundedCorners(26))
//            .into(holder.pic)
        Glide.with(holder.itemView.context)
            .load(List.get(position).restaurantImage)
            .centerCrop()
            .into(holder.pic)
        holder.RestaurantName.text = List[position].nameOfRestaurant
////        holder.Rating.text = List[position].Rating
////        holder.DeliveryTime.text = List[position].DelvieryTime
//        holder.AvailableFood.text = List[position].AvailableFood
        holder.Address.text = List[position].location
////        holder.Distance.text = List[position].Distance

        // Set a click listener on the item
        holder.itemView.setOnClickListener {
            // Create an intent to navigate to the next screen
            val intent = Intent(context, RestaurantProfile::class.java)

            // Pass the restaurant name to the next activity
            intent.putExtra("RESTAURANT_UID", List[position].IdOfRestaurant)
            intent.putExtra("RESTAURANT_NAME", List[position].nameOfRestaurant)
            intent.putExtra("RESTAURANT_DESCRIPTION", List[position].description)
            intent.putExtra("RESTAURANT_IMAGE", List[position].restaurantImage)

            // Start the next activity
            context?.startActivity(intent)
        }
    }
}