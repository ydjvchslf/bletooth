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

class RealtimeViewModel: ViewModel(), XsensDotRecordingCallback {

    private val logTag = RealtimeViewModel::class.simpleName
    private var mManager: XsensDotRecordingManager? = null
    private var internalList: ArrayList<XsensDotRecordingFileInfo>? = null
    private var byteIds: ByteArray? = null
    private var xsLogger: XsensDotLogger? = null
    // A list contains tag and data from each sensor
    private var mDataList: ArrayList<HashMap<String?, Any>>? = null
    // A list contains mac address and XsensDotLogger object -> 전역변수로 이동
    //private var mLoggerList: ArrayList<HashMap<String, Any>>? = null

    init {
        BleDebugLog.i(logTag, "init-()")
        // Remove XsensDotLogger objects from list before start data logging.
        mLoggerList?.value?.clear()
    }

    fun initRecord(xsDevice: XsensDotDevice) {
        BleDebugLog.i(logTag, "initRecord-()")
        mManager = XsensDotRecordingManager(App.context(), xsDevice, this)
        mManager?.enableDataRecordingNotification()
    }

    override fun onXsensDotRecordingNotification(address: String?, isEnabled: Boolean) {
        BleDebugLog.i(logTag, "onXsensDotRecordingNotification-()")
        BleDebugLog.d(logTag, "isEnabled: $isEnabled")
        if (isEnabled) { mManager?.requestFlashInfo() }
    }

    //storage space is insufficient, clear flash storage
    override fun onXsensDotEraseDone(address: String?, isSuccess: Boolean) {
        BleDebugLog.i(logTag, "onXsensDotEraseDone-()")
        // 내부저장소 clear 완료 후, 뒤늦게 불림
        // UI 변경 및 알림 줄거면 isSuccess 받아서 할 것
    }

    override fun onXsensDotRequestFlashInfoDone(address: String?, usedFlashSpace: Int, totalFlashSpace: Int) {
        BleDebugLog.i(logTag, "onXsensDotRequestFlashInfoDone-()")
        BleDebugLog.d(logTag, "usedFlashSpace: $usedFlashSpace, totalFlashSpace: $totalFlashSpace")
        // get usedFlashSpace & totalFlashSpace, if the available flash space <= 10%, it cannot start recording
    }

    override fun onXsensDotRecordingAck(
        address: String?,
        recordingId: Int,
        isSuccess: Boolean,
        recordingState: XsensDotRecordingState?
    ) {
        BleDebugLog.i(logTag, "onXsensDotRecordingAck-()")
        if (recordingId == XsensDotRecordingManager.RECORDING_ID_START_RECORDING) {
        // start recording result, check recordingState, it should be success or fail.
            BleDebugLog.d(logTag, "isRecordingStart: $isSuccess")
            mManager?.requestRecordingState()
        } else if (recordingId ==
            XsensDotRecordingManager.RECORDING_ID_STOP_RECORDING) {
            // stop recording result, check recordingState, it should be success or fail.
            BleDebugLog.d(logTag, "isRecordingStop: $isSuccess")
            terminateRecord()
        }
        // requestRecordingState()
        if (recordingId == XsensDotRecordingManager.RECORDING_ID_GET_STATE) {
            if (recordingState == XsensDotRecordingState.onErasing // 48
                || recordingState == XsensDotRecordingState.onExportFlashInfo // 80
                || recordingState == XsensDotRecordingState.onRecording // 64
                || recordingState == XsensDotRecordingState.onExportRecordingFileInfo // 96
                || recordingState == XsensDotRecordingState.onExportRecordingFileData // 112
            ) {
                BleDebugLog.d(logTag, "recordingState: $recordingState") // onRecording
                mManager?.requestRecordingTime()
            }
        }
    }
    // mManager.requestRecordingTime()
    override fun onXsensDotGetRecordingTime(
        address: String?,
        startUTCSeconds: Int,
        totalRecordingSeconds: Int,
        remainingRecordingSeconds: Int
    ) {
        BleDebugLog.i(logTag, "onXsensDotGetRecordingTime-()")
    }

    override fun onXsensDotRequestFileInfoDone(
        address: String?,
        list: ArrayList<XsensDotRecordingFileInfo>?,
        isSuccess: Boolean
    ) {
        BleDebugLog.i(logTag, "onXsensDotRequestFileInfoDone-()")
        // A list of file information can be obtained, one message contains: fileId, fileName, dataSize
        BleDebugLog.d(logTag, "fileList: ${list?.size}")
        list?.forEachIndexed { index, fileInfo ->
            BleDebugLog.d(logTag, "index: [$index]번째, fileId: ${fileInfo.fileId}, fileName: ${fileInfo.fileName}, dataSize: ${fileInfo.dataSize}")
            // internal dataList 할당
            internalList = list
            // Set export data format
            val ids = setExportData()
            mManager?.selectExportedData(ids)
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onXsensDotDataExported(
        address: String?,
        XsensDotRecordingFileInfo: XsensDotRecordingFileInfo?,
        XsensDotData: XsensDotData?
    ) {
        BleDebugLog.i(logTag, "onXsensDotDataExported-()111111")

        // Logger 경로
        val xsLogger =
            XsensDotLogger(App.context(), 1, 2, "fileName", "TAG", "version0", true, 1, "profileName","version1.0")

//        if (xsLogger == null) {
//            xsLogger = XsensDotLogger.createRecordingsLogger(
//                App.context(),
//                byteIds,
//                XsensDotRecordingFileInfo?.fileName,
//                "MIA TAG",
//                "firmwareVersion",
//                BuildConfig.VERSION_NAME
//            )
//        }
        xsLogger?.update(byteIds)
    }

    override fun onXsensDotDataExported(p0: String?, XsensDotRecordingFileInfo: XsensDotRecordingFileInfo?) {
        BleDebugLog.i(logTag, "onXsensDotDataExported-()222222")
        XsensDotRecordingFileInfo.let { fileInfo ->
            BleDebugLog.d(logTag, "id: [${fileInfo?.fileId}], fileName: ${fileInfo?.fileName} exported!!")
        }
    }

    override fun onXsensDotAllDataExported(p0: String?) {
        BleDebugLog.i(logTag, "onXsensDotAllDataExported-()")
        BleDebugLog.d(logTag, "모든 파일 exported 완료!!")
    }

    override fun onXsensDotStopExportingData(p0: String?) {
        BleDebugLog.i(logTag, "onXsensDotStopExportingData-()")
    }

    fun eraseInternal() {
        BleDebugLog.i(logTag, "eraseInternal-()")
        mManager?.eraseRecordingData()
    }

    fun startRecording() {
        BleDebugLog.i(logTag, "startRecording-()")
        mManager?.startRecording()
    }

    fun stopRecording() {
        BleDebugLog.i(logTag, "stopRecording-()")
        mManager?.stopRecording()
    }

    private fun terminateRecord() { // recording 콜백 삭제
        BleDebugLog.i(logTag, "terminateRecord-()")
        mManager?.clear()
        mManager = null
    }

    fun selectInternalFile() {
        BleDebugLog.i(logTag, "selectInternalFile-()")
        mManager?.requestFileInfo()
    }

    private fun setExportData(): ByteArray {
        BleDebugLog.i(logTag, "setExportData-()")
        val mSelectExportedDataIds = ByteArray(3)
        mSelectExportedDataIds[0] = XsensDotRecordingManager.RECORDING_DATA_ID_TIMESTAMP
        mSelectExportedDataIds[1] = XsensDotRecordingManager.RECORDING_DATA_ID_EULER_ANGLES
        mSelectExportedDataIds[2] = XsensDotRecordingManager.RECORDING_DATA_ID_CALIBRATED_ACC
        byteIds = mSelectExportedDataIds
        return mSelectExportedDataIds
    }

    fun exportFile() {
        if (internalList != null) {
            mManager?.startExporting(internalList!!)
        }
    }

    @SuppressLint("RestrictedApi")
    fun createLogger() {
        BleDebugLog.i(logTag, "createLogger-()")
        val logger = XsensDotLogger.createMtbLogger(App.context(), "address", "device-tag")
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