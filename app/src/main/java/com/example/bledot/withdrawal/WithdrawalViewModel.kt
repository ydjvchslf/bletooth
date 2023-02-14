package com.example.bledot.withdrawal

import androidx.lifecycle.ViewModel
import com.example.bledot.retrofit.RemoteDataSource
import com.example.bledot.util.BleDebugLog


class WithdrawalViewModel : ViewModel() {

    private val logTag = WithdrawalViewModel::class.simpleName

    init {
        BleDebugLog.i(logTag, "init-()")
    }
}
