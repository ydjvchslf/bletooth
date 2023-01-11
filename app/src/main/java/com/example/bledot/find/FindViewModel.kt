package com.example.bledot.find

import androidx.lifecycle.ViewModel
import com.example.bledot.util.BleDebugLog

class FindViewModel: ViewModel() {

    private val logTag = FindViewModel::class.simpleName

    init {
        BleDebugLog.i(logTag, "init-()")
    }
}