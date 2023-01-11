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

    fun login(user: (Int?, UserInfoEntity?) -> Unit) {
        BleDebugLog.i(logTag, "login-()")
        viewModelScope.launch {
            remoteDataSource.loginServer { retCode, userInfoEntity -> // 200 or 5555
                when (retCode) {
                    200 -> {
                        user.invoke(200, userInfoEntity)
                    }
                    5555 -> {
                        user.invoke(5555, null)
                    }
                    else -> {
                        return@loginServer
                    }
                }
            }
        }
    }

}
