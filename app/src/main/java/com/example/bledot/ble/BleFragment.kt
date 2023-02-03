package com.example.bledot.ble

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
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
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class BleFragment : Fragment(), XsensDotScannerCallback {

    private val logTag = BleFragment::class.simpleName
    private lateinit var binding: FragmentBleBinding
    private val bleViewModel: BleViewModel by activityViewModels()

    private var mXsScanner: XsensDotScanner? = null
    // 중복 체크되어 담긴 센서리스트
    private var mScannedSensorList = ArrayList<HashMap<String, Any>>()
    // 스캔 시작 후 발견된 센서 여부
    private var isFoundedSensor = MutableLiveData<Boolean?>()
    // 사용할 리사이클러뷰 생성
    var bleAdapter = BleAdapter()
    // 시간 제한 타이머
    var timer: Timer? = null

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

        // 연결된 장치 있는지 체크
        val device = isConnectedSensor()
        // 뷰모델 뷰 체크
        checkCrnState()

        BleDebugLog.d(logTag, "timer => $timer")

        // 스캔 상태 체크 -> 버튼 문구 표시
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
                bleViewModel.BLE_STATE.postValue(BleState.SCANNING)

                bleAdapter.clear()
                bleViewModel.disconnectAllSensor()
                bleViewModel.mSensorList.value?.clear()
                mScannedSensorList.clear()

                initXsScanner(requireContext())
            } else { // 스캔 중단
                stopXsScanner()
            }
        }

        // 어답터
//        binding.recyclerView.apply {
//            adapter = bleAdapter
//        }
//
//        if (bleViewModel.mSensorList.value != null) {
//            val list = BleDevice.fromHashMapList(bleViewModel.mScannedSensorList) as ArrayList // 스캔된 데이터 리스트
//            bleAdapter.submitList(list) // 데이터 주입
//        }

        // 클릭리스너 (연결/해제) 활용
        bleAdapter.clickListener = { _, index ->
            // 스캔 중단 &
            //stopXsScanner()
            btScanningStatus.value = false

            bleViewModel.mConnectedIndex = index
            val clickedBleDevice = bleViewModel.getBleFromSensorList(index)
            val xsDevice = bleViewModel.makeBleToXsDevice(requireContext(), clickedBleDevice)

            if (bleViewModel.mConnectionState.value == 0) { // 아예 비연결 상태 -> 연결 시작
                BleDebugLog.d(logTag, "=====연결시작")
                bleViewModel.connectSensor(xsDevice)
            } else { // 선택된 device 연결상태 or 다른 것이 연결 상태 -> 연결 끊기
                BleDebugLog.d(logTag, "=====연결끊기")
                bleViewModel.disconnectAllSensor()
            }
        }

//        device?.let {
//            return
//        }

//        // 뷰모델 뷰 체크
//        checkCrnState()

        // 연결 UI 업데이트
        bleViewModel.mConnectionState.observe(viewLifecycleOwner) { connectState ->
            if (connectState == 2) {
                bleViewModel.mConnectedXsDevice.value?.let {
                    bleAdapter.updateConnectState(
                        fromXsDeviceToBleDevice(it),
                        bleViewModel.mConnectedIndex
                    )
                }
            } else if (connectState == 0) {
                BleDebugLog.d(logTag, "=====connectState: 0")
                val list = BleDevice.fromHashMapList(bleViewModel.mScannedSensorList) as ArrayList // 스캔된 데이터 리스트
                bleAdapter.submitList(list)
            }
        }

        /*
        bleViewModel.BLE_STATE.observe(viewLifecycleOwner) { BleState ->
            if (BleState == com.example.bledot.ble.BleState.SCAN_COMPLETE_CONNECTED) {
                BleDebugLog.d(logTag, "BleState == SCAN_COMPLETE_CONNECTED")

                val connectedDevice = bleViewModel.mConnectedXsDevice.value
                connectedDevice?.let {
                    val bleDevice = BleDevice.fromXsDeviceToBleDevice(it)
                    val list = arrayListOf(bleDevice)
                    binding.recyclerView.apply {
                        adapter = bleAdapter
                        bleAdapter.submitList(list)
                    }
                }
            } else if (BleState == com.example.bledot.ble.BleState.SCAN_COMPLETE_DISCONNECTED) {
                BleDebugLog.d(logTag, "BleState == TRYING_TO_DISCONNECT")
                val connectedDevice = bleViewModel.mConnectedXsDevice.value
                connectedDevice?.let {
                    BleDebugLog.d(logTag, "${it.connectionState}")
                    val bleDevice = BleDevice.fromXsDeviceToBleDevice(it)
                    val list = arrayListOf(bleDevice)
                    binding.recyclerView.apply {
                        adapter = bleAdapter
                        bleAdapter.submitList(list)
                    }
                }
            }
        }
         */
    }

    private fun checkCrnState() {
        val state = bleViewModel.BLE_STATE.value
        when(state) {
            BleState.NOT_SCANNED -> {}
            BleState.SCAN_COMPLETE_DISCONNECTED -> {
                BleDebugLog.d(logTag, "BleState == SCAN_COMPLETE_DISCONNECTED")
                val list = BleDevice.fromHashMapList(bleViewModel.mScannedSensorList) as ArrayList // 스캔된 데이터 리스트
                binding.recyclerView.apply {
                    adapter = bleAdapter
                    bleAdapter.submitList(list)
                }
            }
            BleState.SCAN_COMPLETE_CONNECTED -> {
                BleDebugLog.d(logTag, "BleState == SCAN_COMPLETE_CONNECTED")
                val connectedDevice = bleViewModel.mConnectedXsDevice.value
                connectedDevice?.let {
                    BleDebugLog.d(logTag, "${it.connectionState}")
                    val bleDevice = BleDevice.fromXsDeviceToBleDevice(it)
                    val list = arrayListOf(bleDevice)
                    binding.recyclerView.apply {
                        adapter = bleAdapter
                        bleAdapter.submitList(list)
                    }
                }
            }
            else -> {}
        }
    }

    private fun isConnectedSensor(): XsensDotDevice? {
        BleDebugLog.i(logTag, "isConnectedSensor-()")
        bleViewModel.mConnectedXsDevice.value.let { device ->
            if (device == null) {
                BleDebugLog.d(logTag, "device == null")
                return null
            } else { // 이미 연결중
                BleDebugLog.d(logTag, "device 이미 연결 중")
//                binding.layoutConnected.visibility = View.VISIBLE
//                binding.layoutAfter.visibility = View.GONE
//
//                binding.batteryPer.text = device.batteryPercentage.toString()
//                binding.name.text = device.name
//                binding.address.text = device.address
//                binding.battery.text = device.batteryState.toString()
                return device
            }
        }
    }

    private fun initXsScanner(context: Context) {
        BleDebugLog.i(logTag, "initXsScanner-()")
        mXsScanner = XsensDotScanner(context, this)
        mXsScanner?.setScanMode(ScanSettings.SCAN_MODE_BALANCED)
        mXsScanner?.startScan()

        bleViewModel.hasScanHistory = true

        if (timer == null) {
            timer = Timer()
        }
        autoStopXsScanner()
    }

    private fun stopXsScanner() { // 수동 중지
        BleDebugLog.i(logTag, "stopXsScanner-()")

        timer?.let {
            it.cancel()
            timer = null
        }
        mXsScanner?.stopScan()

        BleDebugLog.d(logTag, "mScannedSensorList.size : ${mScannedSensorList.size}")

        activity?.runOnUiThread {
            if (mScannedSensorList.size == 0) {
                bleViewModel.BLE_STATE.value = BleState.NOT_SCANNED
                BleDebugLog.d(logTag, "${bleViewModel.BLE_STATE.value}")
                showDialog("NOT FOUNDED", "Try to scan again?")
            } else {
                bleViewModel.BLE_STATE.value = BleState.SCAN_COMPLETE_DISCONNECTED
                BleDebugLog.d(logTag, "${bleViewModel.BLE_STATE.value}")
                btScanningStatus.value = false

                // 어댑터 및 어댑터 데이터 주입
                binding.recyclerView.apply {
                    adapter = bleAdapter
                }

                val list =
                    BleDevice.fromHashMapList(bleViewModel.mScannedSensorList) as ArrayList // 스캔된 데이터 리스트
                bleAdapter.submitList(list)
            }
        }
    }

    private fun autoStopXsScanner() { // 10초 후 자동 중지
        timer?.schedule(object : TimerTask() {
            override fun run() {
                BleDebugLog.i(logTag, "autoStopXsScanner-()")
                stopXsScanner()
            }
        }, 10000L) // 10 초
    }

    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("MissingPermission")
    override fun onXsensDotScanned(device: BluetoothDevice, p1: Int) {
        BleDebugLog.i(logTag, "onXsensDotScanned-()")
        BleDebugLog.d(logTag, "name: ${device.name}, address: ${device.address}")
        BleDebugLog.d(logTag, "device.toString(): ${device}")

        device.let { device ->
            // Use the mac address as UID to filter the same scan result.
            var isExist = false
            for (map in mScannedSensorList) {
                if ((map["KEY_DEVICE"] as BluetoothDevice).address == device.address) isExist =
                    true
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
                val list =
                    BleDevice.fromHashMapList(this.mScannedSensorList) as ArrayList // 스캔된 데이터 리스트
                bleAdapter.submitList(list)
            }
        }
    }

    private fun showDialog(title: String, subTitle: String) {
        btScanningStatus.value = false
        val builder = AlertDialog.Builder(context).apply {
            setTitle(title)
            setMessage(subTitle)
            setPositiveButton("YES") { _, _ ->
                bleViewModel.BLE_STATE.postValue(BleState.SCANNING)
                btScanningStatus.value = true

                bleAdapter.clear()
                bleViewModel.disconnectAllSensor()
                bleViewModel.mSensorList.value?.clear()
                mScannedSensorList.clear()

                initXsScanner(requireContext())
            }
            setNegativeButton("NO") { _, _ ->

            }
        }
        builder.create().show()
    }
}