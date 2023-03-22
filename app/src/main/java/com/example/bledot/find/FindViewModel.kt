package com.example.bledot.find

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bledot.App
import com.example.bledot.data.UserInfoEntity
import com.example.bledot.retrofit.RemoteDataSource
import com.example.bledot.util.BleDebugLog
import kotlinx.coroutines.launch

class FindViewModel: ViewModel() {

    private val logTag = FindViewModel::class.simpleName
    private val remoteDataSource = RemoteDataSource()

    init {
        BleDebugLog.i(logTag, "init-()")
    }

    fun requestResetPw(email: String, isSent: (Boolean) -> Unit) {
        BleDebugLog.i(logTag, "requestResetPw-()")
        // TODO:: password 찾기 api
        viewModelScope.launch {
            remoteDataSource.findPassword(email) { result ->
                if (result) {
                    isSent.invoke(true)
                } else { // -1, null
                    isSent.invoke(false)
                }
            }
        }
    }
}