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
    lateinit var crnUserInfo: UserInfoEntity

    init {
        BleDebugLog.i(logTag, "init-()")
    }

    fun getMyInfo(userInfo: (UserInfoEntity) -> Unit) {
        BleDebugLog.i(logTag, "getMyInfo-()")
        viewModelScope.launch  {
            // TODO :: 내 정보 보기 api
            remoteDataSource.getUserInfo("userId") {
                crnUserInfo = it
                userInfo.invoke(it)
            }
        }
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

    fun isGoogleUser(): Boolean {
        BleDebugLog.i(logTag, "parseEmail-()")
        val words = crnUserInfo.email.split("@")
        BleDebugLog.d(logTag, "words[1]: ${words[1]}")
        if (words[1] == "gmail.com") {
            return true
        }
        return false
    }
}
