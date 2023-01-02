package com.example.bledot.detail

import androidx.lifecycle.ViewModel
import com.example.bledot.data.Product
import com.example.bledot.retrofit.RetrofitClient
import com.example.bledot.util.BleDebugLog
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DetailViewModel : ViewModel() {

    private val logTag = DetailViewModel::class.simpleName
    private val retrofitService = RetrofitClient.retrofitService

    init {
        BleDebugLog.i(logTag, "init-()")
    }

    fun getProductList() {
        BleDebugLog.i(logTag, "getProductList-()")
        retrofitService.getProductList().enqueue(object : Callback<List<Product>> {
            override fun onResponse(
                call: Call<List<Product>>, response: Response<List<Product>>
            ) {
                if(response.isSuccessful) {
                    BleDebugLog.i(logTag, "Success!!")
                    BleDebugLog.d(logTag, response.body().toString())
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                BleDebugLog.i(logTag, "Fail!!")
                BleDebugLog.d(logTag, t.toString())
            }

        })
    }
}
