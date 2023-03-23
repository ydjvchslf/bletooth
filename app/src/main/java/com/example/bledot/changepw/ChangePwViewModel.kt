package com.example.bledot.changepw

import android.app.appsearch.BatchResultCallback
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bledot.App
import com.example.bledot.data.UserInfoEntity
import com.example.bledot.retrofit.RemoteDataSource
import com.example.bledot.util.BleDebugLog
import kotlinx.coroutines.launch


class ChangePwViewModel : ViewModel() {

    private val logTag = ChangePwViewModel::class.simpleName
    private val remoteDataSource = RemoteDataSource()

    init {
        BleDebugLog.i(logTag, "init-()")
    }

    // 미사용
    fun isCheckedPw(pw: Int, resultCallback: (Boolean) -> Unit) {
        BleDebugLog.i(logTag, "isCheckedPw-()")
        // TODO :: 현재 비밀번호 유효 체크 api
        if (pw == 1111) {
            resultCallback.invoke(true)
        } else {
            resultCallback.invoke(false)
        }
    }

    fun changePassword(crtPw: String, newPw: String, isChanged: (Boolean, String?) -> Unit) {
        BleDebugLog.i(logTag, "changePw-()")
        // TODO :: 비밀번호 수정 api
        viewModelScope.launch {
            remoteDataSource.updatePassword(
                App.prefs.getString("token", "no token"),
                App.prefs.getString("email", "no email"),
                crtPw,
                newPw) { isUpdated, msg ->
                if (isUpdated) {
                    isChanged.invoke(true, msg)
                } else {
                    isChanged.invoke(false, msg)
                }
            }
        }
    }
}
