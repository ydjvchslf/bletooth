package com.example.bledot.retrofit

import com.example.bledot.data.Product
import com.example.bledot.util.BleDebugLog

class RemoteDataSource {

    private val logTag = RemoteDataSource::class.simpleName
    private val retrofitService = RetrofitClient.retrofitService

    suspend fun getAllProducts(retCode: (Int) -> Unit): List<Product>? {
        BleDebugLog.w(logTag, "getAllProducts-()")
        val response = retrofitService.getProductList()
        return when (response) {
            is Result.Success -> {
                BleDebugLog.i(logTag, "Result Success!!")
                BleDebugLog.d(logTag, "response.code => ${response.code}")
                BleDebugLog.d(logTag, "response.data => ${response.data}")
                retCode.invoke(response.code)
                return response.data
            }
            is Result.ApiError -> {
                BleDebugLog.i(logTag, "ApiError!!")
                if (response.code == 5000) { // 로그인에러
                    BleDebugLog.i(logTag, "Login error")
                }
                retCode.invoke(5000)
                return null
            }
            is Result.NetworkError -> {
                BleDebugLog.i(logTag, "NetworkError!!")
                BleDebugLog.d(logTag, "response.throwable => ${response.throwable}")
                retCode.invoke(9999)
                return null
            }
            else -> {
                retCode.invoke(9999)
                null
            }
        }
    }
}