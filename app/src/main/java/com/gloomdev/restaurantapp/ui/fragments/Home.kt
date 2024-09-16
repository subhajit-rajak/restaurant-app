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
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.gloomdev.restaurantapp.R
import com.gloomdev.restaurantapp.databinding.FragmentHomeBinding
import com.gloomdev.restaurantapp.ui.activities.AllAddress
import com.gloomdev.restaurantapp.ui.adapter.HomeAdapter
import com.gloomdev.restaurantapp.ui.dataclass.RestaurantList
import com.gloomdev.restaurantapp.ui.dataclass.RestaurantListHome
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Home : Fragment() {
    private lateinit var adapter: HomeAdapter
    private val List = arrayListOf<RestaurantList>()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        // Initialize Firebase references
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        binding.RestaurantRecyclerView.layoutManager = LinearLayoutManager(context)

        database.child("user")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {


                    val restaurantData = mutableListOf<RestaurantListHome>()
                    if (snapshot.exists()) {


                        for (noteSnapshot in snapshot.children) {
                            val userIdOfRestaurant = noteSnapshot.key

                            val user = noteSnapshot.getValue(RestaurantListHome::class.java)
                            user?.let {
//                            binding.name.text = it.username
//                            binding.email.text = it.email
//                            binding.number.text = it.mobile
//                                binding.restName.text = it.restName
                                it.IdOfRestaurant = userIdOfRestaurant ?: ""
                                restaurantData.add(it)

                            }
                        }
                    }

                    restaurantData.reverse()
                    val myadapter = HomeAdapter(context,restaurantData)
                    binding.RestaurantRecyclerView.adapter = myadapter
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Faild to load data", Toast.LENGTH_SHORT).show()
                }
            })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


//        sharedPreferences = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
//        binding.UAddress.text = sharedPreferences.getString("selectedFlat", "-")+","+
//                sharedPreferences.getString("selectedArea", "-")+","+
//                sharedPreferences.getString("selectedState", "-")
//
//        binding.UName.text =  sharedPreferences.getString("username", "Hii")
//        binding.name.text = sharedPreferences.getString("username", "")+", What's on your mind?"

        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.slider_image_1, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.slider_image_2, ScaleTypes.FIT))
        binding.imageSlider.setImageList(imageList)
        binding.imageSlider.setImageList(imageList, ScaleTypes.FIT)
//
//        List.add(RestaurantList("https://loremflickr.com/320/240/burger-restaurant","Highway King","4.1 (5K+)","20-25 Min","Burgers, American, Fast Food, Snacks","Subhash Chandra Road","2km"))
//        List.add(RestaurantList("https://loremflickr.com/320/240/korean-bbq","Tasty Bites","4.5 (2K+)","15-20 Min","Indian, Chinese, Italian","Main Market Road","1.5km"))
//        List.add(RestaurantList("https://loremflickr.com/320/240/pizza-place","Pizza Palace","4.2 (1K+)","30-35 Min","Pizzas, Italian, Fast Food","Sector 5 Road","3km"))
//        List.add(RestaurantList("https://loremflickr.com/320/240/indian-restaurant","Burger Barn","4.8 (500+)","10-15 Min","Burgers, American, Fast Food","Food Court Road","1km"))
//        List.add(RestaurantList("https://loremflickr.com/320/240/chinese-food","Spice Route","4.0 (1K+)","25-30 Min","Indian, Chinese, Thai","Highway Road","2.5km"))
//        List.add(RestaurantList("https://loremflickr.com/320/240/sandwich-bar","Sandwich Hub","4.6 (1K+)","15-20 Min","Sandwiches, Salads, Fast Food","College Road","1.2km"))
//        List.add(RestaurantList("https://loremflickr.com/320/240/dhaba-food","Dhaba Lane","4.3 (500+)","20-25 Min","Indian, Punjabi, North Indian","NH 8 Road","2km"))
//        List.add(RestaurantList("https://loremflickr.com/320/240/coffee-shop","Café Coffee Day","4.1 (2K+)","10-15 Min","Coffee, Snacks, Fast Food","Mall Road","1km"))
//        List.add(RestaurantList("https://loremflickr.com/320/240/mexican-restaurant","KFC","4.4 (1K+)","20-25 Min","Fried Chicken, Fast Food","Main Market Road","1.5km"))
//        List.add(RestaurantList("https://loremflickr.com/320/240/indian-restaurant","Subway","4.5 (500+)","15-20 Min","Sandwiches, Salads, Fast Food","Sector 10 Road","2.5km"))
//        List.add(RestaurantList("https://loremflickr.com/320/240/italian-restaurant","Dominos Pizza","4.2 (1K+)","30-35 Min","Pizzas, Italian, Fast Food","Highway Road","3km"))
//
//        adapter = HomeAdapter(List)
//        binding.RestaurantRecyclerView.adapter = adapter
//        binding.RestaurantRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.UAddress.setOnClickListener {
            val intent = Intent(context, AllAddress::class.java)
            startActivity(intent)
        }

    }
    override fun onResume() {
        super.onResume()
        sharedPreferences = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        binding.UAddress.text = sharedPreferences.getString("selectedFlat", " - ")+","+
                sharedPreferences.getString("selectedArea", " - ")+","+
                sharedPreferences.getString("selectedState", " - ")

        var name = sharedPreferences.getString("username", "Anonymous")
        if (name != null) {
            val spaceIndex = name.indexOf(" ")
            if(spaceIndex!=-1) {
                name = name.substring(0, spaceIndex)
            }
        }
        binding.UName.text =  "Hey, " + name + "!"
        binding.name.text = name +", What's on your mind?"
    }
}