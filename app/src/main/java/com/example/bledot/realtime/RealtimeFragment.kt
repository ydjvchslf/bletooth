package com.example.bledot.realtime

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView.setWebContentsDebuggingEnabled
import android.widget.Switch
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import com.example.bledot.App
import com.example.bledot.R
import com.example.bledot.WebAppInterface
import com.example.bledot.ble.BleViewModel
import com.example.bledot.data.XYZData
import com.example.bledot.databinding.FragmentRealtimeBinding
import com.example.bledot.util.BleDebugLog
import com.google.gson.Gson
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.timer

class RealtimeFragment : Fragment() {

    private val logTag = RealtimeFragment::class.simpleName
    private lateinit var binding: FragmentRealtimeBinding
    private val realtimeViewModel: RealtimeViewModel by activityViewModels()
    private val bleViewModel: BleViewModel by activityViewModels()
    // 웹뷰용 dataList
    var webViewList = ArrayList<XYZData>()
    // 경과 시간 위한 timer
    private var time = 0
    private var timerTask: Timer? = null

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
        // toggle btn
        binding.toggleBtn.setOnClickListener {
            val isToggleValue = binding.toggleBtn.isChecked
            BleDebugLog.d(logTag, "isToggleValue: $isToggleValue")
            realtimeViewModel.isWearingOption = isToggleValue
        }
        // recording start btn
        binding.recordBtn.setOnClickListener {
            BleDebugLog.i(logTag, "녹화 Start")
            realtimeViewModel.isRecording.value = true
            BleDebugLog.i(logTag, "isRecording: ${realtimeViewModel.isRecording.value}")
            startTimer()
        }
        // recording stop btn
        binding.stopBtn.setOnClickListener {
            BleDebugLog.i(logTag, "녹화 Stop")
            realtimeViewModel.isRecording.value = false
            BleDebugLog.i(logTag, "isRecording: ${realtimeViewModel.isRecording.value}")
            stopTimer()
            showDialog("New Data", "Do you want to upload to the server?")
        }
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
                    binding.realWebView.loadUrl("javascript:stopStreaming()")
                }
            }
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
            setWebContentsDebuggingEnabled(true)
            addJavascriptInterface(WebAppInterface(App.context()), "Android") // Android란 이름으로 js 인터페이스 설정
            loadUrl("file:///android_asset/sample.html")
        }
        // 실시간 data 리스너
        bleViewModel.dataListener = { xyzData ->
            BleDebugLog.d(logTag, "1번 그래프 Listener")
            if (webViewList.size < 10) {
                webViewList.add(xyzData)
            } else { // size > 10
                BleDebugLog.d(logTag, "webViewList.size: ${webViewList.size}")
                val jsonArrayString = Gson().toJson(webViewList)
                activity?.runOnUiThread { // 웹뷰 표시용 (UI 메인쓰레드에서)
                    binding.realWebView.loadUrl("javascript:addDataList($jsonArrayString)")
                    webViewList.clear()
                }
            }
        }

        bleViewModel.data2Listener = { xyzData ->
            BleDebugLog.d(logTag, "2번 그래프 Listener")
            val jsonXYZData = Gson().toJson(xyzData)
            activity?.runOnUiThread { // 웹뷰 표시용 (UI 메인쓰레드에서) Streaming 그래프
                binding.realWebView.loadUrl("javascript:fn_draw_next($jsonXYZData)")
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun startTimer() {
        BleDebugLog.i(logTag, "startTimer-()")
        timerTask = timer(period = 1000) {
            time ++
            BleDebugLog.d(logTag, "time: $time")

            val second = time % 60
            val minute = time / 60

            activity?.runOnUiThread {
                // 초
                binding.second.text = if (second < 10) ":0${second}"
                else ":${second}"
                // 분
                binding.minute.text = if (minute < 10) "0${minute}"
                else "$minute"
            }
        }
    }

    private fun stopTimer() {
        BleDebugLog.i(logTag, "stopTimer-()")
        timerTask?.cancel()
    }

    @SuppressLint("SetTextI18n")
    private fun resetTimer() {
        BleDebugLog.i(logTag, "resetTimer-()")
        timerTask?.cancel()

        time = 0
        binding.recordTextView.text = "When you're ready to record,\npress the record button."
    }

    private fun showDialog(title: String, subTitle: String) {
        val builder = AlertDialog.Builder(context).apply {
            setTitle(title)
            setMessage(subTitle)
            setPositiveButton("Action") { _, _ ->
                resetTimer()
            }
            setNegativeButton("Cancel") { _, _ ->
                resetTimer()
            }
        }
        builder.create().show()
    }

    override fun onDestroy() {
        BleDebugLog.i(logTag, "resetTimer-()")
        resetTimer()
        super.onDestroy()
    }
}