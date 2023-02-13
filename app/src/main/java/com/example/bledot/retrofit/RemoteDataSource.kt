package com.example.bledot.retrofit

import com.example.bledot.data.Product
import com.example.bledot.data.UserInfoEntity
import com.example.bledot.data.toEntity
import com.example.bledot.util.BleDebugLog
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


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

    suspend fun getUserInfo(userCallback: (UserInfoEntity) -> Unit) {
        BleDebugLog.w(logTag, "getUserInfo-()")
        val sampleUser = sampleUserEntity()
        userCallback.invoke(sampleUser)
    /*
        val response = retrofitService.getUser()
        when (response) {
            is Result.Success -> {
                BleDebugLog.i(logTag, "Api Success!!")
                val resBody = response.data
                val statusCode = resBody.statusCode
                if (statusCode == 200) {
                    BleDebugLog.d(logTag, "userInfo 가져오기 성공")
                    val remoteUserInfo = resBody.data
                    val userInfoEntity = remoteUserInfo?.toEntity()
                    userCallback.invoke(userInfoEntity)
                }
            }
            is Result.ApiError -> {
                BleDebugLog.i(logTag, "ApiError!!")
                BleDebugLog.d(logTag, "Error Code: [${response.code}], message: ${response.message}")
            }
            is Result.NetworkError -> {
                BleDebugLog.i(logTag, "NetworkError!!")
                // throwable
                BleDebugLog.d(logTag, "${response.throwable}")
            }
            else -> { }
        }
    */
    }

    suspend fun uploadToServer(email: String, file: File, resultCallback: (Boolean) -> Unit) {
        BleDebugLog.w(logTag, "uploadToServer-()")

        // multipart 작업
        val formId = MultipartBody.Part.createFormData("email", email)

        val requestBody = RequestBody.create(MediaType.parse("text/csv"), file)
        val multipartBody = MultipartBody.Part.createFormData("file", file.name, requestBody)

        val response = retrofitService.uploadData(formId, multipartBody)

        when (response) {
            is Result.Success -> { // Success<T>(val code: Int, val data: T)
                BleDebugLog.i(logTag, "Api Success!!")
                // response.code = 200, response.data = response.body()
                val resBody = response.data
                BleDebugLog.d(logTag, "resBody: $resBody")
                resultCallback.invoke(true)
            }
            is Result.ApiError -> { // ApiError<T>(val code: Int, val message: String)
                BleDebugLog.i(logTag, "ApiError!!")
                // response.code = 400대, response.message()
                BleDebugLog.d(logTag, "Error Code: [${response.code}], message: ${response.message}")
                resultCallback.invoke(false)
            }
            is Result.NetworkError -> { // NetworkError<T>(val throwable: Throwable)
                BleDebugLog.i(logTag, "NetworkError!!")
                // throwable
                BleDebugLog.d(logTag, "${response.throwable}")
                resultCallback.invoke(false)
            }
            else -> { resultCallback.invoke(false) }
        }
    }

    private fun sampleUserEntity(): UserInfoEntity {
        return UserInfoEntity(
            "abc@naver.com",
            "용미경",
            "1991-09-14",
            "Female",
            "55",
            "1",
            "korean",
            "감기",
            315679995,
            "주소1",
            "주소2",
            "주소3",
            "주소4",
            "Korea"
        )
    }
}