package com.example.bledot.changepw

import android.app.appsearch.BatchResultCallback
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bledot.data.UserInfoEntity
import com.example.bledot.retrofit.RemoteDataSource
import com.example.bledot.util.BleDebugLog
import kotlinx.coroutines.launch


class ChangePwViewModel : ViewModel() {

    private val logTag = ChangePwViewModel::class.simpleName

    init {
        BleDebugLog.i(logTag, "init-()")
    }

    fun isCheckedPw(pw: Int, resultCallback: (Boolean) -> Unit) {
        BleDebugLog.i(logTag, "isCheckedPw-()")
        // TODO :: 현재 비밀번호 유효 체크 api
        if (pw == 1111) {
            resultCallback.invoke(true)
        } else {
            resultCallback.invoke(false)
        }
    }

    fun changePw(email: String, pw: String, result: (Boolean) -> Unit) {
        BleDebugLog.i(logTag, "changePw-()")
        // TODO :: 비밀번호 수정 api
        result.invoke(true)
    }
}
