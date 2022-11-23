package com.example.bledot.realtime

import androidx.lifecycle.ViewModel
import com.example.bledot.util.BleDebugLog

class RealtimeViewModel: ViewModel() {

    private val logTag = RealtimeViewModel::class.simpleName

    init {
        BleDebugLog.i(logTag, "init-()")
    }

}