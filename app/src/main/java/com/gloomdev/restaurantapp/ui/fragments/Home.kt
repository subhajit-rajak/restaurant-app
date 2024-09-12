package com.gloomdev.restaurantapp.ui.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.gloomdev.restaurantapp.R
import com.gloomdev.restaurantapp.databinding.FragmentHomeBinding
import com.gloomdev.restaurantapp.ui.activities.AllAddress
import com.gloomdev.restaurantapp.ui.adapter.HomeAdapter
import com.gloomdev.restaurantapp.ui.dataclass.RestaurantList

class Home : Fragment() {
    private lateinit var adapter: HomeAdapter
    private val List = arrayListOf<RestaurantList>()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
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

        List.add(RestaurantList("https://loremflickr.com/320/240/burger-restaurant","Highway King","4.1 (5K+)","20-25 Min","Burgers, American, Fast Food, Snacks","Subhash Chandra Road","2km"))
        List.add(RestaurantList("https://loremflickr.com/320/240/korean-bbq","Tasty Bites","4.5 (2K+)","15-20 Min","Indian, Chinese, Italian","Main Market Road","1.5km"))
        List.add(RestaurantList("https://loremflickr.com/320/240/pizza-place","Pizza Palace","4.2 (1K+)","30-35 Min","Pizzas, Italian, Fast Food","Sector 5 Road","3km"))
        List.add(RestaurantList("https://loremflickr.com/320/240/indian-restaurant","Burger Barn","4.8 (500+)","10-15 Min","Burgers, American, Fast Food","Food Court Road","1km"))
        List.add(RestaurantList("https://loremflickr.com/320/240/chinese-food","Spice Route","4.0 (1K+)","25-30 Min","Indian, Chinese, Thai","Highway Road","2.5km"))
        List.add(RestaurantList("https://loremflickr.com/320/240/sandwich-bar","Sandwich Hub","4.6 (1K+)","15-20 Min","Sandwiches, Salads, Fast Food","College Road","1.2km"))
        List.add(RestaurantList("https://loremflickr.com/320/240/dhaba-food","Dhaba Lane","4.3 (500+)","20-25 Min","Indian, Punjabi, North Indian","NH 8 Road","2km"))
        List.add(RestaurantList("https://loremflickr.com/320/240/coffee-shop","Café Coffee Day","4.1 (2K+)","10-15 Min","Coffee, Snacks, Fast Food","Mall Road","1km"))
        List.add(RestaurantList("https://loremflickr.com/320/240/mexican-restaurant","KFC","4.4 (1K+)","20-25 Min","Fried Chicken, Fast Food","Main Market Road","1.5km"))
        List.add(RestaurantList("https://loremflickr.com/320/240/indian-restaurant","Subway","4.5 (500+)","15-20 Min","Sandwiches, Salads, Fast Food","Sector 10 Road","2.5km"))
        List.add(RestaurantList("https://loremflickr.com/320/240/italian-restaurant","Dominos Pizza","4.2 (1K+)","30-35 Min","Pizzas, Italian, Fast Food","Highway Road","3km"))

        adapter = HomeAdapter(List)
        binding.RestaurantRecyclerView.adapter = adapter
        binding.RestaurantRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.UAddress.setOnClickListener {
            val intent = Intent(context, AllAddress::class.java)
            startActivity(intent)
        }

    }
    override fun onResume() {
        super.onResume()
        sharedPreferences = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        binding.UAddress.text = sharedPreferences.getString("selectedFlat", "-")+","+
                sharedPreferences.getString("selectedArea", "-")+","+
                sharedPreferences.getString("selectedState", "-")
        binding.UName.text =  sharedPreferences.getString("username", "Hii")
        binding.name.text = sharedPreferences.getString("username", "")+", What's on your mind?"
    }
}