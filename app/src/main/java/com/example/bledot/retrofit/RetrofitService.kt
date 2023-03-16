package com.example.bledot.retrofit

import com.example.bledot.data.Product
import com.example.bledot.data.request.RequestEmailPwData
import com.example.bledot.data.request.RequestFileData
import com.example.bledot.data.response.RemoteDefaultData
import okhttp3.MultipartBody
import retrofit2.http.*

interface RetrofitService {

    @GET("products")
    suspend fun getProductList(): Result<List<Product>>

    @POST("member/checkEmail")
    suspend fun checkEmail(@Body reqEmailPwData: RequestEmailPwData): Result<RemoteDefaultData>

    @Headers("Content-Type: application/json")
    @POST("login")
    suspend fun login(@Body reqEmailPwData: RequestEmailPwData): Result<RemoteDefaultData>

    @GET("user/{userId}")
    suspend fun getUser(): Result<RemoteDefaultData>

    @Multipart
    @POST("data/send")
    suspend fun uploadData(
        @Part email: MultipartBody.Part,
        @Part file: MultipartBody.Part
    ): Result<RequestFileData>
}