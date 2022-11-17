package com.example.bledot.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanSettings
import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.bledot.adapter.BleAdapter
import com.example.bledot.data.BleDevice
import com.example.bledot.util.BleDebugLog
import com.xsens.dot.android.sdk.interfaces.XsensDotScannerCallback
import com.xsens.dot.android.sdk.models.XsensDotDevice
import com.xsens.dot.android.sdk.utils.XsensDotScanner

class BleViewModel : ViewModel(), XsensDotScannerCallback {

    private val logTag = BleViewModel::class.simpleName

    private var mXsScanner: XsensDotScanner? = null
    // 중복 체크되어 담긴 센서리스트
    private val mScannedSensorList = ArrayList<HashMap<String, Any>>()
    // 센서 데이터 담을 그릇, 즉 배열
    var mDeviceList = arrayListOf<BleDevice>()
    // 사용할 리사이클러뷰 생성
    var bleAdapter = BleAdapter()

    init {
        BleDebugLog.i(logTag, "init-()")
    }

    fun initXsScanner(context: Context) {
        BleDebugLog.i(logTag, "initXsScanner-()")
        mXsScanner = XsensDotScanner(context, this)
        mXsScanner?.setScanMode(ScanSettings.SCAN_MODE_BALANCED)
        mXsScanner?.startScan()
    }

    fun stopXsScanner() {
        mXsScanner?.stopScan()
    }

    @SuppressLint("MissingPermission")
    override fun onXsensDotScanned(device: BluetoothDevice, p1: Int) {
        BleDebugLog.i(logTag, "onXsensDotScanned-()")
        BleDebugLog.d(logTag, "name: ${device.name}, address: ${device.address}")

        device.let { device ->
            // Use the mac address as UID to filter the same scan result.
            var isExist = false
            for (map in mScannedSensorList) {
                if ((map["KEY_DEVICE"] as BluetoothDevice).address == device.address) isExist = true
            }

            if (!isExist) {
                // The original connection state is Disconnected.
                // Also set tag, battery state, battery percentage to default value.
                val map = HashMap<String, Any>()
                map["KEY_DEVICE"] = device
                map["KEY_NAME"] = device.name
                map["KEY_CONNECTION_STATE"] = XsensDotDevice.CONN_STATE_DISCONNECTED
                map["KEY_TAG"] = ""
                map["KEY_BATTERY_STATE"] = -1
                map["KEY_BATTERY_PERCENTAGE"] = -1
                mScannedSensorList.add(map)
            }
        }
        val list = BleDevice.fromHashMapList(mScannedSensorList) as ArrayList

        // 어답터
        bleAdapter.submitList(list) // 데이터 주입



        bleAdapter.listener = { device ->
            BleDebugLog.d(logTag, "device clicked-()")
            BleDebugLog.d(logTag, "device : $device")
        }
    }
}