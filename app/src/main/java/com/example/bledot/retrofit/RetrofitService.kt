package com.example.bledot.retrofit

import com.example.bledot.data.Product
import com.example.bledot.data.request.RequestCommonData
import com.example.bledot.data.request.RequestFileData
import com.example.bledot.data.request.RequestRegData
import com.example.bledot.data.response.RemoteDefaultData
import okhttp3.MultipartBody
import retrofit2.http.*

interface RetrofitService {

    @GET("products")
    suspend fun getProductList(): Result<List<Product>>

    @POST("member/checkEmail")
    suspend fun checkEmail(@Body reqCmmData: RequestCommonData): Result<RemoteDefaultData>

    @POST("member/isExist")
    suspend fun checkUserInfoExist(@Body reqCmmData: RequestCommonData): Result<RemoteDefaultData>

    @Headers("Content-Type: application/json")
    @POST("login")
    suspend fun login(@Body reqCmmData: RequestCommonData): Result<RemoteDefaultData>

    @Headers("Content-Type: application/json")
    @POST("member/register")
    suspend fun signUp(@Body reqRegData: RequestRegData): Result<RemoteDefaultData>

    @GET("user/{userId}")
    suspend fun getUser(): Result<RemoteDefaultData>

    @Multipart
    @POST("data/send")
    suspend fun uploadData(
        @Part email: MultipartBody.Part,
        @Part file: MultipartBody.Part
    ): Result<RequestFileData>
}