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

    fun normalLogin(email: String, pw: String, result: (Boolean) -> Unit) {
        BleDebugLog.i(logTag, "normalLogin-()")
        viewModelScope.launch {
            result.invoke(false)
        }
    }

    fun checkUserInfo(email: String, isExist: (Boolean) -> Unit) {
        BleDebugLog.i(logTag, "checkUserInfo-()")
        isExist.invoke(true)
    }
}