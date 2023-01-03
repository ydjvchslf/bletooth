package com.example.bledot.retrofit

import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

sealed class Result<T> {
    class Success<T>(val code: Int, val data: T) : Result<T>()
    class Loading<T> : Result<T>()
    class ApiError<T>(val code: Int, val message: String) : Result<T>()
    class NetworkError<T>(val throwable: Throwable) : Result<T>()
    class NullResult<T> : Result<T>()
    // 최종 결과 response
    class CustomResponse<T>(val code: Int, val data: T?, val message: String?) : Result<T>()
}

class ResponseCall<T> constructor(
    private val callDelegate: Call<T>
) : Call<Result<T>> {

    override fun enqueue(callback: Callback<Result<T>>) {
        callDelegate.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                response.body()?.let {
                    when(response.code()) {
                        in 200..299 -> {
                            callback.onResponse(this@ResponseCall, Response.success(Result.Success(response.code(), it)))
                        }
                        in 400..409 -> {
                            callback.onResponse(this@ResponseCall, Response.success(Result.ApiError(response.code(), response.message())))
                        }
                        5000 -> { // ex) 로그인 에러 (계정 틀림)
                            callback.onResponse(this@ResponseCall, Response.success(Result.ApiError(response.code(), "Login Error!")))
                        }
                    }
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
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