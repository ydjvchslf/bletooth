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
        // TODO:: db에 존재하는 email 인지 확인 api
        isExist.invoke(true)
    }

    fun requestResetPw(email: String, retCode: (Int?) -> Unit) {
        BleDebugLog.i(logTag, "requestResetPw-()")
        // TODO:: 이메일 찾기 api
        retCode.invoke(200)
    }
}