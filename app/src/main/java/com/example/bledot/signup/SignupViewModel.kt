package com.example.bledot.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bledot.retrofit.RemoteDataSource
import com.example.bledot.util.BleDebugLog
import kotlinx.coroutines.launch

class SignupViewModel: ViewModel() {

    private val logTag = SignupViewModel::class.simpleName
    private val remoteDataSource = RemoteDataSource()

    init {
        BleDebugLog.i(logTag, "init-()")
    }

    fun checkUserInfo(email: String?, isExist: (Boolean) -> Unit) {
        BleDebugLog.i(logTag, "checkUserInfo-()")
        isExist.invoke(false)
    }

    fun checkEmail(email: String, isDuplicate: (Boolean) -> Unit) {
        BleDebugLog.i(logTag, "checkEmail-()")
        viewModelScope.launch {
            remoteDataSource.checkEmailDuplication(email) {
                BleDebugLog.i(logTag, "retCode: $it")
                if (it == 200) { // 가능한 email
                    isDuplicate.invoke(false)
                }
                else if (it == -1) { // 중복된 email
                    isDuplicate.invoke(true)
                }
            }
        }
    }
}