package com.example.bledot.editinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bledot.data.UserInfoEntity
import com.example.bledot.retrofit.RemoteDataSource
import com.example.bledot.util.BleDebugLog
import kotlinx.coroutines.launch


class EditInfoViewModel : ViewModel() {

    private val logTag = EditInfoViewModel::class.simpleName

    init {
        BleDebugLog.i(logTag, "init-()")
    }

    fun editUserInfo(email:String, userInfo: UserInfoEntity, result: (Boolean) -> Unit) {
        BleDebugLog.i(logTag, "editUserInfo-()")
        // TODO :: 회원정보 수정 api
        result.invoke(true)
    }
}
