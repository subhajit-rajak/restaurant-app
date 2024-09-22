package com.gloomdev.restaurantapp.ui.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.gloomdev.restaurantapp.R
import com.gloomdev.restaurantapp.databinding.FragmentHomeBinding
import com.gloomdev.restaurantapp.ui.activities.AllAddress
import com.gloomdev.restaurantapp.ui.adapter.HomeAdapter
import com.gloomdev.restaurantapp.ui.dataclass.RestaurantListHome
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Home : Fragment() {
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
        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.slider_image_1, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.slider_image_2, ScaleTypes.FIT))
        binding.imageSlider.setImageList(imageList)
        binding.imageSlider.setImageList(imageList, ScaleTypes.FIT)
        binding.UAddress.setOnClickListener {
            val intent = Intent(context, AllAddress::class.java)
            startActivity(intent)
        }

    }
    override fun onResume() {
        super.onResume()
        sharedPreferences = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)

        val flat = sharedPreferences.getString("selectedFlat","")
        val area = sharedPreferences.getString("selectedArea","")
        val state = sharedPreferences.getString("selectedState","")

        if (flat!!.isNotEmpty()  && area!!.isNotEmpty() && state!!.isNotEmpty()){
            binding.UAddress.text = buildString {
                append(sharedPreferences.getString("selectedFlat", " - "))
                append(", ")
                append(sharedPreferences.getString("selectedArea", " - "))
                append(", ")
                append(sharedPreferences.getString("selectedState", " - "))
            }
        }else{
            binding.UAddress.text = "Add Address!"
        }



        var name = sharedPreferences.getString("selectedUserName", "")
        if (name?.isNotEmpty() == true) {
            val spaceIndex = name?.indexOf(" ")
            if(spaceIndex!=-1) {
                name = spaceIndex?.let { name?.substring(0, it) }
            }
            binding.UName.text = buildString {
                append("Hey, ")
                append(name)
                append("!")
            }
        } else{
            binding.UName.text = buildString {
                append("Hey ")
                append("!")
            }
        }


        //binding.name.text = sharedPreferences.getString("username", "")+", What's on your mind?"
    }
}