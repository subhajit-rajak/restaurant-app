package com.gloomdev.restaurantapp.ui.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gloomdev.restaurantapp.R
import com.gloomdev.restaurantapp.databinding.ActivityAllAddressBinding
import com.gloomdev.restaurantapp.ui.adapter.AllAddressAdapter
import com.gloomdev.restaurantapp.ui.dataclass.AllAddressDetailes
import com.gloomdev.restaurantapp.ui.dataclass.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AllAddress : AppCompatActivity(), AllAddressAdapter.OnItemClickListener  {
    private val binding:ActivityAllAddressBinding by lazy {
        ActivityAllAddressBinding.inflate(layoutInflater)
    }

    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var recyclerView: RecyclerView

    private lateinit var sharedPreferences: SharedPreferences
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
//        userId = sharedPreferences.getString("userId", "-")
        userId = auth.currentUser?.uid
        // Initialize RecyclerView and set layout manager
        recyclerView = binding.AllAddressRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        userId?.let { id ->
            database.child("Customers").child("customerDetails").child(id).child("Address")
                .addListenerForSingleValueEvent(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val addressList = mutableListOf<AllAddressDetailes>()
                        if (snapshot.exists()) {
                            for (noteSnapshot in snapshot.children) {
                                val user = noteSnapshot.getValue(AllAddressDetailes::class.java)
                                user?.let {
                                    addressList.add(it)
                                }
                            }
                        }
                        addressList.reverse()
                        val myadapter = AllAddressAdapter(this@AllAddress,addressList,this@AllAddress)
                        recyclerView.adapter = myadapter
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@AllAddress, "Failed to fetch user data: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        binding.AddNewAddress.setOnClickListener {
            val intent = Intent(this, AddNewAddress::class.java)
            startActivity(intent)
            finish()
        }

    }

    override fun onItemClick() {
        finish()
    }


}