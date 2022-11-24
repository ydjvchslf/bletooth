package com.example.bledot.realtime

import androidx.lifecycle.ViewModel
import com.example.bledot.App
import com.example.bledot.util.BleDebugLog
import com.xsens.dot.android.sdk.events.XsensDotData
import com.xsens.dot.android.sdk.interfaces.XsensDotDeviceCallback
import com.xsens.dot.android.sdk.interfaces.XsensDotRecordingCallback
import com.xsens.dot.android.sdk.models.*
import com.xsens.dot.android.sdk.recording.XsensDotRecordingManager
import java.util.ArrayList

class RealtimeViewModel: ViewModel(), XsensDotRecordingCallback {

    private val logTag = RealtimeViewModel::class.simpleName
    private var mManager: XsensDotRecordingManager? = null

    init {
        BleDebugLog.i(logTag, "init-()")
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
            BleDebugLog.d(logTag, "index: [$index]번째, fileId: ${fileInfo.fileId}, fileName: ${fileInfo.fileName}, dataSize: ${fileInfo.dataSize}, ")
        }
    }

    override fun onXsensDotDataExported(
        p0: String?,
        p1: XsensDotRecordingFileInfo?,
        p2: XsensDotData?
    ) {
        BleDebugLog.i(logTag, "onXsensDotDataExported-()")
    }

    override fun onXsensDotDataExported(p0: String?, p1: XsensDotRecordingFileInfo?) {
        BleDebugLog.i(logTag, "onXsensDotDataExported-()")
    }

    override fun onXsensDotAllDataExported(p0: String?) {
        BleDebugLog.i(logTag, "onXsensDotAllDataExported-()")
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
        mManager?.requestFileInfo()
    }

}