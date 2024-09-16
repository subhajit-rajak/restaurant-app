package com.gloomdev.restaurantapp.ui.dataclass

data class RestaurantListHome(
    var IdOfRestaurant: String,
    var restaurantImage: String,
    var description: String,
    var userName: String? = null,
    var email: String? = null,
    var phone: String? = null,
    var nameOfRestaurant: String? = null,
    var location: String? = null,){
    constructor(): this("","","","","","","","")
}
