package com.gloomdev.restaurantapp.ui.activities

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.gloomdev.restaurantapp.R
import com.gloomdev.restaurantapp.databinding.ActivityMenuDetailsBinding
import com.gloomdev.restaurantapp.ui.dataclass.MenuRestaurantScreen
import com.gloomdev.restaurantapp.ui.dataclass.ItemDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MenuDetails : AppCompatActivity() {

    private  val binding: ActivityMenuDetailsBinding by lazy {
        ActivityMenuDetailsBinding.inflate(layoutInflater)
    }

    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val itemKey = intent.getStringExtra("ITEM_KEY")
        val RestuarantId = intent.getStringExtra("RESTAURANT_UID")



        // Initialize Firebase references
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        database.child("menu").child(RestuarantId.toString()).child(itemKey.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val menuData = mutableListOf<MenuRestaurantScreen>()
                    if (snapshot.exists()) {


                        val user = snapshot.getValue(MenuRestaurantScreen::class.java)
                        user?.let {

                            binding.Price.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.rupee,
                                0,
                                0,
                                0
                            )


                            binding.foodName.text = it.foodName
                            binding.Price.text = it.foodPrice
                            binding.shortDescriptionOfItem2.text = it.foodDescription

                            Glide.with(this@MenuDetails).load(it.foodImage).centerCrop()
                                .into(binding.foodImage)

                            sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)

                            val editor = sharedPreferences.edit()
                            editor.putString("userIdOfRestaurant", RestuarantId)
                            editor.apply()

//                            val userId = sharedPreferences.getString("userId", "-")
                           val userId = mAuth.currentUser?.uid
//                            val userName = sharedPreferences.getString("userIdOfRestaurant", "-")
                            val userName = sharedPreferences.getString("selectedUserName", "-")
                            val itemDetails = ItemDetails(userId,userName,it.foodName,it.foodImage,it.foodPrice.toInt(),
                                it.foodPrice,RestuarantId!!,1)
                            binding.AddToCart.setOnClickListener {

                                val ItemDetails =   database.child("Customers").child("customerDetails").child(RestuarantId).child(userId!!).child("CartItems")
//                                    database.child("ItemDetails").child(RestuarantId!!)
                                ItemDetails.addListenerForSingleValueEvent(object :
                                    ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {

                                        val orderRefNumber =
                                            "Order${101 + snapshot.childrenCount.toInt()}"
                                        database.child("Customers").child("customerDetails").child(userId).child("CartItems").child(RestuarantId).child(itemKey!!).setValue(itemDetails)
//                                        database.child("ItemDetails").child(RestuarantId).child(userId!!)
//                                            .child(itemKey!!).setValue(itemDetails)


                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    Toast.makeText(
                                                        this@MenuDetails,
                                                        "item added to cart successfuly",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
//                                showToast("User address saved successfully")
//                                val editor = sharedPreferences.edit()
//                                editor.putString("userRefNumber", userRefNumber)
//                                editor.apply()

//                                val intent = Intent(this@AddNewAddress, AllAddress::class.java)
//                                startActivity(intent)
//                                finish()
                                                } else {
                                                    Toast.makeText(
                                                        this@MenuDetails,
                                                        "Failed ",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
//                                showToast("Failed to save user address: ${task.exception?.message}")
                                                }
                                            }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        // Handle the error
                                    }
                                })

                            }

                        }
                    }
                }


                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MenuDetails, "Faild to load data", Toast.LENGTH_SHORT).show()
                }
            })



    }
}
