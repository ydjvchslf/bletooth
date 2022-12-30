package com.example.bledot.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {
    private const val BASE_URL = "https://fakestoreapi.com"

    //Retrofit 객체 초기화
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(this.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val retrofitService: RetrofitService = retrofit.create(RetrofitService::class.java)
}