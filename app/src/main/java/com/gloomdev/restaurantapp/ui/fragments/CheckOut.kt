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
import androidx.recyclerview.widget.LinearLayoutManager
import com.gloomdev.restaurantapp.databinding.FragmentCheckOutBinding
import com.gloomdev.restaurantapp.ui.activities.AllAddress
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

class CheckOut : Fragment() {

    private lateinit var binding: FragmentCheckOutBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var cartAdapter: CartAdapter
    private lateinit var itemDetailsList: MutableList<ItemDetails>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCheckOutBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        binding.recyclerview.layoutManager = LinearLayoutManager(context)
        sharedPreferences = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)

        val userId = sharedPreferences.getString("userId", null)
        val userIdOfRestaurant = sharedPreferences.getString("userIdOfRestaurant", null)

        if (userId != null && userIdOfRestaurant != null) {
            // Fetch cart items from Firebase
            database.child("ItemDetails").child(userIdOfRestaurant).child(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        itemDetailsList = mutableListOf()
                        if (snapshot.exists()) {
                            for (itemSnapshot in snapshot.children) {
                                val item = itemSnapshot.getValue(ItemDetails::class.java)
                                item?.let { itemDetailsList.add(it) }
                            }
                            cartAdapter = CartAdapter(itemDetailsList) {
                                updateTotalPrice(itemDetailsList)
                            }
                            binding.recyclerview.adapter = cartAdapter
                            updateTotalPrice(itemDetailsList)

                            binding.deliveryTxt.text = "50"
                            binding.taxTxt.text = "10"
                            updateFinalPrice()

                            if (itemDetailsList.isEmpty()) {
                                binding.emptyCartTxt.visibility = View.VISIBLE
                                binding.ScrollView.visibility = View.GONE
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context, "Failed to load data: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            Toast.makeText(context, "User ID or Restaurant ID is null", Toast.LENGTH_SHORT).show()
        }

        binding.checkOutTxt.setOnClickListener {
            if (itemDetailsList.isNotEmpty()) {
                placeOrder(userId, userIdOfRestaurant)

            } else {
                Toast.makeText(context, "Cart is empty!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.cartAddressChange.setOnClickListener {
            val intent = Intent(context, AllAddress::class.java)
            startActivity(intent)
        }
    }

    private fun placeOrder(userId: String?, userIdOfRestaurant: String?) {
        // Retrieve address details from shared preferences
        val userName = sharedPreferences.getString("selectedUserName", "-")
        val userAddress = sharedPreferences.getString("selectedAddress", "-")
        val phoneNumber = sharedPreferences.getString("CustomerPhoneNumber", "-")
        val totalPrice = binding.totalTxt.text.toString()

        // Create lists for order items
        val foodNames = itemDetailsList.map { it.foodName!! }.toMutableList()
        val foodImages = itemDetailsList.map { it.foodImages!! }.toMutableList()
        val foodPrices = itemDetailsList.map { it.foodPrices.toString() }.toMutableList()
        val foodQuantities = itemDetailsList.map { it.foodQuantities!! }.toMutableList()

        // Create the OrderDetails object
        val orderDetails = OrderDetails(
            userUid = userId,
            userName = userName,
            foodNames = foodNames,
            foodImages = foodImages,
            foodPrices = foodPrices,
            foodQuantities = foodQuantities,
            address = userAddress,
            totalPrice = totalPrice,
            phoneNumber = phoneNumber,
            orderAccepted = false,
            paymentReceived = false,
            currentTime = getFormattedDateTime(System.currentTimeMillis())

        )

        // Save the order to Firebase
        val orderReference = database.child("Orders").child(userIdOfRestaurant!!).child(userId!!).push()
        orderReference.setValue(orderDetails)
            .addOnSuccessListener {
                Toast.makeText(context, "Order placed successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to place order: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
    fun getFormattedDateTime(currentTime: Long): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        val date = Date(currentTime)
        return dateFormat.format(date)
    }

    private fun updateTotalPrice(cartItems: List<ItemDetails>) {
        var itemTotal = 0.0
        for (item in cartItems) {
            itemTotal += item.foodQuantities!! * item.foodPrices!!
        }
        binding.totalFeeTxt.text = itemTotal.toString()
        updateFinalPrice()
    }

    private fun updateFinalPrice() {
        val itemsTotalValue = binding.totalFeeTxt.text.toString().toDouble()
        val deliveryServiceValue = binding.deliveryTxt.text.toString().toDouble()
        val taxValue = binding.taxTxt.text.toString().toDouble()

        val finalTotalPrice = itemsTotalValue + deliveryServiceValue + taxValue
        binding.totalTxt.text = finalTotalPrice.toString()
    }

    override fun onResume() {
        super.onResume()
        binding.cartAllName.text = sharedPreferences.getString("selectedUserName", "-")
        binding.cartAllFlat.text = sharedPreferences.getString("selectedAddress", "-")
        binding.cartAllMobile.text = sharedPreferences.getString("CustomerPhoneNumber", "-")
    }
}
