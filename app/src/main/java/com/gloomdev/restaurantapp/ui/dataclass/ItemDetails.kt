package com.gloomdev.restaurantapp.ui.dataclass

data class ItemDetails(var userUid: String? = null,
                       var userName: String? = null,
                       var foodName: String? = null,
                       var foodImages: String? = null,
                       var foodPrices: Int? = null,
                       var foodPrice:String? = null,
                       var RestuarantId: String? = null,
                       var foodQuantities: Int? = null,
//                       var address: String? = null,
//                       var totalPrice: String? = null,
//                       var phoneNumber: String? = null,
//                       var orderAccepted: Boolean = false,
//                       var paymentReceived: Boolean = false,
//                       var itemPushKey: String? = null,
//                       var currentTime: Long = 0
)
