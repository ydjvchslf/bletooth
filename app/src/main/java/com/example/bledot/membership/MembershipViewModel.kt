package com.example.bledot.membership

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bledot.App
import com.example.bledot.data.MbsEntity
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

    fun checkMembership(mbsCallback: (MbsEntity?) -> Unit) { // 멤버십 state, 시작날짜, 만료날짜
        BleDebugLog.i(logTag, "checkMembership-()")
        viewModelScope.launch {
            remoteDataSource.getMembership(
                App.prefs.getString("token", "no token"),
                App.prefs.getString("email", "no email")
            ) { it?.let(mbsCallback) }
        }
    }

    fun registerMembership(inputMemNum: String, result: (Boolean) -> Unit) {
        BleDebugLog.i(logTag, "registerMembership-()")
        viewModelScope.launch {
            // TODO :: 멤버십 등록 api
            remoteDataSource.enrollMembership(
                App.prefs.getString("token", "no token"),
                App.prefs.getString("email", "no email"),
                inputMemNum
            ) { result.invoke(it) }
        }
    }

    fun getUserInfo(email: String) {
        BleDebugLog.i(logTag, "getUserInfo-()")
        viewModelScope.launch {

        }
    }
}
