package com.example.bledot.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bledot.data.Product
import com.example.bledot.retrofit.RemoteDataSource
import com.example.bledot.retrofit.RetrofitClient
import com.example.bledot.util.BleDebugLog
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DetailViewModel : ViewModel() {

    private val logTag = DetailViewModel::class.simpleName
    private val remoteDataSource = RemoteDataSource()
    //var serverRetCode: Int? = null

    init {
        BleDebugLog.i(logTag, "init-()")
    }

   fun getProductList(serverRetCode: (Int) -> Unit) {
        BleDebugLog.i(logTag, "getProductList-()")
        viewModelScope.launch  {
            remoteDataSource.getAllProducts { retCode ->
                if (retCode == 200) {
                    serverRetCode.invoke(200)
                }
            }
        }
    }
}
