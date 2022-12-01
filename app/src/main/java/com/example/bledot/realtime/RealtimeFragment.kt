package com.example.bledot.realtime

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import com.example.bledot.App
import com.example.bledot.R
import com.example.bledot.WebAppInterface
import com.example.bledot.ble.BleViewModel
import com.example.bledot.databinding.FragmentRealtimeBinding
import com.example.bledot.util.BleDebugLog

class RealtimeFragment : Fragment() {

    private val logTag = RealtimeFragment::class.simpleName
    private lateinit var binding: FragmentRealtimeBinding
    private val realtimeViewModel: RealtimeViewModel by activityViewModels()
    private val bleViewModel: BleViewModel by activityViewModels()
    private var xyAxis = MutableLiveData<ArrayList<Double>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        BleDebugLog.i(logTag, "onCreateView-()")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_realtime, container, false)
        with(binding) {
            viewModel = realtimeViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BleDebugLog.i(logTag, "onViewCreated-()")

        // zeroing
        binding.zeroing.setOnClickListener {
            when (bleViewModel.mConnectedXsDevice.value) {
                null -> {
                    Toast.makeText(App.context(), "기기 먼저 연결하세요", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    bleViewModel.makeResetZero(bleViewModel.mConnectedXsDevice.value!!)
                }
            }
        }
        // realtime start
        binding.start.setOnClickListener {
            when (bleViewModel.mConnectedXsDevice.value) {
                null -> {
                    Toast.makeText(App.context(), "기기 먼저 연결하세요", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    bleViewModel.startMeasure(bleViewModel.mConnectedXsDevice.value!!)
                }
            }
        }
        // realtime stop
        binding.stop.setOnClickListener {
            when (bleViewModel.mConnectedXsDevice.value) {
                null -> {
                    Toast.makeText(App.context(), "기기 먼저 연결하세요", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    bleViewModel.stopMeasure(bleViewModel.mConnectedXsDevice.value!!)
                }
            }
        }
        // init
        binding.init.setOnClickListener {
            realtimeViewModel.initRecord(bleViewModel.mConnectedXsDevice.value!!)
        }
        // RC start
        binding.RcStart.setOnClickListener {
            realtimeViewModel.startRecording()
        }
        // RC stop
        binding.RcStop.setOnClickListener {
            realtimeViewModel.stopRecording()
        }
        // Internal erase
        binding.internalErase.setOnClickListener {
            realtimeViewModel.eraseInternal()
        }
        // Select internal file
        binding.selectFileBtn.setOnClickListener {
            realtimeViewModel.selectInternalFile()
        }
        // Export btn
        binding.exportBtn.setOnClickListener {
            realtimeViewModel.exportFile()
        }
        // Logger btn
        binding.loggerBtn.setOnClickListener {
            realtimeViewModel.createLogger()
        }
        binding.fileSaveBtn.setOnClickListener {
            realtimeViewModel.createFile(bleViewModel.mConnectedXsDevice.value!!)
        }
        binding.fileCloseBtn.setOnClickListener {
            realtimeViewModel.closeFiles()
        }
        // 웹뷰 js 인터페이스 연결
        binding.realWebView.apply {
            settings.javaScriptEnabled = true
            addJavascriptInterface(WebAppInterface(App.context()), "Android") // Android란 이름으로 js 인터페이스 설정
            loadUrl("file:///android_asset/sample.html")
        }
        // 실시간 data 리스너
        bleViewModel.dataListener = { x, y ->
            BleDebugLog.d(logTag, "x값: [$x], y값: [$y]")
            val arrayDouble = ArrayList<Double>()
            arrayDouble.add(x)
            arrayDouble.add(y)
            xyAxis.postValue(arrayDouble)
        }

        xyAxis.observe(viewLifecycleOwner) {
            BleDebugLog.i(logTag, "xyAxis?.observe-()")
            BleDebugLog.d(logTag, "감지중!!!!! x: ${it[0]}, y: ${it[1]}")
            binding.realWebView.loadUrl("javascript:addData(${it[0]}, ${it[1]})")
        }
    }
}