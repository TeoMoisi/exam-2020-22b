package com.example.marketapp.models

import com.google.gson.annotations.SerializedName

data class Product (
    @field:SerializedName("id")
    var id: Int = 0,
    @field:SerializedName("name")
    var name: String = "",
    @field:SerializedName("specs")
    var specs: String = "",
    @field:SerializedName("height")
    var height: Int,
    @field:SerializedName("type")
    var type: String = "",
    @field:SerializedName("age")
    var age :Int = 0)