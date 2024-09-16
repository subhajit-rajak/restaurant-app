package com.gloomdev.restaurantapp.ui.activities

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

                                binding.Price.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rupee, 0, 0, 0)

                                binding.foodName.text = it.foodName
                                binding.Price.text =  it.foodPrice
                                binding.shortDescriptionOfItem2.text = it.foodDescription

                                Glide.with(this@MenuDetails).load(it.foodImage).centerCrop().into(binding.foodImage)


                            }

                    }
//
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MenuDetails, "Faild to load data", Toast.LENGTH_SHORT).show()
                }
            })
    }
}