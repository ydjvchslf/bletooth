package com.example.bledot.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bledot.data.UserInfoEntity
import com.example.bledot.retrofit.RemoteDataSource
import com.example.bledot.util.BleDebugLog
import kotlinx.coroutines.launch


class ListViewModel : ViewModel() {

    private val logTag = ListViewModel::class.simpleName

    init {
        BleDebugLog.i(logTag, "init-()")
    }
}