package com.gloomdev.restaurantapp.ui.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gloomdev.restaurantapp.R
import com.gloomdev.restaurantapp.databinding.ActivityLoginBinding
import com.gloomdev.restaurantapp.ui.dataclass.User
import com.gloomdev.restaurantapp.ui.fragments.Home
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.security.MessageDigest
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    //Variables
    private lateinit var binding: ActivityLoginBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Removing Action Bar
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        //Login Button
        binding.loginButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        //Register Button
        binding.createNewAccount.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        //Firebase
        auth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        database = FirebaseDatabase.getInstance().reference

        if (isUserLoggedIn()) {
            navigateToHomePage()
        }
        val currentUser = auth.currentUser

        if (currentUser != null && sharedPreferences.
            getBoolean("rememberMe", false)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.rememberMe.isChecked = sharedPreferences
            .getBoolean("rememberMe", false)
        binding.rememberMe.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPreferences.edit()
            editor.putBoolean("rememberMe", isChecked)
            if (!isChecked) {
                editor.remove("email")
                editor.remove("password")
            }
            editor.apply()
        }
        binding.loginButton.setOnClickListener {
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                val hashedPassword = hashPassword(password)
                checkCredentials(email, hashedPassword)
            } else {
                showToast("Please enter email and password")
            }
        }

        binding.createNewAccount.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }
    //Functions
    private fun isUserLoggedIn(): Boolean {
        val email = sharedPreferences.getString("email", null)
        val password = sharedPreferences.getString("password", null)
        return email != null && password != null
    }

    private fun saveUserCredentials(email: String, password: String) {
        if (binding.rememberMe.isChecked) {
            val editor = sharedPreferences.edit()
            editor.putString("email", email)
            editor.putString("password", password)
            editor.apply()
        }
    }

    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("", { str, it -> str + "%02x".format(it) })
    }

    private fun checkCredentials(email: String, hashedPassword: String) {
        database.child("Customers").child("customerDetails")
            .orderByChild("email")
            .equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            val user = userSnapshot.getValue(User::class.java)
                            if (user != null && user.password == hashedPassword) {
                                showToast("Login successful!")
                                saveUserDataToSharedPreferences(user)
                                saveUserCredentials(email, hashedPassword)
                                navigateToHomePage()
                                return
                            }
                        }
                        showToast("Invalid email or password")
                    } else {
                        showToast("Invalid email or password")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    showToast("Error: ${error.message}")
                }
            })
    }

    private fun saveUserDataToSharedPreferences(user: User) {
        val editor = sharedPreferences.edit()
        editor.putString("userId", user.userId)
        editor.putString("email", user.email)
        editor.putString("username", user.username)
        editor.apply()
    }

    private fun navigateToHomePage() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}