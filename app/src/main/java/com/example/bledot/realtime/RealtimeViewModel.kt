package com.example.bledot.realtime

import androidx.lifecycle.ViewModel
import com.example.bledot.util.BleDebugLog
import com.xsens.dot.android.sdk.events.XsensDotData
import com.xsens.dot.android.sdk.interfaces.XsensDotDeviceCallback
import com.xsens.dot.android.sdk.models.FilterProfileInfo
import com.xsens.dot.android.sdk.models.XsensDotDevice
import com.xsens.dot.android.sdk.models.XsensDotPayload
import java.util.ArrayList

class RealtimeViewModel: ViewModel() {

    private val logTag = RealtimeViewModel::class.simpleName

    init {
        BleDebugLog.i(logTag, "init-()")
    }


//
//    override fun onXsensDotConnectionChanged(p0: String?, p1: Int) {
//        BleDebugLog.i(logTag, "onXsensDotConnectionChanged-()")
//    }
//
//    override fun onXsensDotServicesDiscovered(p0: String?, p1: Int) {
//        BleDebugLog.i(logTag, "onXsensDotServicesDiscovered-()")
//    }
//
//    override fun onXsensDotFirmwareVersionRead(p0: String?, p1: String?) {
//        BleDebugLog.i(logTag, "onXsensDotFirmwareVersionRead-()")
//    }
//
//    override fun onXsensDotTagChanged(p0: String?, p1: String?) {
//        BleDebugLog.i(logTag, "onXsensDotTagChanged-()")
//    }
//
//    override fun onXsensDotBatteryChanged(p0: String?, p1: Int, p2: Int) {
//        BleDebugLog.i(logTag, "inonXsensDotBatteryChangedt-()")
//    }
//
//    // 여기 안타고 BleViewModel 에서 탐
//    override fun onXsensDotDataChanged(p0: String?, data: XsensDotData?) {
//        BleDebugLog.i(logTag, "onXsensDotDataChanged-()")
//        val xsData = data?.euler
//        BleDebugLog.d(logTag, "data: ${xsData.toString()}")
//    }
//
//    override fun onXsensDotInitDone(p0: String?) {
//        BleDebugLog.i(logTag, "onXsensDotInitDone-()")
//    }
//
//    override fun onXsensDotButtonClicked(p0: String?, p1: Long) {
//        BleDebugLog.i(logTag, "onXsensDotButtonClicked-()")
//    }
//
//    override fun onXsensDotPowerSavingTriggered(p0: String?) {
//        BleDebugLog.i(logTag, "onXsensDotPowerSavingTriggered-()")
//    }
//
//    override fun onReadRemoteRssi(p0: String?, p1: Int) {
//        BleDebugLog.i(logTag, "onReadRemoteRssi-()")
//    }
//
//    override fun onXsensDotOutputRateUpdate(p0: String?, p1: Int) {
//        BleDebugLog.i(logTag, "onXsensDotOutputRateUpdate-()")
//    }
//
//    override fun onXsensDotFilterProfileUpdate(p0: String?, p1: Int) {
//        BleDebugLog.i(logTag, "onXsensDotFilterProfileUpdate-()")
//    }
//
//    override fun onXsensDotGetFilterProfileInfo(p0: String?, p1: ArrayList<FilterProfileInfo>?) {
//        BleDebugLog.i(logTag, "onXsensDotGetFilterProfileInfo-()")
//    }
//
//    override fun onSyncStatusUpdate(p0: String?, p1: Boolean) {
//        BleDebugLog.i(logTag, "onSyncStatusUpdate-()")
//    }

}