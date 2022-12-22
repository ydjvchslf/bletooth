package com.example.bledot.ble

import android.bluetooth.BluetoothDevice
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bledot.data.BleDevice
import com.example.bledot.data.XYZData
import com.example.bledot.util.*
import com.xsens.dot.android.sdk.events.XsensDotData
import com.xsens.dot.android.sdk.interfaces.XsensDotDeviceCallback
import com.xsens.dot.android.sdk.interfaces.XsensDotMeasurementCallback
import com.xsens.dot.android.sdk.models.FilterProfileInfo
import com.xsens.dot.android.sdk.models.XsensDotDevice
import com.xsens.dot.android.sdk.models.XsensDotPayload
import com.xsens.dot.android.sdk.utils.XsensDotLogger

class BleViewModel: ViewModel(), XsensDotDeviceCallback, XsensDotMeasurementCallback {

    private val logTag = BleViewModel::class.simpleName
    // 중복 체크되어 담긴 센서리스트
    var mScannedSensorList = ArrayList<HashMap<String, Any>>()
    // 연결 상태 바뀐 센서리스트
    var mSensorList = MutableLiveData<ArrayList<XsensDotDevice>>() // 초기값 null
    // A variable to notify the connection state
    var mConnectionState = MutableLiveData(0) // 0 미연결, 2 연결
    var mConnectedXsDevice = MutableLiveData<XsensDotDevice?>() // 선택된 device
    var mConnectedIndex = -1 // 리스트 index
    // KEY
    val KEY_DEVICE = "KEY_DEVICE"
    // data 리스너
    var dataListener : ((XYZData) -> Unit)? = null
    // 100개 단위
    var xyzData100List = ArrayList<XYZData>()

    init {
        BleDebugLog.i(logTag, "init-()")
    }

    fun startMeasure(device: XsensDotDevice) {
        BleDebugLog.i(logTag, "setMode-()")
        BleDebugLog.d(logTag, "${device.batteryPercentage}")
        device.measurementMode = XsensDotPayload.PAYLOAD_TYPE_COMPLETE_EULER
        device.startMeasuring()
    }

    fun stopMeasure(device: XsensDotDevice) {
        device.stopMeasuring()
        //closeFiles()
    }

    fun getBleFromSensorList(index: Int): BluetoothDevice {
        BleDebugLog.i(logTag, "getBleFromSensorList-()")
        return mScannedSensorList[index][KEY_DEVICE] as BluetoothDevice
    }

    fun makeBleToXsDevice(context: Context, device: BluetoothDevice) : XsensDotDevice? {
        BleDebugLog.i(logTag, "makeBleToXsDevice-()")
        mConnectedXsDevice.value = XsensDotDevice(context, device, this)
        return mConnectedXsDevice.value
    }

    fun connectSensor(xsDevice: XsensDotDevice?) {
        BleDebugLog.i(logTag, "connectSensor-()")
        addDevice(xsDevice)
        xsDevice?.connect()
    }

    fun disconnectAllSensor() {
        BleDebugLog.i(logTag, "disconnectSensor-()")
        if (mSensorList.value != null) {
            for (device in mSensorList.value!!) {
                if (device.address == mConnectedXsDevice.value?.address) {
                    device.disconnect()
                    break
                }
            }
        }
        mSensorList.value?.clear()
        mConnectionState.value = 0
        mConnectedXsDevice.value = null
    }

    fun addDevice(xsDevice: XsensDotDevice?) {
        BleDebugLog.i(logTag, "addDevice-()") // var mSensorList = MutableLiveData<ArrayList<XsensDotDevice>>()
        if (mSensorList.value == null) mSensorList.value = ArrayList() // mSensorList = ArrayList<XsensDotDevice>()

        val devices = mSensorList.value // val devices = ArrayList<XsensDotDevice>()
        var isExist = false

        for (_xsDevice in devices!!) {
            if (xsDevice!!.address == _xsDevice.address) {
                isExist = true
                break
            }
        }

        if (!isExist) devices.add(xsDevice!!) // val devices = [XsensDotDevice, XsensDotDevice ...]
        BleDebugLog.d(logTag, "devices.size: ${devices.size}")
    }

    override fun onXsensDotConnectionChanged(address: String?, p1: Int) {
        BleDebugLog.i(logTag, "onXsensDotConnectionChanged-()")
    }

    override fun onXsensDotServicesDiscovered(p0: String?, p1: Int) {
        BleDebugLog.i(logTag, "onXsensDotServicesDiscovered-()")
    }

    override fun onXsensDotFirmwareVersionRead(p0: String?, p1: String?) {
        BleDebugLog.i(logTag, "onXsensDotFirmwareVersionRead-()")
    }

    override fun onXsensDotTagChanged(p0: String?, p1: String?) {
        BleDebugLog.i(logTag, "onXsensDotTagChanged-()")
    }

    override fun onXsensDotBatteryChanged(p0: String?, p1: Int, p2: Int) {
        BleDebugLog.i(logTag, "onXsensDotBatteryChanged-()")
    }

    override fun onXsensDotDataChanged(address: String?, xsData: XsensDotData?) {
        BleDebugLog.i(logTag, "onXsensDotDataChanged-()")
        //BleDebugLog.d(logTag, "address: $address, xsData: $xsData")
        val eulerAngles = xsData?.euler
        if (eulerAngles != null) {
            val eulerAnglesStr = String.format("%.6f", eulerAngles[0]) + ", "+ String.format("%.6f", eulerAngles[1]) + ", " + String.format("%.6f", eulerAngles[2])
            //BleDebugLog.d(logTag, "eulerAnglesStr: $eulerAnglesStr")

            val xEuler = String.format("%.6f", eulerAngles[0]).toDouble()
            val yEuler = String.format("%.6f", eulerAngles[1]).toDouble()
            val zEuler = String.format("%.6f", eulerAngles[2]).toDouble()

            if (xyzData100List.size < 50) {
                xyzData100List.add(XYZData(xEuler, yEuler, zEuler))
            } else {
                dataListener?.invoke(xyzData100List[xyzData100List.lastIndex])
                xyzData100List.clear()
            }

            // 모듈러 연산

            // data 파일에 업데이트
            //updateFiles(address!!, xsData)
        }
    }

    override fun onXsensDotInitDone(p0: String?) {
        BleDebugLog.i(logTag, "onXsensDotInitDone-()")
        mConnectionState.value = mConnectedXsDevice.value!!.connectionState
        BleDebugLog.d(logTag, "mConnectionState.value: ${mConnectionState.value}")
        BleDebugLog.d(logTag, "mConnectedIndex : $mConnectedIndex")
        BleDebugLog.d(logTag, "mConnectedXsDevice.value?.connectionState: ${mConnectedXsDevice.value?.connectionState}")
    }

    override fun onXsensDotButtonClicked(p0: String?, p1: Long) {
        BleDebugLog.i(logTag, "onXsensDotButtonClicked-()")
    }

    override fun onXsensDotPowerSavingTriggered(p0: String?) {
        BleDebugLog.i(logTag, "onXsensDotPowerSavingTriggered-()")
    }

    override fun onReadRemoteRssi(p0: String?, p1: Int) {
        BleDebugLog.i(logTag, "onReadRemoteRssi-()")
    }

    override fun onXsensDotOutputRateUpdate(p0: String?, p1: Int) {
        BleDebugLog.i(logTag, "onXsensDotOutputRateUpdate-()")
    }

    override fun onXsensDotFilterProfileUpdate(p0: String?, p1: Int) {
        BleDebugLog.i(logTag, "onXsensDotFilterProfileUpdate-()")
    }

    override fun onXsensDotGetFilterProfileInfo(
        p0: String?,
        p1: java.util.ArrayList<FilterProfileInfo>?
    ) {
        BleDebugLog.i(logTag, "onXsensDotGetFilterProfileInfo-()")
    }

    override fun onSyncStatusUpdate(p0: String?, p1: Boolean) {
        BleDebugLog.i(logTag, "onSyncStatusUpdate-()")
    }

    fun makeResetZero(xsDevice: XsensDotDevice) {
        BleDebugLog.i(logTag, "makeResetZero-()")
        xsDevice.setXsensDotMeasurementCallback(this)
        xsDevice.resetHeading()
    }

    // 센서 zero 인터페이스
    override fun onXsensDotHeadingChanged(p0: String?, p1: Int, p2: Int) {
        BleDebugLog.i(logTag, "onXsensDotHeadingChanged-()")
    }
    // 센서 zero 인터페이스
    override fun onXsensDotRotLocalRead(p0: String?, p1: FloatArray?) {
        BleDebugLog.i(logTag, "onXsensDotRotLocalRead-()")
    }

    // 센서 data 실시간 받아오면서 update
    private fun updateFiles(address: String, data: XsensDotData) {
        BleDebugLog.i(logTag, "updateFiles-()")
        BleDebugLog.d(logTag, "mLoggerList: ${mLoggerList.value}")

        for (map in mLoggerList.value!!) {
            val _address = map[KEY_ADDRESS] as String
            if (_address == address) {
                val logger = map[KEY_LOGGER] as XsensDotLogger?
                logger?.update(data)
            }
        }

    }
}