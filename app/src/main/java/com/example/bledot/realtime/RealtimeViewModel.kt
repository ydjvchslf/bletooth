package com.example.bledot.realtime

import android.os.Environment
import android.os.StatFs
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bledot.App
import com.example.bledot.BuildConfig
import com.example.bledot.util.BleDebugLog
import com.example.bledot.util.KEY_LOGGER
import com.example.bledot.util.mLoggerList
import com.xsens.dot.android.sdk.models.XsensDotDevice
import com.xsens.dot.android.sdk.models.XsensDotPayload
import com.xsens.dot.android.sdk.utils.XsensDotLogger
import java.io.File
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow


class RealtimeViewModel: ViewModel() {

    private val logTag = RealtimeViewModel::class.simpleName
    var isWearingOption: Boolean = false
    var isRecording = MutableLiveData(false)
    var filename = ""
    var fileFullName = ""

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
        for (map in mLoggerList.value!!) {
            // Call stop() function to flush and close the output stream.
            // Data is kept in the stream buffer and write to file when the buffer is full.
            // Call this function to write data to file whether the buffer is full or not.
            val logger =
                map[KEY_LOGGER] as XsensDotLogger
            logger.stop()
        }
    }

    fun getExternalMemory() {
        BleDebugLog.i(logTag, "getExternalMemory-()")
        val externalMemory = checkExternalStorageAllMemory()
        val externalAvailableMemory = checkExternalAvailableMemory()

        externalMemory?.let {
            BleDebugLog.d(logTag, "allMemory: ${getFileSize(it)}")
        }

        externalAvailableMemory?.let {
            BleDebugLog.d(logTag, "available: ${getFileSize(it)}")
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

}