package com.example.bledot.withdrawal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bledot.App
import com.example.bledot.retrofit.RemoteDataSource
import com.example.bledot.util.BleDebugLog
import kotlinx.coroutines.launch


class WithdrawalViewModel : ViewModel() {

    private val logTag = WithdrawalViewModel::class.simpleName
    private val remoteDataSource = RemoteDataSource()

    init {
        BleDebugLog.i(logTag, "init-()")
    }

    fun deleteAccount(isDeleted: (Boolean) -> Unit) {
        BleDebugLog.i(logTag, "deleteAccount-()")
        // TODO :: 탈퇴 api
        viewModelScope.launch {
            remoteDataSource.withdrawAccount(
                App.prefs.getString("token", "no token"),
                App.prefs.getString("email", "no email")
            ) { result ->
                if (result) {
                    isDeleted.invoke(true)
                } else { // -1, null
                    isDeleted.invoke(false)
                }
            }
        }
    }
}
