package com.gloomdev.restaurantapp.ui.adapter

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


class HomeAdapter(val List: ArrayList<RestaurantList>) : Adapter<HomeAdapter.HomeViewHolder>() {

    class HomeViewHolder(itemView: View) : ViewHolder(itemView) {
        var pic = itemView.findViewById<ImageView>(R.id.restaurantImage)
        var RestaurantName= itemView.findViewById<TextView>(R.id.restaurantNameTxt)
        var Rating = itemView.findViewById<TextView>(R.id.ratingtxt)
        var DeliveryTime = itemView.findViewById<TextView>(R.id.DeliveryTimeTxt)
        var AvailableFood = itemView.findViewById<TextView>(R.id.availableFoodTxt)
        var Address = itemView.findViewById<TextView>(R.id.addressTxt)
        var Distance = itemView.findViewById<TextView>(R.id.DistanceTxt)
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
        Glide.with(holder.itemView.context)
            .load(List.get(position).pic)
            .transform(CenterCrop(), RoundedCorners(26))
            .into(holder.pic)
        holder.RestaurantName.text = List[position].Name
        holder.Rating.text = List[position].Rating
        holder.DeliveryTime.text = List[position].DelvieryTime
        holder.AvailableFood.text = List[position].AvailableFood
        holder.Address.text = List[position].Address
        holder.Distance.text = List[position].Distance
    }
}