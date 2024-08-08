package com.gloomdev.restaurantapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gloomdev.restaurantapp.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    //Declare binding variable
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.getRoot())

        //Login button click listener
        binding.login.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}