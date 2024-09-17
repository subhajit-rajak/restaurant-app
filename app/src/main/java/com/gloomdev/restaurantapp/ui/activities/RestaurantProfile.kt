package com.gloomdev.restaurantapp.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.gloomdev.restaurantapp.R
import com.gloomdev.restaurantapp.databinding.ActivityRestaurantProfileBinding
import com.gloomdev.restaurantapp.ui.adapter.MenuAdapterRestaurantScreen
import com.gloomdev.restaurantapp.ui.dataclass.MenuRestaurantScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RestaurantProfile : AppCompatActivity() {

    private  val binding:ActivityRestaurantProfileBinding by lazy {
        ActivityRestaurantProfileBinding.inflate(layoutInflater)
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

        // Get the UID from the intent
        val restaurantUid = intent.getStringExtra("RESTAURANT_UID")
        val restaurantName = intent.getStringExtra("RESTAURANT_NAME")
        val restaurantDescription = intent.getStringExtra("RESTAURANT_DESCRIPTION")
        val restaurantImage = intent.getStringExtra("RESTAURANT_IMAGE")

        // Use the restaurantUid as needed (e.g., to query more details from Firebase)
        if (restaurantUid != null) {
            // Query Firebase with the restaurantUid or display it

        } else {
            Toast.makeText(this, "No Restaurant ID found!", Toast.LENGTH_SHORT).show()
        }
        // Initialize Firebase references
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        binding.menuRVForRestProfile.layoutManager = LinearLayoutManager(this)


        binding.restName.text = restaurantName
        binding.shortDescription2.text = restaurantDescription
        Glide.with(this).load(restaurantImage).into(binding.restImage)



        database.child("menu").child(restaurantUid.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val menuData = mutableListOf<MenuRestaurantScreen>()
                    if (snapshot.exists()) {

                        for (menuSnapshot in snapshot.children) {
                            val user = menuSnapshot.getValue(MenuRestaurantScreen::class.java)
                            user?.let {

                                it.IdOfRestaurant = restaurantUid ?: ""
                              menuData.add(it)

                            }
                        }
                    }
                    menuData.reverse()
                    val myadapter = MenuAdapterRestaurantScreen(this@RestaurantProfile,menuData)
                    binding.menuRVForRestProfile.adapter = myadapter
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@RestaurantProfile, "Faild to load data", Toast.LENGTH_SHORT).show()
                }
            })
    }
}