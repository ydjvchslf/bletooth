package com.example.bledot.realtime

import android.os.Environment
import android.os.StatFs
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bledot.App
import com.example.bledot.BuildConfig
import com.example.bledot.retrofit.RemoteDataSource
import com.example.bledot.util.*
import com.xsens.dot.android.sdk.models.XsensDotDevice
import com.xsens.dot.android.sdk.models.XsensDotPayload
import com.xsens.dot.android.sdk.utils.XsensDotLogger
import kotlinx.coroutines.launch
import java.io.File
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow


class RealtimeViewModel: ViewModel() {

    private val logTag = RealtimeViewModel::class.simpleName
    private val remoteDataSource = RemoteDataSource()
    var isWearingOption: Boolean = false
    var isRecording = MutableLiveData(false)
    var filename = ""
    var fileFullName = ""
    var isUploading = MutableLiveData(false)

    init {
        BleDebugLog.i(logTag, "init-()")
        BleDebugLog.d(logTag, "isWearingOption: $isWearingOption")
        // Remove XsensDotLogger objects from list before start data logging.
        mLoggerList.value?.clear()
    }

    fun createFile(xsDevice: XsensDotDevice) {
        BleDebugLog.i(logTag, "createFile-()")
        // Remove XsensDotLogger objects from list before start data logging.
        mLoggerList.value?.clear()

        val appVersion = BuildConfig.VERSION_NAME
        val fwVersion = xsDevice.firmwareVersion
        val address = xsDevice.address
        val tag = xsDevice.tag.ifEmpty { xsDevice.name }

        // Store log file in app internal folder.
        // Don't need user to granted the storage permission.
        val dir: File = App.context().getExternalFilesDir(null)!!
        val filePath = dir.absolutePath + File.separator
        filename = tag + "_" +
                    SimpleDateFormat(
                    "yyyyMMdd_HHmmss_SSS",
                    Locale.getDefault()
                    ).format(Date()) +
                    ".csv"
        fileFullName = filePath + filename
        BleDebugLog.d(logTag, "파일 [$filename] 생성 완료")

        val logger = XsensDotLogger(
            App.context(),
            XsensDotLogger.TYPE_CSV,
            XsensDotPayload.PAYLOAD_TYPE_COMPLETE_EULER,
            fileFullName,
            tag,
            fwVersion,
            xsDevice.isSynced,
            xsDevice.currentOutputRate,
            xsDevice.filterProfileInfoList[0].name,
            appVersion
        )

        // Use mac address as a key to find logger object.
        val map = HashMap<String, Any>()
        map["KEY_ADDRESS"] = address
        map["KEY_LOGGER"] = logger
        val postLoggerList = ArrayList<HashMap<String, Any>>()
        postLoggerList.add(map)
        mLoggerList.value = postLoggerList
        BleDebugLog.d(logTag, "${mLoggerList.value}")
        BleDebugLog.d(logTag, "${mLoggerList.value?.size}")
    }

    /**
     * Close the data output stream.
     */
    fun closeFiles() {
        BleDebugLog.i(logTag, "closeFiles-()")
        mLoggerList.value?.let {
            for (map in it) {
                // Call stop() function to flush and close the output stream.
                // Data is kept in the stream buffer and write to file when the buffer is full.
                // Call this function to write data to file whether the buffer is full or not.
                val logger =
                    map[KEY_LOGGER] as XsensDotLogger
                logger.stop()
            }
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
                // userId, file 넣어서 Post 호출
                remoteDataSource.uploadToServer("abc@naver.com", it) { result ->
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