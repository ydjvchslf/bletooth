package com.example.bledot.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bledot.data.UserInfoEntity
import com.example.bledot.retrofit.RemoteDataSource
import com.example.bledot.util.BleDebugLog
import kotlinx.coroutines.launch


class HomeViewModel : ViewModel() {

    private val logTag = HomeViewModel::class.simpleName
    private val remoteDataSource = RemoteDataSource()
    lateinit var crnUserInfo: UserInfoEntity

    init {
        BleDebugLog.i(logTag, "init-()")
        getMyInfo()
    }

    private fun getMyInfo() {
        BleDebugLog.i(logTag, "getMyInfo-()")
        viewModelScope.launch  {
            remoteDataSource.getUserInfo {
                crnUserInfo = it
            }
        }
    }
}
