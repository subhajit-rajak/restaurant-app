package com.gloomdev.restaurantapp.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gloomdev.restaurantapp.databinding.ActivityRegisterBinding
import com.gloomdev.restaurantapp.ui.dataclass.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var username: String
    private lateinit var confirmpassword: String
    private lateinit var database: DatabaseReference

    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Removing Action Bar
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        auth = Firebase.auth
        database = Firebase.database.reference

        binding.registerButton.setOnClickListener {
            username = binding.name.text.toString().trim()
            email = binding.email.text.toString().trim()
            password = binding.password.text.toString().trim()
            confirmpassword = binding.confirmpassword.text.toString().trim()

            if(username.isBlank() || email.isBlank() || password.isBlank() != confirmpassword.isBlank()) {
                Toast.makeText(this, "Fill all credentials", Toast.LENGTH_SHORT).show()
            } else {
                createAccount(username, email, password)
            }
        }

        binding.loginToExistingAccount.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    // creates a new account
    private fun createAccount(
        username: String,
        email: String,
        password: String,

    ) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->

            if(task.isSuccessful) {
                saveUserData(username, email)
                Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Account creation failed", Toast.LENGTH_SHORT).show()
                Log.d("Account", "createAccount: Failure", task.exception)
            }
        }
    }

    // saves user data in database
    private fun saveUserData(
        username: String,
        email: String
    ) {
        val user = User(username, email)
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
//       val userId = database.child("Customers").child("customerDetails")
//        database.child("user").child(userId).setValue(user)
         database.child("Customers").child("customerDetails").child(userId).setValue(user)

    }
    private fun Context.showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

