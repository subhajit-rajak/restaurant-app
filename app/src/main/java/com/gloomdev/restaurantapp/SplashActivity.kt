package com.gloomdev.restaurantapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.gloomdev.restaurantapp.databinding.ActivitySplashScreen2Binding


class SplashActivity : AppCompatActivity() {

    //Declare binding variable
    private lateinit var binding: ActivitySplashScreen2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreen2Binding.inflate(layoutInflater)
        setContentView(binding.root)


        //Removing Action Bar
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        //Setting splash screen
        Handler().postDelayed({
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.transition_enter, R.anim.transition_exit)
            finish()
        }, 2000)

    }
}