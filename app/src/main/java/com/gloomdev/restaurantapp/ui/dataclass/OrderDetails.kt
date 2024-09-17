package com.gloomdev.restaurantapp.ui.dataclass

data class OrderDetails(
    var userUid: String? = null,
    var userName: String? = null,
    var foodNames: MutableList<String>? = null,
    var foodImages: MutableList<String>? = null,
    var foodPrices: MutableList<String>? = null,
    var foodQuantities: MutableList<Int>? = null,
    var address: String? = null,
    var totalPrice: String? = null,
    var phoneNumber: String? = null,
    var orderAccepted: Boolean = false,
    var paymentReceived: Boolean = false,
    var itemPushKey: String? = null,
    var currentTime: String? = null)
