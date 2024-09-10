package com.gloomdev.restaurantapp.ui.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


import com.gloomdev.restaurantapp.databinding.ActivityRegisterBinding
import com.gloomdev.restaurantapp.ui.fragments.Home
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.R
import com.google.firebase.database.ValueEventListener
import java.security.MessageDigest

class RegisterActivity : AppCompatActivity() {

    //Variables
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Removing Action Bar
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        //Register Button
        binding.registerButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        //Login Button.
        binding.loginToExistingAccount.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Initialize auth before using it
        auth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        database = FirebaseDatabase.getInstance().reference

        val currentUser = auth.currentUser

        if (currentUser != null && sharedPreferences.getBoolean("rememberMe", true)) {
            // User is logged in and "Remember Me" is enabled
            startActivity(Intent(this, Home::class.java))
            finish()
        }

        if (isUserLoggedIn()) {
            navigateToHomePage()
            return
        }

        binding.registerButton.setOnClickListener {
            Log.d("RegisterActivity", "Login button clicked")
            val username = binding.name.text.toString().trim()
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()
            val confirmPassword = binding.confirmpassword.text.toString().trim()
            if (validateInputs(username, email, password, confirmPassword)) {
                val hashedPassword = hashPassword(password)
                checkIfUserExists(username, email, hashedPassword)
            }
        }
    }

    //Functions
    private fun validateInputs(
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (username.isEmpty()) {
            showToast("Please enter a username")
            return false
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Please enter a valid email address")
            return false
        }
        if (password.isEmpty()) {
            showToast("Please enter a password")
            return false
        }
        if (password != confirmPassword) {
            showToast("Passwords do not match")
            return false
        }
        return true
    }

    //    private fun isUserLoggedIn(): Boolean {
//        val email = sharedPreferences.getString("email", null)
//        val password = sharedPreferences.getString("password", null)
//        return email != null && password != null
//    }
//
//    private fun hashPassword(password: String): String {
//        val bytes = password.toByteArray(Charsets.UTF_8)
//        val md = MessageDigest.getInstance("SHA-256")
//        val digest = md.digest(bytes)
//        return digest.fold("", { str, it -> str + "%02x".format(it) })
//    }
//
//    private fun checkIfUserExists(
//        username: String, email: String, hashedPassword: String
//    ) {
//        val usersRef = database.child("Customers").child("customerDetails")
//        var isEmailRegistered = false
//
//        usersRef.orderByChild("email").equalTo(email)
//            .addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    if (snapshot.exists()) {
//                        showToast("Email already registered")
//                        isEmailRegistered = true
//                        return
//                    }
//
//                    usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
//                        override fun onDataChange(snapshot: DataSnapshot) {
//                            if (!isEmailRegistered) {
//                                generateUserRefNumberAndSaveDetails(
//                                    "", username, email, hashedPassword
//                                )
//                            }
//                        }
//
//                        override fun onCancelled(error: DatabaseError) {
//                            showToast("Database read failed: ${error.message}")
//                        }
//                    })
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    showToast("Database read failed: ${error.message}")
//                }
//            })
//    }
//
//    private fun generateUserRefNumberAndSaveDetails(
//        userId: String,
//        username: String,
//        email: String,
//        hashedPassword: String = ""
//    ) {
//        val usersRef = database
//            .child("Customers")
//            .child("customerDetails")
//        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val userRefNumber = "USER${1001 + snapshot.childrenCount.toInt()}"
//                saveUserDetailsToDatabase(
//                    userRefNumber, userId, username, email, hashedPassword
//                )
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                showToast("Database read failed: ${error.message}")
//            }
//        })
//    }
//
//    private fun saveUserDetailsToDatabase(
//        userRefNumber: String,
//        userId: String,
//        username: String,
//        email: String,
//        hashedPassword: String
//    ) {
//        val userDetailsRef =
//            database.child("Customers")
//                .child("customerDetails")
//                .child(userRefNumber)
//
//        val user = hashMapOf(
//            "userId" to userRefNumber,
//            "username" to username,
//            "email" to email,
//            "password" to hashedPassword
//        )
//
//        userDetailsRef.setValue(user).addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                showToast("User details saved successfully")
//                navigateToHomePage()
//                finish()
//            } else {
//                showToast("Failed to save user details: ${task.exception?.message}")
//            }
//        }
//    }
    private fun isUserLoggedIn(): Boolean {
        val email = sharedPreferences.getString("email", null)
        val password = sharedPreferences.getString("password", null)
        return email != null && password != null
    }


    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray(Charsets.UTF_8)
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("", { str, it -> str + "%02x".format(it) })
    }

    private fun checkIfUserExists(
        username: String,
        email: String,
        hashedPassword: String
    ) {
        val usersRef = database.child("Customers").child("customerDetails")
        var isEmailRegistered = false
        var isMobileRegistered = false

        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    showToast("Email already registered")
                    isEmailRegistered = true
                    return
                }
                if (!isEmailRegistered && !isMobileRegistered) {
                    generateUserRefNumberAndSaveDetails(
                        "", username, email, hashedPassword
                    )
                }
            }
                override fun onCancelled(error: DatabaseError) {
                    showToast("Database read failed: ${error.message}")
                }
        })
    }

    private fun generateUserRefNumberAndSaveDetails(
        userId: String,
        username: String,
        email: String,
        hashedPassword: String = ""
    ) {
        val usersRef = database.child("Customers").child("customerDetails")
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userRefNumber = "USER${1001 + snapshot.childrenCount.toInt()}"
                saveUserDetailsToDatabase(
                    userRefNumber,
                    userId,
                    username,
                    email,
                    hashedPassword
                )
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Database read failed: ${error.message}")
            }
        })
    }

    private fun saveUserDetailsToDatabase(
        userRefNumber: String,
        userId: String,
        username: String,
        email: String,
        hashedPassword: String
    ) {
        val userDetailsRef = database.child("Customers").child("customerDetails").child(userRefNumber)

        val user = hashMapOf(
            "userId" to userRefNumber,
            "username" to username,
            "email" to email,
            "password" to hashedPassword
           // "profile_image" to "-"
        )

        userDetailsRef.setValue(user).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                showToast("User details saved successfully")
                navigateToHomePage()
                finish()
            } else {
                showToast("Failed to save user details: ${task.exception?.message}")
            }
        }
    }

    private fun navigateToHomePage() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }

    private fun Context.showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}


