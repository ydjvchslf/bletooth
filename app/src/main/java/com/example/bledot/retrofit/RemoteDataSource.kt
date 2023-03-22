package com.example.bledot.retrofit

import com.example.bledot.data.Product
import com.example.bledot.data.UserInfoEntity
import com.example.bledot.data.request.RequestCommonData
import com.example.bledot.data.response.toEntity
import com.example.bledot.util.BleDebugLog
import com.example.bledot.util.isGoogleUser
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File


class RemoteDataSource {

    private val logTag = RemoteDataSource::class.simpleName
    private val retrofitService = RetrofitClient.retrofitService

    suspend fun checkEmailDuplication(email: String, retCode: (Int?) -> Unit) {
        BleDebugLog.w(logTag, "checkEmailDuplication-()")
        val reqData = toReqCmmData(email, null, null, null)

        val response = retrofitService.checkEmail(reqData)
        return when (response) {
            is Result.Success -> {
                BleDebugLog.i(logTag, "Result Success!!")
                when (response.data.resultCode) {
                    200 -> { retCode.invoke(200) }
                    -1 -> { retCode.invoke(-1) }
                    else -> { retCode.invoke(null) }
                }
            }
            is Result.ApiError -> {
                BleDebugLog.i(logTag, "ApiError!!")
                retCode.invoke(null)
            }
            is Result.NetworkError -> {
                BleDebugLog.i(logTag, "NetworkError!!")
                retCode.invoke(null)
            }
            else -> {
                retCode.invoke(null)
            }
        }
    }

    suspend fun checkAlreadyUser(email: String, result: (Boolean?) -> Unit) {
        BleDebugLog.w(logTag, "checkAlreadyUser-()")
        val reqData = toReqCmmData(email, null, null, null)

        val response = retrofitService.checkUserInfoExist(reqData)
        return when (response) {
            is Result.Success -> {
                BleDebugLog.i(logTag, "Result Success!!")
                when (response.data.resultCode) {
                    200 -> { result.invoke(true) }
                    -1 -> { result.invoke(false) }
                    else -> { result.invoke(null) }
                }
            }
            is Result.ApiError -> {
                BleDebugLog.i(logTag, "ApiError!!")
                result.invoke(null)
            }
            is Result.NetworkError -> {
                BleDebugLog.i(logTag, "NetworkError!!")
                result.invoke(null)
            }
            else -> {
                result.invoke(null)
            }
        }
    }

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

    suspend fun registerServer(userInfo: UserInfoEntity, resResult: (Int?, String?) -> Unit) {
        BleDebugLog.w(logTag, "registerServer-()")
        val reqRegData = UserInfoEntity.fromUserEntityToReqData(userInfo)

        val response = retrofitService.signUp(reqRegData)
        when (response) {
            is Result.Success -> { // Success<T>(val code: Int, val data: T)
                BleDebugLog.i(logTag, "Api Success!!")
                // response.code = 200, response.data = response.body()
                val resBody = response.data
                val resCode = resBody.resultCode
                val resMsg = resBody.resultMessage
                val token = resBody.token

                when (resCode) {
                    200 -> {
                        BleDebugLog.d(logTag, resMsg)
                        resResult.invoke(resCode, token)
                    }
                    -1 -> {
                        BleDebugLog.d(logTag, resMsg)
                        resResult.invoke(resCode, null)
                    }
                    -2 -> {
                        BleDebugLog.d(logTag, resMsg)
                        resResult.invoke(resCode, null)
                    }
                    else -> { resResult.invoke(null, null) }
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

    suspend fun loginServer(email: String, pw: String?, retCode: (Int?, String?) -> Unit) {
        BleDebugLog.w(logTag, "login-()")
        val reqData = toReqCmmData(
            email,
            if (isGoogleUser(email)) { "google" } else { "email" },
            pw,
            null
        )

        val response = retrofitService.login(reqData)
        when (response) {
            is Result.Success -> { // Success<T>(val code: Int, val data: T)
                BleDebugLog.i(logTag, "Api Success!!")
                // response.code = 200, response.data = response.body()
                val resBody = response.data
                val resCode = resBody.resultCode
                val resMsg = resBody.resultMessage
                val token = resBody.token

                when (resCode) {
                    200 -> {
                        BleDebugLog.d(logTag, resMsg)
                        retCode.invoke(resCode, token)
                    }
                    -1 -> {
                        BleDebugLog.d(logTag, resMsg)
                        retCode.invoke(resCode, null)
                    }
                    -2 -> {
                        BleDebugLog.d(logTag, resMsg)
                        retCode.invoke(resCode, null)
                    }
                    -3 -> {
                        BleDebugLog.d(logTag, resMsg)
                        retCode.invoke(resCode, null)
                    }
                    else -> { retCode.invoke(null, null) }
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

    suspend fun getUserInfo(token: String, email: String, userCallback: (UserInfoEntity?) -> Unit) {
        BleDebugLog.w(logTag, "getUserInfo-()")
        val reqData = toReqCmmData(email, null, null, null)

        val response = retrofitService.getUser(token, reqData)
        when (response) {
            is Result.Success -> {
                BleDebugLog.i(logTag, "Api Success!!")
                val resBody = response.data
                val resCode = resBody.resultCode
                val resMsg = resBody.resultMessage

                when (resCode) {
                    200 -> {
                        BleDebugLog.d(logTag, "resMsg: $resMsg")
                        userCallback.invoke(resBody.toEntity())
                    }
                    -1 -> {
                        BleDebugLog.d(logTag, "resMsg: $resMsg")
                        userCallback.invoke(null)
                    }
                    else -> { userCallback.invoke(null) }
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

    }

    suspend fun uploadToServer(email: String, file: File, resultCallback: (Boolean) -> Unit) {
        BleDebugLog.w(logTag, "uploadToServer-()")

        // multipart 작업
        val formId = MultipartBody.Part.createFormData("email", email)

        val requestBody = RequestBody.create("text/csv".toMediaTypeOrNull(), file)
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

    suspend fun enrollMembership(inputMemNum: String, resultCallback: (Boolean) -> Unit) {
        BleDebugLog.w(logTag, "enrollMembership-()")
        resultCallback.invoke(true)
    }

    suspend fun withdrawAccount(token: String, email: String, result: (Boolean) -> Unit) {
        BleDebugLog.w(logTag, "withdrawAccount-()")
        val reqData = toReqCmmData(email, null, null, null)

        val response = retrofitService.deleteAccount(token, reqData)
        when (response) {
            is Result.Success -> {
                BleDebugLog.i(logTag, "Api Success!!")
                val resBody = response.data
                val resCode = resBody.resultCode
                val resMsg = resBody.resultMessage

                when (resCode) {
                    200 -> {
                        BleDebugLog.d(logTag, "resMsg: $resMsg")
                        result.invoke(true)
                    }
                    -1 -> {
                        BleDebugLog.d(logTag, "resMsg: $resMsg")
                        result.invoke(false)
                    }
                    else -> { result.invoke(false) }
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
    }

    private fun sampleUserEntity(): UserInfoEntity {
        return UserInfoEntity(
            "abc@naver.com",
            "테스트",
            "1991-09-14",
            "Female",
            "55",
            "1",
            "korean",
            "감기",
            "race",
            "주소1",
            "주소2",
            "주소3",
            "주소4",
            "Korea",
            "11921",
            "korea",
            null
        )
    }

    private fun toReqCmmData(email: String, vender: String?,
                             pwd: String?, token: String?): RequestCommonData {
        BleDebugLog.w(logTag, "toReqCmmData-()")
        return RequestCommonData(
            email = email,
            vender = vender,
            pwd = pwd,
            token = token
        )
    }
}