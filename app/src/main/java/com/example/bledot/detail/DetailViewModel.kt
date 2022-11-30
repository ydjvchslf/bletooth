package com.example.bledot.detail

import androidx.lifecycle.ViewModel
import com.example.bledot.ble.BleViewModel
import com.example.bledot.util.BleDebugLog

class DetailViewModel : ViewModel() {

    private val logTag = DetailViewModel::class.simpleName

    init {
        BleDebugLog.i(logTag, "init-()")
    }

}