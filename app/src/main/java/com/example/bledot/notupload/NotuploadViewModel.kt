package com.example.bledot.notupload

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bledot.App
import com.example.bledot.data.CSVData
import com.example.bledot.retrofit.RemoteDataSource
import com.example.bledot.util.BleDebugLog
import com.example.bledot.util.appIsWorking
import kotlinx.coroutines.launch
import java.io.File

class NotuploadViewModel : ViewModel() {

    private val logTag = NotuploadViewModel::class.simpleName
    private val remoteDataSource = RemoteDataSource()
    private var path = ""
    var localFileList = ArrayList<CSVData>()
    var localFileListSize = MutableLiveData<Int>(null)
    var isUpdate = MutableLiveData(false)
    var isUploading = MutableLiveData(false)

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

        files?.forEach { file ->
            BleDebugLog.d(logTag, "file.name: ${file.name}")
            localFileList.add(CSVData(file.name, false))
        }

        val dataNum = localFileList.size
        BleDebugLog.d(logTag, "dataNum: $dataNum")
    }
    // 선택된 아이템이 뭔지 확인 작업
    fun checkSelectedData(originalList: ArrayList<CSVData>, cmd: Boolean, uploadCallback: (Boolean) -> Unit) {
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
            uploadLocalData(selectedList) {
                if (it) {
                    uploadCallback.invoke(true)
                } else {
                    uploadCallback.invoke(false)
                }
            }
        } else {
            deleteLocalData(selectedList)
        }
    }

    private fun uploadLocalData(selectedList: ArrayList<CSVData>, callBack: (Boolean) -> Unit) {
        BleDebugLog.i(logTag, "uploadLocalData-()")
        viewModelScope.launch {
            var isSuccess = false
            isUploading.value = true
            appIsWorking.value = true

            val directory = File(path)
            val files = directory.listFiles()

            val remainFileList = arrayListOf<File>()

            files?.forEach { file ->
                if (file.isFile) {
                    val fileName = file.name
                    if (selectedList.any { it.name == fileName }) {
                        // 업로드 성공
//                        BleDebugLog.d(logTag, "[${file.name}] 업로드 성공!")
//                        isUploading.value = false
//                        appIsWorking.value = false
//                        file.delete()
//                        isSuccess = true

                        // 업로드 실패
//                        BleDebugLog.d(logTag, "[${file.name}] 업로드 실패")
//                        isUploading.value = false
//                        appIsWorking.value = false
//                        isSuccess = false

                        remoteDataSource.uploadToServer(
                            App.prefs.getString("token", "no token"),
                            App.prefs.getString("email", "no email"),
                            file,
                            null
                        ) { result ->
                            if (result) {
                                BleDebugLog.d(logTag, "[${file.name}] 업로드 성공!")
                                isUploading.value = false
                                appIsWorking.value = false
                                // 업로드 성공 후 데이터 지우기
                                file.delete()
                                isSuccess = true
                            } else {
                                BleDebugLog.d(logTag, "[${file.name}] 업로드 실패")
                                remainFileList.add(file) // Add the file to the new list
                                isUploading.value = false
                                appIsWorking.value = false
                                isSuccess = false
                            }
                        }
                    } else {
                        remainFileList.add(file) // Add the file to the new list
                    }
                }
            }

            localFileList.clear()
            remainFileList.forEach { file ->
                localFileList.add(CSVData(file.name, false))
            }

            BleDebugLog.d(logTag, "업로드 후, 삭제 후 localFileList: $localFileList")
            localFileListSize.value = localFileList.size
            isExistData()
            isUpdate.value = true

            callBack.invoke(isSuccess)
        }
    }

    private fun deleteLocalData(selectedList: ArrayList<CSVData>) {
        BleDebugLog.i(logTag, "deleteLocalData-()")

        val directory = File(path)
        val files = directory.listFiles()

        val remainFileList = arrayListOf<File>()

        files?.forEach { file ->
            if (file.isFile) {
                val fileName = file.name
                if (selectedList.any { it.name == fileName }) {
                    val deleted = file.delete()
                    if (deleted) {
                        BleDebugLog.d(logTag, "$fileName deleted successfully")
                    } else {
                        remainFileList.add(file) // Add the file to the new list
                        BleDebugLog.d(logTag, "Failed to delete $fileName")
                    }
                } else {
                    remainFileList.add(file) // Add the file to the new list
                }
            }
        }

        localFileList.clear()
        remainFileList.forEach { file ->
            localFileList.add(CSVData(file.name, false))
        }

        BleDebugLog.d(logTag, "삭제 후 localFileList: $localFileList, size: ${localFileList.size}")
        localFileListSize.value = localFileList.size
        isExistData()
        isUpdate.value = true
    }
}
