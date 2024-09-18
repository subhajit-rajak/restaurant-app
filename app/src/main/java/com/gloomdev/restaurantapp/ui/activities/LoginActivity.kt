package com.gloomdev.restaurantapp.ui.activities


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gloomdev.restaurantapp.R
import com.gloomdev.restaurantapp.databinding.ActivityLoginBinding

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var binding: ActivityLoginBinding

    //check if user already signed in
    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        if(currentUser!=null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Removing Action Bar
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        auth = Firebase.auth
        database = Firebase.database.reference
//
//        binding.email.setText(getString(R.string.guestLoginEmail))
//        binding.password.setText(getString(R.string.guestLoginPassword))

        binding.loginButton.setOnClickListener {
            email = binding.email.text.toString().trim()
            password = binding.password.text.toString().trim()
            if(email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Fill all credentials", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(email, password)
            }
        }

        binding.forgotPassword.setOnClickListener {
            if(binding.email.text.toString().trim().isBlank()) {
                Toast.makeText(this, "Enter your email", Toast.LENGTH_SHORT).show()
            } else {
                auth.sendPasswordResetEmail(binding.email.text.toString().trim())
                    .addOnSuccessListener {
                        Toast.makeText(this, "Email sent", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        binding.createNewAccount.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // user logging in
    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "${task.exception!!.message}", Toast.LENGTH_SHORT).show()
                Log.d("Account", "loginUser: Failed", task.exception)
            }
        }
    }
}