package com.example.bledot.mydata

import androidx.lifecycle.ViewModel
import com.example.bledot.util.BleDebugLog

class MydataViewModel : ViewModel() {

    private val logTag = MydataViewModel::class.simpleName

    init {
        BleDebugLog.i(logTag, "init-()")
    }
}
