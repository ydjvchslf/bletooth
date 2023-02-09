package com.example.bledot.ble

import android.bluetooth.BluetoothDevice
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

    // Device 연결 상태
    var BLE_STATE = MutableLiveData(BleState.NOT_SCANNED)
    // 스캔 작업 히스토리 여부
    var hasScanHistory = false
    // 스캔 중
    var isScanning = false
    // 연결 중
    var isConnecting = false
    // 끊기 중
    var isDisconnecting = false

    // data 리스너
    var dataListener : ((XYZData) -> Unit)? = null
    // data2 리스너
    var data2Listener : ((XYZData) -> Unit)? = null
    // 50개 단위
    var xyzData50List = ArrayList<XYZData>()

    init {
        BleDebugLog.i(logTag, "init-()")
        BleDebugLog.d(logTag, "BLE_STATE: ${BLE_STATE.value}")
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
        BLE_STATE.value = BleState.TRYING
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
        //mSensorList.value?.clear()
        //mConnectionState.value = 0
        //mConnectedXsDevice.value = null
        if(this.BLE_STATE.value == BleState.CONNECTED){
            BLE_STATE.value = (BleState.NOT_SCANNED)
            mConnectionState.value = 0
            mConnectedXsDevice.value = null
            return
        }
        if(this.BLE_STATE.value == BleState.SCAN_COMPLETE_CONNECTED){
            BLE_STATE.value = (BleState.NOT_SCANNED)
            mConnectionState.value = 0
            mConnectedXsDevice.value = null
            return
        }
        BLE_STATE.value = (BleState.SCAN_COMPLETE_DISCONNECTED)
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
        //BLE_STATE.postValue(BleState.TRYING)
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

            BleDebugLog.d(logTag, "xEuler: $xEuler, yEuler: $yEuler, zEuler: $zEuler")

            if (xyzData50List.size < 50) {
                xyzData50List.add(XYZData(xEuler, yEuler, zEuler))
                //if(xyzData50List.size == 20){
                    data2Listener?.invoke(xyzData50List[xyzData50List.lastIndex])
                //}
            } else {
                dataListener?.invoke(xyzData50List[xyzData50List.lastIndex])
                xyzData50List.clear()
            }

            // 모듈러 연산

            // data 파일에 업데이트
            //updateFiles(address!!, xsData)

        }
    }

    override fun onXsensDotInitDone(p0: String?) {
        BleDebugLog.i(logTag, "onXsensDotInitDone-()")
        mConnectionState.value = mConnectedXsDevice.value?.connectionState
        if (mConnectionState.value == 0) { // 안타는거같아
            BLE_STATE.value = (BleState.SCAN_COMPLETE_DISCONNECTED)
        } else if (mConnectionState.value == 2) {
            BLE_STATE.value = (BleState.SCAN_COMPLETE_CONNECTED)
        }

        BleDebugLog.d(logTag, "mConnectionState.value: ${mConnectionState.value}")
        BleDebugLog.d(logTag, "mConnectedXsDevice.value.tag: ${mConnectedXsDevice.value?.tag}")
        BleDebugLog.d(logTag, "mConnectedIndex : $mConnectedIndex")
        BleDebugLog.d(logTag, "mConnectedXsDevice.value?.connectionState: ${mConnectedXsDevice.value?.connectionState}")
        BleDebugLog.d(logTag, "BLE_STATE: ${BLE_STATE.value}")
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