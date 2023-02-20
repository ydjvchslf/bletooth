package com.example.bledot.retrofit

import com.example.bledot.data.Product
import com.example.bledot.data.request.RequestEmailPwData
import com.example.bledot.data.request.RequestFileData
import com.example.bledot.data.response.RemoteDefaultData
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface RetrofitService {

    @GET("products")
    suspend fun getProductList(): Result<List<Product>>

    @POST("member/checkEmail")
    suspend fun checkEmail(@Body reqEmailPwData: RequestEmailPwData): Result<RemoteDefaultData>

    @GET("login_s")
    suspend fun loginSuccess(): Result<RemoteDefaultData>

    @GET("user/{userId}")
    suspend fun getUser(): Result<RemoteDefaultData>

    @Multipart
    @POST("post")
    suspend fun uploadData(
        @Part email: MultipartBody.Part,
        @Part file: MultipartBody.Part
    ): Result<RequestFileData>
}