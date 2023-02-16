package com.example.bledot.membership

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bledot.retrofit.RemoteDataSource
import com.example.bledot.util.BleDebugLog
import kotlinx.coroutines.launch

class MembershipViewModel : ViewModel() {

    private val logTag = MembershipViewModel::class.simpleName
    private val remoteDataSource = RemoteDataSource()
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

    fun registerMembership(inputMemNum: String, result: (Boolean) -> Unit) {
        BleDebugLog.i(logTag, "registerMembership-()")
        viewModelScope.launch {
            remoteDataSource.enrollMembership(inputMemNum) {
                if (it) {
                    result.invoke(true)
                } else {
                    result.invoke(false)
                }
            }
        }
    }

    fun getUserInfo(email: String) {
        BleDebugLog.i(logTag, "getUserInfo-()")
        viewModelScope.launch {
            remoteDataSource.getUserInfo(email) { userInfoEntity ->
                //membershipDate.value = userInfoEntity.membership
                membershipDate.value = "2022/02/16 ~ 2023/02/15"
                isValid.value = true
            }
        }
    }
}
