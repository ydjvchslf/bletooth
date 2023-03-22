package com.example.bledot.editinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bledot.App
import com.example.bledot.data.UserInfoEntity
import com.example.bledot.retrofit.RemoteDataSource
import com.example.bledot.util.BleDebugLog
import kotlinx.coroutines.launch


class EditInfoViewModel : ViewModel() {

    private val logTag = EditInfoViewModel::class.simpleName
    private val remoteDataSource = RemoteDataSource()

    init {
        BleDebugLog.i(logTag, "init-()")
    }

    fun getUserFromServer(result: (UserInfoEntity?) -> Unit) {
        BleDebugLog.i(logTag, "getUserFromServer-()")
        viewModelScope.launch {
            // TODO:: 유저 정보 조회 api
            val token = App.prefs.getString("token", "no token")
            val email = App.prefs.getString("email", "no email")
            if (token != "no token" && email != "no email") {
                remoteDataSource.getUserInfo(token, email) { userEntity ->
                    userEntity?.let {
                        result.invoke(it)
                    }
                }
            }
        }
    }

    fun editUserInfo(email:String, userInfo: UserInfoEntity, result: (Boolean) -> Unit) {
        BleDebugLog.i(logTag, "editUserInfo-()")
        // TODO :: 회원정보 수정 api
        result.invoke(true)
    }
}
