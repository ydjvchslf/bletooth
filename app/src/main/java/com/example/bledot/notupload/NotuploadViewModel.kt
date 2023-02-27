package com.example.bledot.notupload

import androidx.lifecycle.ViewModel
import com.example.bledot.util.BleDebugLog

class NotuploadViewModel : ViewModel() {

    private val logTag = NotuploadViewModel::class.simpleName

    init {
        BleDebugLog.i(logTag, "init-()")
    }
}
