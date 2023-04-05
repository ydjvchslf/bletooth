package com.example.bledot.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bledot.App
import com.example.bledot.data.UserInfoEntity
import com.example.bledot.retrofit.RemoteDataSource
import com.example.bledot.util.BleDebugLog
import com.example.bledot.util.appIsWorking
import kotlinx.coroutines.launch
import java.io.File


class HomeViewModel : ViewModel() {

    private val logTag = HomeViewModel::class.simpleName
    private val remoteDataSource = RemoteDataSource()
    private var path = ""
    var isUploading = MutableLiveData(false)

    init {
        BleDebugLog.i(logTag, "init-()")
        getMyInfo()
    }

    private fun getMyInfo() {
        BleDebugLog.i(logTag, "getMyInfo-()")
        viewModelScope.launch  {

        }
    }


    // 미전송 데이터 존재 확인
    fun isExistData(unloaded: (Int) -> Unit) {
        BleDebugLog.i(logTag, "isExistData-()")
        val dir: File? = App.context().getExternalFilesDir(null)
        path = dir?.absolutePath + File.separator

        BleDebugLog.d(logTag, "absolutePath: ${dir?.absolutePath}")
        BleDebugLog.d(logTag, "path: $path")

        val directory = File(path)
        val files = directory.listFiles()

        val filesNameList = ArrayList<String>()

        files?.forEach { file ->
            BleDebugLog.d(logTag, "file.name: ${file.name}")
            filesNameList.add(file.name)
        }

        val dataNum = filesNameList.size
        BleDebugLog.d(logTag, "dataNum: $dataNum")

        unloaded.invoke(dataNum)
    }

    fun deleteAllData(result: (Boolean) -> Unit) {
        BleDebugLog.i(logTag, "deleteAllData-()")

        val directory = File(path)
        val files = directory.listFiles()

        files?.forEach {
            it.delete()
        }

        result.invoke(true)
    }

    fun uploadData(callBack: (Boolean) -> Unit) {
        BleDebugLog.i(logTag, "uploadData-()")
        viewModelScope.launch {
            appIsWorking.value = true
            isUploading.value = true

            val directory = File(path)
            val files = directory.listFiles()
            var isSuccess = false

            files?.forEach {
                // TODO :: 서버 업로드 api
                // userId, file 넣어서 Post 호출
                remoteDataSource.uploadToServer(
                    App.prefs.getString("token", "no token"),
                    App.prefs.getString("email", "no email"),
                    it,
                    null
                ) { result ->
                    if (result) {
                        BleDebugLog.d(logTag, "[${it.name}] 업로드 성공!")
                        // 업로드 성공 후 데이터 지우기
                        it.delete()
                        isSuccess = true
                    } else {
                        BleDebugLog.d(logTag, "[${it.name}] 업로드 실패")
                        isSuccess = false
                    }
                }
            }
            callBack.invoke(isSuccess)
            isUploading.value = false
            appIsWorking.value = false
        }
    }
}
