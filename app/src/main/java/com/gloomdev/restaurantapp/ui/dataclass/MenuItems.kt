package com.gloomdev.restaurantapp.ui.dataclass


data class MenuItems(
    var foodName: String,
    var foodDescription: String,
    var foodPrice: String,
    var foodImage: String,
    var key: String
) {
    constructor() : this("", "", "", "", "")
}
