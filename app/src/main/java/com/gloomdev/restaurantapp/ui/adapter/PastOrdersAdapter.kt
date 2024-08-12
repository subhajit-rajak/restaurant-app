package com.gloomdev.restaurantapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gloomdev.restaurantapp.R
import com.gloomdev.restaurantapp.ui.dataclass.Order

class PastOrdersAdapter(private val orders: List<Order>) : RecyclerView.Adapter<PastOrdersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_past_order, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]

        holder.restaurantName.text = order.restaurantName
        holder.orderStatus.text = order.orderStatus
        holder.orderAmount.text = "₹${order.orderAmount}"
        holder.orderDetails.text = order.orderDetails
        holder.orderDateTime.text = order.orderDateTime

        // Implement button actions as needed
        holder.reorderButton.setOnClickListener {
            // Handle reorder action
        }

        holder.rateOrderButton.setOnClickListener {
            // Handle rate order action
        }
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val restaurantName: TextView = itemView.findViewById(R.id.restaurant_name)
        val orderStatus: TextView = itemView.findViewById(R.id.order_status)
        val orderAmount: TextView = itemView.findViewById(R.id.order_amount)
        val orderDetails: TextView = itemView.findViewById(R.id.order_details)
        val orderDateTime: TextView = itemView.findViewById(R.id.order_date_time)
        val reorderButton: Button = itemView.findViewById(R.id.reorder_button)
        val rateOrderButton: Button = itemView.findViewById(R.id.rate_order_button)
    }
}
