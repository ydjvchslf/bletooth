package com.example.bledot.realtime

import android.annotation.SuppressLint
import android.os.Environment
import android.os.StatFs
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bledot.App
import com.example.bledot.BuildConfig
import com.example.bledot.data.WebViewData
import com.example.bledot.data.XYZData
import com.example.bledot.retrofit.RemoteDataSource
import com.example.bledot.util.*
import com.xsens.dot.android.sdk.models.XsensDotDevice
import com.xsens.dot.android.sdk.models.XsensDotPayload
import com.xsens.dot.android.sdk.utils.XsensDotLogger
import kotlinx.coroutines.launch
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow


class RealtimeViewModel: ViewModel() {

    private val logTag = RealtimeViewModel::class.simpleName
    private val remoteDataSource = RemoteDataSource()
    //var isWearingOption = false
    var isRecording = MutableLiveData(false)
    var filename = ""
    var fileFullName = ""
    var isUploading = MutableLiveData(false)
    // MWM arg
    var webViewData: WebViewData? = null

    init {
        BleDebugLog.i(logTag, "init-()")
        //BleDebugLog.d(logTag, "isWearingOption: $isWearingOption")
        // Remove XsensDotLogger objects from list before start data logging.
        mLoggerList.value?.clear()
    }

    fun createFile() {
        BleDebugLog.i(logTag, "createFile-()")
        val dir: File? = App.context().getExternalFilesDir(null)
        val filePath = dir?.absolutePath + File.separator

        filename = if (webViewData == null) {
            SimpleDateFormat(
                "yyyyMMdd_HHmmss_SSS",
                Locale.getDefault()
            ).format(Date()) +
                    ".csv"
        } else {
            SimpleDateFormat(
                "yyyyMMdd_HHmmss_SSS",
                Locale.getDefault()
            ).format(Date()) + "_${webViewData?.meaId}-${webViewData?.daId}-${webViewData?.spId}.csv"
        }

        fileFullName = filePath + filename
        File(fileFullName).createNewFile()
        BleDebugLog.d(logTag, "파일 [$filename] 생성 완료")

        csvFirst()
    }

    private fun csvFirst() {
        BleDebugLog.i(logTag, "csvFirst-()")

        val dir: File? = App.context().getExternalFilesDir(null)
        val filePath = dir?.absolutePath + File.separator

        val file = File(filePath, filename)
        val bw = BufferedWriter(FileWriter(file))

        try {
            bw.write("time,packetNumber,Roll,Pitch,Yaw\n")
            bw.flush()
            bw.close()
        }
        catch (e: Exception) {
            BleDebugLog.e("${e.message}")
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun updateFiles(afterXYZData: XYZData, index: Int) {
        BleDebugLog.i(logTag, "updateFiles-()")

        val parTime = SimpleDateFormat("yyyyMMddHH:mm:ss.sss").format(Calendar.getInstance().time)

        val dir: File? = App.context().getExternalFilesDir(null)
        val filePath = dir?.absolutePath + File.separator

        val file = File(filePath, filename)
        val bw = BufferedWriter(FileWriter(file, true))

        try {
            bw.write("$parTime,$index,${afterXYZData.xValue},${afterXYZData.yValue},${afterXYZData.zValue}\n")
            bw.flush()
            bw.close()
        }
        catch (e: Exception) {
            BleDebugLog.e("${e.message}")
        }
    }

    fun getExternalMemory(limitCallback: (Boolean?) -> Unit) {
        BleDebugLog.i(logTag, "getExternalMemory-()")
        val externalMemory = checkExternalStorageAllMemory()
        val externalAvailableMemory = checkExternalAvailableMemory()

        externalMemory?.let {
            BleDebugLog.d(logTag, "allMemory: ${getFileSize(it)}")
        }

        externalAvailableMemory?.let {
            BleDebugLog.d(logTag, "available: ${getFileSize(it)}")
        }

        if (externalMemory != null && externalAvailableMemory != null) {
            val limit = externalMemory.times(LIMIT_PERCENTAGE)
            if (externalAvailableMemory < limit ) {
                limitCallback.invoke(true)
            }
        }
    }

    private fun isExternalMemoryAvailable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    private fun checkExternalStorageAllMemory(): Long? {
        return if (isExternalMemoryAvailable()) {
            val stat = StatFs(Environment.getExternalStorageDirectory().path)
            val blockSize = stat.blockSizeLong
            val totalBlocks = stat.blockCountLong
            totalBlocks * blockSize
        } else {
            null
        }
    }

    private fun checkExternalAvailableMemory(): Long? {
        return if (isExternalMemoryAvailable()) {
            val file = Environment.getExternalStorageDirectory()
            val stat = StatFs(file.path)
            val blockSize = stat.blockSizeLong
            val availableBlocks = stat.availableBlocksLong
            availableBlocks * blockSize
        } else {
            null
        }
    }

    private fun getFileSize(size: Long): String {
        if (size <= 0) return "0"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
        return DecimalFormat("#,##0.#").format(size / 1024.0.pow(digitGroups.toDouble())) + " " + units[digitGroups]
    }

    fun uploadToServer(callBack: (Boolean) -> Unit) {
        BleDebugLog.i(logTag, "uploadData-()")
        viewModelScope.launch {
            val directory = File(fileFullName)
            var isSuccess = false
            isUploading.value = true
            appIsWorking.value = true

            directory.let {
                // TODO :: 서버 업로드 api
                // userId, file 넣어서 Post 호출
                remoteDataSource.uploadToServer(
                    App.prefs.getString("token", "no token"),
                    App.prefs.getString("email", "no email"),
                    it,
                    webViewData) { result ->
                    if (result) {
                        BleDebugLog.d(logTag, "[${it.name}] 업로드 성공!")
                        isUploading.value = false
                        appIsWorking.value = false
                        // 업로드 성공 후 데이터 지우기
                        it.delete()
                        isSuccess = true
                    } else {
                        BleDebugLog.d(logTag, "[${it.name}] 업로드 실패")
                        isUploading.value = false
                        appIsWorking.value = false
                        isSuccess = false
                    }
                }
            }
            callBack.invoke(isSuccess)
        }
    }

    fun deleteData() {
        BleDebugLog.i(logTag, "deleteData-()")
        viewModelScope.launch {
            val directory = File(fileFullName)
            directory.delete()
            checkDataNum()
        }
    }

    private fun checkDataNum() {
        BleDebugLog.i(logTag, "checkDataNum-()")

        val dir: File? = App.context().getExternalFilesDir(null)
        val filePath = dir?.absolutePath + File.separator

        val directory = File(filePath)
        val files = directory.listFiles()

        val filesNameList = ArrayList<String>()

        files?.forEach { file ->
            BleDebugLog.d(logTag, "file.name: ${file.name}")
            filesNameList.add(file.name)
        }

        val dataNum = filesNameList.size
        BleDebugLog.d(logTag, "dataNum: $dataNum")
    }

}