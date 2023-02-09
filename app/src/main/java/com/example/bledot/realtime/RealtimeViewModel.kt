package com.example.bledot.realtime

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bledot.App
import com.example.bledot.BuildConfig
import com.example.bledot.util.BleDebugLog
import com.example.bledot.util.KEY_LOGGER
import com.example.bledot.util.mLoggerList
import com.xsens.dot.android.sdk.events.XsensDotData
import com.xsens.dot.android.sdk.interfaces.XsensDotRecordingCallback
import com.xsens.dot.android.sdk.models.XsensDotDevice
import com.xsens.dot.android.sdk.models.XsensDotPayload
import com.xsens.dot.android.sdk.models.XsensDotRecordingFileInfo
import com.xsens.dot.android.sdk.models.XsensDotRecordingState
import com.xsens.dot.android.sdk.recording.XsensDotRecordingManager
import com.xsens.dot.android.sdk.utils.XsensDotLogger
import java.io.File
import java.security.AccessController.getContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

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
}