package com.example.bledot.notupload

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bledot.App
import com.example.bledot.data.CSVData
import com.example.bledot.util.BleDebugLog
import java.io.File

class NotuploadViewModel : ViewModel() {

    private val logTag = NotuploadViewModel::class.simpleName
    private var path = ""
    var localFileList = ArrayList<CSVData>()

    init {
        BleDebugLog.i(logTag, "init-()")
    }

    // 미전송 데이터 존재 확인
    fun isExistData() {
        BleDebugLog.i(logTag, "isExistData-()")
        val dir: File? = App.context().getExternalFilesDir(null)
        path = dir?.absolutePath + File.separator

        BleDebugLog.d(logTag, "absolutePath: ${dir?.absolutePath}")
        BleDebugLog.d(logTag, "path: $path")

        localFileList.clear()

        val directory = File(path)
        val files = directory.listFiles()

        //val filesNameList = ArrayList<String>()

        files?.forEach { file ->
            BleDebugLog.d(logTag, "file.name: ${file.name}")
            localFileList.add(CSVData(file.name, false))
        }

        val dataNum = localFileList.size
        BleDebugLog.d(logTag, "dataNum: $dataNum")

        //unloaded.invoke(dataNum)
    }
}
