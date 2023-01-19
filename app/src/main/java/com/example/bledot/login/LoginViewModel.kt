package com.example.bledot.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bledot.data.UserInfoEntity
import com.example.bledot.retrofit.RemoteDataSource
import com.example.bledot.util.BleDebugLog
import kotlinx.coroutines.launch

class LoginViewModel: ViewModel() {

    private val logTag = LoginViewModel::class.simpleName
    private val remoteDataSource = RemoteDataSource()

    init {
        BleDebugLog.i(logTag, "init-()")
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

    fun checkUserInfo(email: String, isExist: (Boolean) -> Unit) {
        BleDebugLog.i(logTag, "checkUserInfo-()")
        isExist.invoke(true)
    }
}