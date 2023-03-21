package com.example.bledot.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bledot.App
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
            remoteDataSource.loginServer(email, pw) { retCode, token ->
                if (retCode == 200) {
                    // TODO :: token, email -> Pref 저장
                    //App.prefs.setString("token", "$token")
                    App.prefs.setString("token", "token::normal login")
                    App.prefs.setString("email", email)
                    result.invoke(true)
                } else { // -1, -2, -3, null
                    result.invoke(false)
                }
            }
        }
    }

    fun googleLogin(email: String, result: (Boolean) -> Unit) {
        BleDebugLog.i(logTag, "googleLogin-()")
        viewModelScope.launch {
            remoteDataSource.loginServer(email, null) { retCode, token ->
                if (retCode == 200) {
                    // TODO :: token, email -> Pref 저장
                    //App.prefs.setString("token", "$token")
                    App.prefs.setString("token", "token::google login")
                    App.prefs.setString("email", email)
                    result.invoke(true)
                } else { // -1, -2, -3, null
                    result.invoke(false)
                }
            }
        }
    }

    fun checkUserInfo(email: String, isExist: (Boolean) -> Unit) {
        BleDebugLog.i(logTag, "checkUserInfo-()")
        // TODO:: 유저정보 유무 체크 api (/member/isExist)
        isExist.invoke(true)
    }
}