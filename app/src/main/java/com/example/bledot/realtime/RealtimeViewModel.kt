package com.example.bledot.realtime

import android.annotation.SuppressLint
import android.util.Log
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
    private var mManager: XsensDotRecordingManager? = null
    private var internalList: ArrayList<XsensDotRecordingFileInfo>? = null
    private var byteIds: ByteArray? = null
    private var xsLogger: XsensDotLogger? = null
    // A list contains tag and data from each sensor
    private var mDataList: ArrayList<HashMap<String?, Any>>? = null
    // A list contains mac address and XsensDotLogger object -> 전역변수로 이동
    //private var mLoggerList: ArrayList<HashMap<String, Any>>? = null
    var isWearingOption: Boolean = false

    init {
        BleDebugLog.i(logTag, "init-()")
        BleDebugLog.d(logTag, "isWearingOption: $isWearingOption")
        // Remove XsensDotLogger objects from list before start data logging.
        mLoggerList?.value?.clear()
    }

    fun createFile(xsDevice: XsensDotDevice) {
        BleDebugLog.i(logTag, "createFile-()")
        // Remove XsensDotLogger objects from list before start data logging.
        mLoggerList.value?.clear()

        val appVersion = BuildConfig.VERSION_NAME
        val fwVersion = xsDevice.firmwareVersion
        val address = xsDevice.address
        val tag = xsDevice.tag.ifEmpty { xsDevice.name }
        var filename = ""

        // Store log file in app internal folder.
        // Don't need user to granted the storage permission.
        val dir: File = App.context().getExternalFilesDir(null)!!
        filename = dir.absolutePath +
                    File.separator +
                    tag + "_" +
                    SimpleDateFormat(
                    "yyyyMMdd_HHmmss_SSS",
                    Locale.getDefault()
                    ).format(Date()) +
                    ".csv"
        BleDebugLog.d(logTag, "파일 [$filename] 생성 완료")

        val logger = XsensDotLogger(
            App.context(),
            XsensDotLogger.TYPE_CSV,
            XsensDotPayload.PAYLOAD_TYPE_COMPLETE_EULER,
            filename,
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