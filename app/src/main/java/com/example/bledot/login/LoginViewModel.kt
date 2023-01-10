package com.example.bledot.login

import androidx.lifecycle.ViewModel
import com.example.bledot.util.BleDebugLog

class LoginViewModel: ViewModel() {

    private val logTag = LoginViewModel::class.simpleName

    init {
        BleDebugLog.i(logTag, "init-()")
    }
}