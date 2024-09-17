package com.gloomdev.restaurantapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.gloomdev.restaurantapp.R
import com.gloomdev.restaurantapp.ui.dataclass.CartDetailes
import com.gloomdev.restaurantapp.ui.dataclass.ItemDetails
import kotlin.time.times

class CartAdapter(val Detailes: MutableList<ItemDetails>, private val onItemQuantityChange: () -> Unit): Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(itemView: View) : ViewHolder(itemView) {
        var pic = itemView.findViewById<ImageView>(R.id.picCart)
        var title = itemView.findViewById<TextView>(R.id.titleTxt)
        var numberItem = itemView.findViewById<TextView>(R.id.numberItemTxt)
        var feeEachItem = itemView.findViewById<TextView>(R.id.feeEachItem)
        var totelFee = itemView.findViewById<TextView>(R.id.totalEachItem)
        var minusButton = itemView.findViewById<ImageView>(R.id.minusCartBtn)
        var plusButton = itemView.findViewById<ImageView>(R.id.plusCartBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.each_cart_item, parent, false)
        return CartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return Detailes.size;
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {

        Glide.with(holder.itemView.context)
            .load(Detailes.get(position).foodImages)
            .transform(CenterCrop(), RoundedCorners(26))
            .into(holder.pic)
//        holder.pic.setImageResource(Detailes[position].picItem)
        holder.title.text = Detailes[position].foodName
        holder.feeEachItem.text = Detailes[position].foodPrices.toString()
        holder.numberItem.text = Detailes[position].foodQuantities.toString()

        holder.totelFee.text = "${(Detailes[position].foodQuantities?.times(Detailes[position].foodPrices!!))}"
        holder.minusButton.setOnClickListener {
            if (Detailes[position].foodQuantities!! > 0) {
                Detailes[position].foodQuantities = Detailes[position].foodQuantities!! - 1
                holder.numberItem.text = Detailes[position].foodQuantities.toString()

                holder.totelFee.text =
                    "${(Detailes[position].foodQuantities?.times(Detailes[position].foodPrices!!))}"
                onItemQuantityChange()
            }
//            else{
//               val color ="#EEEEEE"
//                holder.minusButton.setBackgroundColor(Color.parseColor(color))
//            }
        }

        holder.plusButton.setOnClickListener {
            Detailes[position].foodQuantities = Detailes[position].foodQuantities!! + 1
            holder.numberItem.text = Detailes[position].foodQuantities.toString()
            holder.totelFee.text = "${(Detailes[position].foodQuantities?.times(Detailes[position].foodPrices!!))}"

            onItemQuantityChange()
        }
    }
}

