package com.example.bledot.retrofit

import com.example.bledot.data.*
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

    suspend fun loginServer(retCode: (Int?, UserInfoEntity?) -> Unit) {
        BleDebugLog.w(logTag, "login-()")
        val response = retrofitService.loginSuccess()
        when (response) {
            is Result.Success -> { // Success<T>(val code: Int, val data: T)
                BleDebugLog.i(logTag, "Api Success!!")
                // response.code = 200, response.data = response.body()
                val resBody = response.data
                val statusCode = resBody.statusCode
                if (statusCode == 200) {
                    BleDebugLog.d(logTag, "로그인 성공!")
                    retCode.invoke(200, resBody.data?.toEntity())
                } else if (statusCode == 5555) {
                    BleDebugLog.d(logTag, "로그인 실패!")
                    retCode.invoke(5555, null)
                }
            }
            is Result.ApiError -> { // ApiError<T>(val code: Int, val message: String)
                BleDebugLog.i(logTag, "ApiError!!")
                // response.code = 400대, response.message()
                BleDebugLog.d(logTag, "Error Code: [${response.code}], message: ${response.message}")
            }
            is Result.NetworkError -> { // NetworkError<T>(val throwable: Throwable)
                BleDebugLog.i(logTag, "NetworkError!!")
                // throwable
                BleDebugLog.d(logTag, "${response.throwable}")
            }
            else -> { }
        }
    }
}