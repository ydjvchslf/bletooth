package com.example.bledot.ble

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanSettings
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.bledot.R
import com.example.bledot.databinding.FragmentBleBinding
import com.example.bledot.util.BleDebugLog
import com.example.bledot.util.btScanningStatus
import com.xsens.dot.android.sdk.interfaces.XsensDotScannerCallback
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
        device
        val name = device.name
        val address = device.address
        val uuids = device.uuids
        BleDebugLog.d(logTag, "name: $name, address: $address, uuid: $uuids")
    }

    private fun stopXsScanner() {
        mXsScanner?.stopScan()
    }
}