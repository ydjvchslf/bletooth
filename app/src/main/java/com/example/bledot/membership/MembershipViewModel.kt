package com.example.bledot.membership

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bledot.util.BleDebugLog

class MembershipViewModel : ViewModel() {

    private val logTag = MembershipViewModel::class.simpleName
    var membershipDate = MutableLiveData("")
    var isValid = MutableLiveData<Boolean?>()

    init {
        BleDebugLog.i(logTag, "init-()")
        //checkMembershipDate()
    }

    fun checkMembershipDate() {
        BleDebugLog.i(logTag, "checkMembershipDate-()")
        BleDebugLog.d(logTag, "membershipDate: ${membershipDate.value}")

        // TODO:: 멤버십 만료 여부 체크 작업
        isValid.value = true

        BleDebugLog.d(logTag, "isValid: ${isValid.value}")
    }
}
