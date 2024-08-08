package com.gloomdev.restaurantapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gloomdev.restaurantapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    //Declare binding variable
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.getRoot())

        //Signup button click listener
        binding.signUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}