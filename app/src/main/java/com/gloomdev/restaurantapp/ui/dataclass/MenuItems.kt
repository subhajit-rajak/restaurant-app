package com.gloomdev.restaurantapp.ui.dataclass

import android.net.Uri


data class MenuItems(
    var foodName: String,
    var foodDescription: String,
    var foodPrice: String,
    var foodImage: String,
    var key: String
) {
    constructor() : this("", "", "", "", "")
}
