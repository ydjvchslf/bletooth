package com.example.bledot.notupload

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
        BleDebugLog.d(logTag, "dataNum: ${localFileList.size}")
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
    // 선택된 아이템이 뭔지 확인 작업
    fun checkSelectedData(originalList: ArrayList<CSVData>, cmd: Boolean) {
        BleDebugLog.i(logTag, "checkSelectedData-()")

        val specificValue = true
        val selectedList = ArrayList<CSVData>()

        for (i in originalList) {
            if (i.isChecked == specificValue) {
                selectedList.add(i)
            }
        }

        BleDebugLog.d(logTag, "selectedList: $selectedList")
        if (selectedList.size == 0) { return }

        if (cmd) {
            uploadLocalData(selectedList)
        } else {
            deleteLocalData(selectedList)
        }
    }

    private fun uploadLocalData(selectedList: ArrayList<CSVData>) {
        BleDebugLog.i(logTag, "uploadLocalData-()")

    }

    private fun deleteLocalData(selectedList: ArrayList<CSVData>) {
        BleDebugLog.i(logTag, "deleteLocalData-()")

    }
}
