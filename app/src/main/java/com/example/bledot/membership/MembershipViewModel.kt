package com.example.bledot.membership

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bledot.util.BleDebugLog

class MembershipViewModel : ViewModel() {

    private val logTag = MembershipViewModel::class.simpleName
    var membershipDate: String? = null

    init {
        BleDebugLog.i(logTag, "init-()")
    }
}
