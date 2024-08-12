package com.gloomdev.restaurantapp.ui.dataclass

data class Order(
    val restaurantName: String,
    val orderStatus: String,  // e.g., "Delivered"
    val orderAmount: Double,  // e.g., 388.0
    val orderDetails: String, // e.g., "Lorem Ipsum (1)"
    val orderDateTime: String // e.g., "Jul 20, 2023, 08:48 AM"
){

}
