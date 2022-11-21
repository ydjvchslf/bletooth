package com.example.bledot.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.bledot.R
import com.example.bledot.adapter.BleAdapter
import com.example.bledot.data.BleDevice
import com.example.bledot.data.BleDevice.Companion.fromXsDeviceToBleDevice
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
    // 중복 체크되어 담긴 센서리스트
    private val mScannedSensorList = ArrayList<HashMap<String, Any>>()
    // 사용할 리사이클러뷰 생성
    var bleAdapter = BleAdapter()

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
                initXsScanner(requireContext())
            } else { // 스캔 중단
                stopXsScanner()
            }
        }

        // 클릭리스너 (연결/해제) 활용
        bleAdapter.clickListener = { _, index ->
            bleViewModel.mConnectedIndex.value = index
            val clickedBleDevice = bleViewModel.getBleFromSensorList(index)
            val xsDevice = bleViewModel.makeBleToXsDevice(requireContext(), clickedBleDevice)
            //bleViewModel.makeBleToXsDevice(requireContext(), bleViewModel.getXsDeviceFromSensorList(index))
            if (bleViewModel.mConnectedXsDevice.value != null) { // 아예 비연결 상태 -> 연결 시작
                bleViewModel.connectSensor(xsDevice)
            } else { // 선택된 device 연결상태 or 다른 것이 연결 상태 -> 연결 끊기

            }
        }

        // 연결 UI 업데이트
        bleViewModel.mConnectionState.observe(viewLifecycleOwner) { connectState ->
            if (connectState == 2) {
                bleAdapter.updateConnectState(
                    fromXsDeviceToBleDevice(bleViewModel.mConnectedXsDevice.value!!),
                    bleViewModel.mConnectedIndex.value!!
                )
            }
        }
    }

    private fun initXsScanner(context: Context) {
        BleDebugLog.i(logTag, "initXsScanner-()")
        mXsScanner = XsensDotScanner(context, this)
        mXsScanner?.setScanMode(ScanSettings.SCAN_MODE_BALANCED)
        mXsScanner?.startScan()
    }

    private fun stopXsScanner() {
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
                bleViewModel.mScannedSensorList = this.mScannedSensorList
            }
        }
        val list = BleDevice.fromHashMapList(mScannedSensorList) as ArrayList // 스캔된 데이터 리스트

        // 어답터
        binding.recyclerView.apply {
            adapter = bleAdapter
        }
        bleAdapter.submitList(list) // 데이터 주입
    }
}