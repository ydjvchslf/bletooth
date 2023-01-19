package com.example.bledot.signup

import androidx.lifecycle.ViewModel
import com.example.bledot.util.BleDebugLog

class SignupViewModel: ViewModel() {

    private val logTag = SignupViewModel::class.simpleName

    init {
        BleDebugLog.i(logTag, "init-()")
    }

    fun checkUserInfo(email: String?, isExist: (Boolean) -> Unit) {
        BleDebugLog.i(logTag, "checkUserInfo-()")
        isExist.invoke(false)
    }
}