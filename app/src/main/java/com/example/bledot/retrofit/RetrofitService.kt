package com.example.bledot.retrofit

import com.example.bledot.data.Product
import com.example.bledot.data.RemoteDefaultData
import retrofit2.Call
import retrofit2.http.GET

interface RetrofitService {

    @GET("products")
    suspend fun getProductList(): Result<List<Product>>

    @GET("login_s")
    suspend fun loginSuccess(): Result<RemoteDefaultData>

    @GET("user/{userId}")
    suspend fun getUser(): Result<RemoteDefaultData>
}