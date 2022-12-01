package com.example.bledot.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import com.example.bledot.App
import com.example.bledot.R
import com.example.bledot.WebAppInterface
import com.example.bledot.ble.BleFragment
import com.example.bledot.ble.BleViewModel
import com.example.bledot.databinding.FragmentBleBinding
import com.example.bledot.databinding.FragmentDetailBinding
import com.example.bledot.util.BleDebugLog
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.math.log

class DetailFragment : Fragment() {

    private val logTag = DetailFragment::class.simpleName
    private lateinit var binding: FragmentDetailBinding
    private val detailViewModel: DetailViewModel by activityViewModels()
    private var timer: Timer? = null

    private var xyAxis = MutableLiveData<ArrayList<Int>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        BleDebugLog.i(logTag, "onCreateView-()")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)
        with(binding) {
            viewModel = detailViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BleDebugLog.i(logTag, "onViewCreated-()")

        binding.webView.apply {
            settings.javaScriptEnabled = true
            addJavascriptInterface(WebAppInterface(App.context()), "Android") // Android란 이름으로 js 인터페이스 설정
            loadUrl("file:///android_asset/sample.html")
        }

        binding.startBtn.setOnClickListener {
            startTime()
        }

        binding.stopBtn.setOnClickListener {
            stopTime()
        }

        xyAxis.observe(viewLifecycleOwner) {
            BleDebugLog.i(logTag, "xyAxis?.observe-()")
            BleDebugLog.d(logTag, "감지중!!!!! x: ${it[0]}, y: ${it[1]}")
            binding.webView.loadUrl("javascript:addData(${it[0]}, ${it[1]})")
        }
    }

    private fun startTime() {
        BleDebugLog.i(logTag, "startTime-()")
        timer = Timer()
        timer?.scheduleAtFixedRate(0, 1000) {
            //BleDebugLog.d(logTag, "${LocalDateTime.now()}")
            val arrayInt = createNum()
            BleDebugLog.d(logTag, "x=> ${arrayInt[0]}, y=> ${arrayInt[1]}")
            xyAxis.postValue(arrayInt)
            BleDebugLog.d(logTag, "xyAxis=> ${xyAxis.value}")
        }
    }

    private fun stopTime() {
        BleDebugLog.i(logTag, "stopTime-()")
        timer?.cancel()
    }

    private fun createNum(): ArrayList<Int> {
        val range = (-100..100)
        val x = range.random()
        val y = range.random()
        val arrayInt = ArrayList<Int>()
        arrayInt.add(x)
        arrayInt.add(y)
        return arrayInt
    }
}