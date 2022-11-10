package com.example.bledot.ble

import androidx.lifecycle.ViewModel
import com.example.bledot.util.BleDebugLog

class BleViewModel : ViewModel() {

    private val logTag = BleViewModel::class.simpleName

    init {
        BleDebugLog.i(logTag, "init-()")
    }
}