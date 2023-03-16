package com.example.bledot.retrofit

import com.example.bledot.util.BleDebugLog
import com.google.gson.Gson
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

sealed class Result<T> {
    class Success<T>(val code: Int, val data: T) : Result<T>()
    class ApiError<T>(val code: Int, val message: String?) : Result<T>()
    class NetworkError<T>(val throwable: Throwable) : Result<T>()
    // 최종 결과 response
    class CustomResponse<T>(val code: Int, val data: T?, val message: String?) : Result<T>()
}

class ResponseCall<T> constructor(
    private val callDelegate: Call<T>
) : Call<Result<T>> {

    private val logTag = ResponseCall::class.simpleName

    override fun enqueue(callback: Callback<Result<T>>) {
        callDelegate.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                BleDebugLog.i(logTag, "onResponse-()")
                when(response.code()) {
                    in 200..299 -> { // 200, response.body()
                        val resBody = response.body()
                        resBody?.let {
                            //BleDebugLog.httpResponse(Gson().toJson(resBody))
                            callback.onResponse(this@ResponseCall, Response.success(Result.Success(response.code(), it)))
                        }
                    }
                    in 400..409 -> {
                        val resBody = response.body()
                        //BleDebugLog.httpResponse(Gson().toJson(resBody))
                        callback.onResponse(this@ResponseCall, Response.success(Result.ApiError(response.code(), response.message()))) // 400대, response.message()
                    }
                }

                /*
                response.body()?.let {
                    when(response.code()) {
                        in 200..299 -> {
                            response.body()
                            callback.onResponse(this@ResponseCall, Response.success(Result.Success(response.code(), it))) // 200, response.body()
                        }
                        in 400..409 -> {
                            response.body()
                            callback.onResponse(this@ResponseCall, Response.success(Result.ApiError(response.code(), response.message()))) // 400대, response.message()
                        }
                    }
                }
                 */
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                BleDebugLog.i(logTag, "onFailure-()")
                BleDebugLog.e(t.toString())
                callback.onResponse(this@ResponseCall, Response.success(Result.NetworkError(t)))
                call.cancel()
            }

        })
    }

    override fun clone(): Call<Result<T>> = ResponseCall(callDelegate.clone())

    override fun execute(): Response<Result<T>> = throw UnsupportedOperationException("ResponseCall does not support execute.")

    override fun isExecuted(): Boolean = callDelegate.isExecuted

    override fun cancel() = callDelegate.cancel()

    override fun isCanceled(): Boolean = callDelegate.isCanceled

    override fun request(): Request = callDelegate.request()

    override fun timeout(): Timeout = callDelegate.timeout()

}