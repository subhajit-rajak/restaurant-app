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

class CartAdapter(val Detailes: List<CartDetailes>, private val onItemQuantityChange: () -> Unit): Adapter<CartAdapter.CartViewHolder>() {

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
            .load(Detailes.get(position).picItem)
            .transform(CenterCrop(), RoundedCorners(26))
            .into(holder.pic)
//        holder.pic.setImageResource(Detailes[position].picItem)
        holder.title.text = Detailes[position].title
        holder.feeEachItem.text = Detailes[position].feeEach.toString()
        holder.numberItem.text = Detailes[position].quantity.toString()

        holder.totelFee.text = "${(Detailes[position].quantity * Detailes[position].feeEach)}"
        holder.minusButton.setOnClickListener {
            if (Detailes[position].quantity > 0) {
                Detailes[position].quantity--
                holder.numberItem.text = Detailes[position].quantity.toString()

                holder.totelFee.text =
                    "${(Detailes[position].quantity * Detailes[position].feeEach)}"
                onItemQuantityChange()
            }
//            else{
//               val color ="#EEEEEE"
//                holder.minusButton.setBackgroundColor(Color.parseColor(color))
//            }
        }

        holder.plusButton.setOnClickListener {
            Detailes[position].quantity++
            holder.numberItem.text = Detailes[position].quantity.toString()
            holder.totelFee.text = "${(Detailes[position].quantity * Detailes[position].feeEach)}"

            onItemQuantityChange()
        }
    }
}

