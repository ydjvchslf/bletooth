package com.example.bledot.config

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bledot.App
import com.example.bledot.BuildConfig
import com.example.bledot.retrofit.RemoteDataSource
import com.example.bledot.util.BleDebugLog
import com.example.bledot.util.isGoogleUser
import kotlinx.coroutines.launch

class ConfigViewModel : ViewModel() {

    private val logTag = ConfigViewModel::class.simpleName
    private val remoteDataSource = RemoteDataSource()
    var versionNum = MutableLiveData("")

    init {
        BleDebugLog.i(logTag, "init-()")
        getVersionNum()
    }

    private fun getVersionNum() {
        versionNum.value = BuildConfig.VERSION_NAME
    }

   fun getProductList(serverRetCode: (Int) -> Unit) {
        BleDebugLog.i(logTag, "getProductList-()")
        viewModelScope.launch  {
            remoteDataSource.getAllProducts { retCode ->
                if (retCode == 200) {
                    serverRetCode.invoke(200)
                }
            }
        }
    }

    fun isShowPwChange(): Boolean {
        BleDebugLog.i(logTag, "isShowPwChange-()")
        return !isGoogleUser(App.prefs.getString("email", "no email"))
    }
}
