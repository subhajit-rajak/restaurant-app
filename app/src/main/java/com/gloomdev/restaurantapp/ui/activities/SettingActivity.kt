package com.gloomdev.restaurantapp.ui.activities

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gloomdev.restaurantapp.R
import com.gloomdev.restaurantapp.ui.adapter.PastOrdersAdapter
import com.gloomdev.restaurantapp.ui.dataclass.Order

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        val recyclerView: RecyclerView = findViewById(R.id.past_orders_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = PastOrdersAdapter(pastOrders)

        //Removing Action Bar
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }

    val pastOrders = listOf(
        Order(
            restaurantName = "Grover Mithaiwala",
            orderStatus = "Delivered",
            orderAmount = 388.0,
            orderDetails = "Lorem Ipsum (1)",
            orderDateTime = "Jul 20, 2023, 08:48 AM"
        ),
        Order(
            restaurantName = "The Cubano Sandwich Co.",
            orderStatus = "Delivered",
            orderAmount = 312.0,
            orderDetails = "[veg] Grilled Veggie Parmesan Cubano Sandwich (1), Blue Lemonade (1)",
            orderDateTime = "Jul 19, 2023, 12:20 AM"
        )
        // Add more orders as needed
    )

}