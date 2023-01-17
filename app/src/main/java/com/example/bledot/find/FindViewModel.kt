package com.example.bledot.find

import androidx.lifecycle.ViewModel
import com.example.bledot.data.UserInfoEntity
import com.example.bledot.util.BleDebugLog

class FindViewModel: ViewModel() {

    private val logTag = FindViewModel::class.simpleName

    init {
        BleDebugLog.i(logTag, "init-()")
    }

    fun isValidEmail(email: String, isExist: (Boolean) -> Unit) {
        BleDebugLog.i(logTag, "isValidEmail-()")
        isExist.invoke(true)
    }
}