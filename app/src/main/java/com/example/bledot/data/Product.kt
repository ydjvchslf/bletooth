package com.example.bledot.data

import com.google.gson.annotations.SerializedName

data class Product (
    @SerializedName("id") val id: Int, //서버에서는 "id"을 가져와서, app 에선 id 라는 값으로 쓰겠다
    @SerializedName("title") val title: String,
    @SerializedName("price") val price: Double,
    @SerializedName("rating") val rating: Rating
)

data class Rating (
    @SerializedName("rate") var rate: Double
)
