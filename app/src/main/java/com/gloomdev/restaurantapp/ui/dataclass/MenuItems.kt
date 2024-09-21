package com.gloomdev.restaurantapp.ui.dataclass


data class MenuItems(
    var foodName: String,
    var foodDescription: String,
    var foodImage: String,
    var key: String,
    var foodPrices: Int? = null,
    var foodPrice:String? = null,
    var foodQuantities: Int? = null

) {
    constructor() : this("", "", "", "", 0, "",0)
}
