package com.example.bledot.changepw

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
}
