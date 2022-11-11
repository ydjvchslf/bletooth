package com.example.bledot.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanSettings
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.bledot.R
import com.example.bledot.databinding.FragmentBleBinding
import com.example.bledot.util.BleDebugLog
import com.example.bledot.util.btScanningStatus
import com.xsens.dot.android.sdk.interfaces.XsensDotScannerCallback
import com.xsens.dot.android.sdk.models.XsensDotDevice
import com.xsens.dot.android.sdk.utils.XsensDotScanner


class BleFragment : Fragment(), XsensDotScannerCallback {

    private val logTag = BleFragment::class.simpleName
    private lateinit var binding: FragmentBleBinding
    private val bleViewModel: BleViewModel by activityViewModels()

    private var mXsScanner: XsensDotScanner? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        BleDebugLog.i(logTag, "onCreateView-()")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ble, container, false)
        with(binding) {
            viewModel = bleViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        BleDebugLog.i(logTag, "onViewCreated-()")
        super.onViewCreated(view, savedInstanceState)

        // 스캔 상태 체크
        btScanningStatus.observe(viewLifecycleOwner) { btStatus ->
            if (btStatus) {
                BleDebugLog.d(logTag, "btStatus: $btStatus")
                binding.scanBtn.text = "SCAN STOP"
            } else {
                BleDebugLog.d(logTag, "btStatus: $btStatus")
                binding.scanBtn.text = "SCAN START"
            }
        }

        // 스캔 시작/종료
        binding.scanBtn.setOnClickListener {
            btScanningStatus.value = btScanningStatus.value != true
            BleDebugLog.d(logTag, "버튼 클릭 후 btStatus: ${btScanningStatus.value}")
            if (btScanningStatus.value == true) {
                // 스캔 시작
                initXsScanner()
            } else { // 스캔 중단
                stopXsScanner()
            }
        }
    }

    private fun initXsScanner() {
        BleDebugLog.i(logTag, "initXsScanner-()")
        mXsScanner = XsensDotScanner(activity?.applicationContext, this)
        mXsScanner?.setScanMode(ScanSettings.SCAN_MODE_BALANCED)
        mXsScanner?.startScan()
    }

    @SuppressLint("MissingPermission")
    override fun onXsensDotScanned(device: BluetoothDevice, p1: Int) {
        BleDebugLog.i(logTag, "onXsensDotScanned-()")
        BleDebugLog.d(logTag, "name: ${device.name}, address: ${device.address}")

        // 스캔된 센서 중복 체크하여 센서리스트 담기
        val mScannedSensorList = ArrayList<HashMap<String, Any>>()

        device.let { device ->
            // Use the mac address as UID to filter the same scan result.
            var isExist = false
            for (map in mScannedSensorList) {
                if ((map["device"] as BluetoothDevice).address == device.address) isExist = true
            }

            if (!isExist) {
                // The original connection state is Disconnected.
                // Also set tag, battery state, battery percentage to default value.
                val map = HashMap<String, Any>()
                map["KEY_DEVICE"] = device
                map["KEY_CONNECTION_STATE"] = XsensDotDevice.CONN_STATE_DISCONNECTED
                map["KEY_TAG"] = ""
                map["KEY_BATTERY_STATE"] = -1
                map["KEY_BATTERY_PERCENTAGE"] = -1
                mScannedSensorList.add(map)
            }
        }
        BleDebugLog.d(logTag, "mScannedSensorList.size: ${mScannedSensorList.size}")
    }

    private fun stopXsScanner() {
        mXsScanner?.stopScan()
    }
}