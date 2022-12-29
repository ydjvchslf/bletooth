package com.example.bledot.realtime

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView.setWebContentsDebuggingEnabled
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

class RealtimeFragment : Fragment() {

    private val logTag = RealtimeFragment::class.simpleName
    private lateinit var binding: FragmentRealtimeBinding
    private val realtimeViewModel: RealtimeViewModel by activityViewModels()
    private val bleViewModel: BleViewModel by activityViewModels()
    // 웹뷰용 dataList
    var webViewList = ArrayList<XYZData>()

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
                    binding.realWebView.loadUrl("javascript:stopStreaming()")
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
            //realtimeViewModel.exportFile()
            val XYZData = XYZData(3.0, 2.0, 99.7)
            val jsonData = Gson().toJson(XYZData)
            binding.realWebView.loadUrl("javascript:addStreamingValue($jsonData)")
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
}