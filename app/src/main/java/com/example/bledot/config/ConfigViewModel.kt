package com.example.bledot.config

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bledot.data.UserInfoEntity
import com.example.bledot.retrofit.RemoteDataSource
import com.example.bledot.util.BleDebugLog
import kotlinx.coroutines.launch


class ConfigViewModel : ViewModel() {

    private val logTag = ConfigViewModel::class.simpleName
    private val remoteDataSource = RemoteDataSource()

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
