package com.example.bledot.retrofit

import com.example.bledot.data.Product
import retrofit2.Call
import retrofit2.http.GET

interface RetrofitService {

    @GET("products")
    fun getProductList(): Call<List<Product>>
}