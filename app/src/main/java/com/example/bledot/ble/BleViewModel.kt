package com.example.bledot.ble

import androidx.lifecycle.ViewModel
import com.example.bledot.data.BleDevice
import com.example.bledot.util.BleDebugLog

class BleViewModel : ViewModel() {

    private val logTag = BleViewModel::class.simpleName
    private var deviceList = ArrayList<BleDevice>()

    init {
        BleDebugLog.i(logTag, "init-()")
    }
}