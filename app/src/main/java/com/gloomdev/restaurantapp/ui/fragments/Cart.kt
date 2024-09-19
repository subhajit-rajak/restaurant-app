package com.gloomdev.restaurantapp.ui.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gloomdev.restaurantapp.R
import com.gloomdev.restaurantapp.databinding.FragmentCartBinding
import com.gloomdev.restaurantapp.ui.activities.AllAddress
import com.gloomdev.restaurantapp.ui.adapter.AllAddressAdapter
import com.gloomdev.restaurantapp.ui.adapter.CartAdapter
import com.gloomdev.restaurantapp.ui.dataclass.ItemDetails
import com.gloomdev.restaurantapp.ui.dataclass.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Cart : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var cartAdapter: CartAdapter
    private lateinit var itemDetailsList: MutableList<ItemDetails>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        binding.recyclerview.layoutManager = LinearLayoutManager(context)
        sharedPreferences = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)

//        val userId = sharedPreferences.getString("userId", null)
        val userId = mAuth.currentUser?.uid
        val userIdOfRestaurant = sharedPreferences.getString("userIdOfRestaurant", null)

        if (userId != null && userIdOfRestaurant != null) {
            // Fetch cart items from Firebase
            database.child("Customers").child("customerDetails").child(userId).child("CartItems").child(userIdOfRestaurant)
//            database.child("ItemDetails").child(userIdOfRestaurant).child(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        itemDetailsList = mutableListOf()
                        if (snapshot.exists()) {
                            for (itemSnapshot in snapshot.children) {
                                val item = itemSnapshot.getValue(ItemDetails::class.java)
                                item?.let { itemDetailsList.add(it) }
                            }
                            cartAdapter = CartAdapter(itemDetailsList) {
//                                updateTotalPrice(itemDetailsList)
                            }
                            binding.recyclerview.adapter = cartAdapter
//                            updateTotalPrice(itemDetailsList)

//                            binding.deliveryTxt.text = "50"
//                            binding.taxTxt.text = "10"
//                            updateFinalPrice()

                            if (itemDetailsList.isEmpty()) {
                                binding.emptyCartTxt.visibility = View.VISIBLE
//                                binding.ScrollView.visibility = View.GONE
                                binding.checkOutTxt.visibility = View.GONE
//                                binding.checkOutTxt.text = itemDetailsList.toString()
                            }
                        }else {
                            // No items found, handle the case where snapshot doesn't exist
                            binding.emptyCartTxt.visibility = View.VISIBLE
//                            binding.ScrollView.visibility = View.GONE
                            binding.checkOutTxt.visibility = View.GONE
                        }
//                        addressList.reverse()
//                        val myadapter = AllAddressAdapter(this@AllAddress,addressList)
//                        recyclerView.adapter = myadapter
//                        itemDetailsList.reverse()
//                        cartAdapter = CartAdapter(this@Cart,itemDetailsList)
//                        binding.recyclerview.adapter = cartAdapter
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context, "Failed to load data: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            Toast.makeText(context, "User ID or Restaurant ID is null", Toast.LENGTH_SHORT).show()
        }

        binding.checkOutTxt.setOnClickListener {

            val navController = findNavController()
            navController.navigate(R.id.action_cartFragment_to_checkOutFragment)

        }

//        binding.cartAddressChange.setOnClickListener {
//            val intent = Intent(context, AllAddress::class.java)
//            startActivity(intent)
//        }
    }

//    private fun updateTotalPrice(cartItems: List<ItemDetails>) {
//
//
//    }

}
