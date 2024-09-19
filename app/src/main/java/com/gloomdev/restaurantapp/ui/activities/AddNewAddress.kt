package com.gloomdev.restaurantapp.ui.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gloomdev.restaurantapp.R
import com.gloomdev.restaurantapp.databinding.ActivityAddNewAddressBinding
import com.gloomdev.restaurantapp.ui.dataclass.AllAddressDetailes
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale

class AddNewAddress : AppCompatActivity() {

    private lateinit var binding: ActivityAddNewAddressBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var typeOfAddress: String? = null
    var isButtonClicked: Boolean?  = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001


    private lateinit var sharedPreferences: SharedPreferences
    private var userId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddNewAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase references
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        isButtonClicked = false
        sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
//        userId = sharedPreferences.getString("userId", "-")

        userId = mAuth.currentUser?.uid



        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.useMyLocationButton.setOnClickListener {
            // Check permissions and request location
            if (checkPermissions()) {
                getLastLocation()
            } else {
                requestPermissions()
            }
        }
        binding.HomeButton.setOnClickListener {
            typeOfAddress = "Home"
            binding.HomeButton.setBackgroundResource(R.drawable.orange_button)
            binding.WorkButton.setBackgroundResource(R.drawable.custom_offwhite_button)
            isButtonClicked = true
        }
        binding.WorkButton.setOnClickListener {
            typeOfAddress = "Work"
            binding.WorkButton.setBackgroundResource(R.drawable.orange_button)
            binding.HomeButton.setBackgroundResource(R.drawable.custom_offwhite_button)
            isButtonClicked = true
        }

        binding.AddAddressButton.setOnClickListener {
            Log.d("AddNewAddressActivity", "Add New Address button clicked")
            val name = binding.nameEditText.text.toString().trim()
            val mobile = binding.mobileEditText.text.toString().trim()
            val flat = binding.FlatEditText.text.toString().trim()
            val landmark = binding.LandmarkEditText.text.toString().trim()
            val area = binding.areaEditText.text.toString().trim()
            val city = binding.CityEditText.text.toString().trim()
            val state = binding.StateEditText.text.toString().trim()
            val pincode = binding.PincodeEditText.text.toString().trim()
            if (validateInputs(
                    name,
                    mobile,
                    flat,
                    landmark,
                    area,
                    city,
                    state,
                    pincode
                )
            ) {
                Toast.makeText(this, "Address Added", Toast.LENGTH_SHORT).show()

                val usersRef = database.child("Customers").child("customerDetails")
                usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        saveUserDetailsToDatabase(
                            userId!!,
                            name,
                            mobile,
                            flat,
                            landmark,
                            area,
                            city,
                            state,
                            pincode,
                            typeOfAddress
                        )
                    }

                    override fun onCancelled(error: DatabaseError) {
                        showToast("Database read failed: ${error.message}")
                    }
                })
            }
        }
    }

    private fun checkPermissions(): Boolean {
        return (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permissions are not granted yet
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    // Get the address from the location coordinates
                    getAddressFromLocation(location)
                } ?: run {
//                    binding.tvAddress.text = "Location not available"
                    Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {

                Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show()
            }
    }


    private fun getAddressFromLocation(location: Location) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                val premises = if (!address.premises.isNullOrEmpty())"${address.premises}, " else ""
                binding.areaEditText.setText(address.premises)
                binding.LandmarkEditText.setText(address.subLocality)
                binding.CityEditText.setText(address.locality)
                binding.StateEditText.setText(address.adminArea)
                binding.PincodeEditText.setText(address.postalCode)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Unable to get address", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, get the location
                getLastLocation()
            } else {
//                binding.tvAddress.text = "Permission denied"
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateInputs(
        name: String,
        mobile: String,
        flat: String,
        landmark: String,
        area: String,
        city: String,
        state: String,
        pincode: String
    ): Boolean {
        if (name.isEmpty() || flat.isEmpty()  || area.isEmpty()|| city.isEmpty() || state.isEmpty() ||isButtonClicked !=true) {
            showToast("Please enter all details")
            return false
        }
        if (pincode.isEmpty() ||!(Regex("^[0-9]{6}$").matches(pincode))) {
            showToast("Please enter a valid pincode")
            return false
        }
        if (mobile.isEmpty() || !(Regex("^[6-9]\\d{9}$")).matches(mobile)) {
            showToast("Please enter a valid mobile number")
            return false

        }
        return true
        }
        private fun Context.showToast(message: String) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }


    private fun saveUserDetailsToDatabase(
        userId: String,
        name: String,
        mobile: String,
        flat: String,
        ladmark: String,
        area: String,
        city: String,
        state: String,
        pincode: String,
        typeOfAddress: String?
    ) {
        val allAdressDetailes = AllAddressDetailes(userId, name, mobile, flat,ladmark, area,city, state, pincode, typeOfAddress.toString())

        val addressRef = database.child("Customers").child("customerDetails").child(userId).child("Address")
        addressRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val userRefNumber = "Address${101 + snapshot.childrenCount.toInt()}"
                database.child("Customers").child("customerDetails").child(userId).child("Address").child(userRefNumber).setValue(allAdressDetailes)


                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            showToast("User address saved successfully")
                            val editor = sharedPreferences.edit()
                            editor.putString("userRefNumber", userRefNumber)
                            editor.apply()

                            val intent = Intent(this@AddNewAddress, AllAddress::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            showToast("Failed to save user address: ${task.exception?.message}")
                        }
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })

    }
}