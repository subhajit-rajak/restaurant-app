package com.gloomdev.restaurantapp.ui.adapter

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gloomdev.restaurantapp.R
import com.gloomdev.restaurantapp.databinding.EachAddressBinding
import com.gloomdev.restaurantapp.ui.dataclass.AllAddressDetailes

class AllAddressAdapter(
    private val context: Context,
    private val list: List<AllAddressDetailes>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<AllAddressAdapter.AddressViewHolder>() {

    private var selectedPosition: Int = RecyclerView.NO_POSITION
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)


    // Define an interface for the callback
    interface OnItemClickListener {
        fun onItemClick()
    }

init {
    // Load the previously selected address, area, and state values from SharedPreferences
    val savedAddress = sharedPreferences.getString("selectedFlat", "")
    val savedArea = sharedPreferences.getString("selectedArea", "")
    val savedState = sharedPreferences.getString("selectedState", "")

    // Find the index of the item that matches the saved values
    selectedPosition = list.indexOfFirst { it.flat == savedAddress && it.area == savedArea && it.state == savedState }
}

    class AddressViewHolder(val binding: EachAddressBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: AllAddressDetailes, isSelected: Boolean) {
            binding.allName.text = note.name
            binding.allMobile.text = note.mobile
            val landmarkText = if (!note.landmark.isNullOrEmpty()) "${note.landmark}, " else ""
            binding.allFlat.text = note.flat + ", " + note.area + ", " + landmarkText + note.city + ", " + note.state + " - " + note.pincode

            // Change the background color of the selected item
            binding.addressCard.setCardBackgroundColor(if (isSelected) Color.WHITE else Color.parseColor("#E6DFDF"))
//        binding.root.setBackgroundResource(if (isSelected) R.drawable.orange_button else R.drawable.custom_offwhite_button)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding = EachAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddressViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val note = list[position]
        holder.bind(note, position == selectedPosition)

        holder.itemView.setOnClickListener {
            // Update the selected position
            val previousPosition = selectedPosition
            selectedPosition = holder.adapterPosition

            val landmarkText = if (!note.landmark.isNullOrEmpty()) "${note.landmark}, " else ""
            // Save the selected `allFlat` value in SharedPreferences
            sharedPreferences.edit()
                .putString("selectedFlat", note.flat)
                .putString("selectedArea", note.area)
                .putString("selectedState", note.state)
                .putString("selectedUserName", note.name)
                .putString("selectedAddress", note.flat+", "+note.area+", "+landmarkText+note.city+", "+note.state+" - "+note.pincode)
                .putString("CustomerPhoneNumber", note.mobile)
                .apply()

            // Notify the adapter to refresh the items
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
            // Call the callback to finish the activity
            onItemClickListener.onItemClick()
        }
    }
}


