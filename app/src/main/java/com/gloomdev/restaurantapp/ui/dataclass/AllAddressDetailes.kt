package com.gloomdev.restaurantapp.ui.dataclass

data class AllAddressDetailes(val userRefNumber:String,
                              val name:String,
                              val mobile:String,
                              val flat:String,
                              val landmark:String,
                              val area:String,
                              val city:String,
                              val state:String,
                              val pincode:String,
                              val typeOfAddress:String){
    constructor(): this("","","","","","","","","","")
}
