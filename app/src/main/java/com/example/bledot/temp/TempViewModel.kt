package com.example.bledot.temp

import androidx.lifecycle.ViewModel
import com.example.bledot.util.BleDebugLog

class TempViewModel : ViewModel() {

    private val logTag = TempViewModel::class.simpleName

    init {
        BleDebugLog.i(logTag, "init-()")
    }
}
