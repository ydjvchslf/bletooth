package com.example.bledot.realtime

import android.R
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView.setWebContentsDebuggingEnabled
import android.widget.Toast
import androidx.core.graphics.toColorInt
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.bledot.App
import com.example.bledot.WebAppInterface
import com.example.bledot.ble.BleViewModel
import com.example.bledot.data.XYZData
import com.example.bledot.databinding.FragmentRealtimeBinding
import com.example.bledot.util.BleDebugLog
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
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
    // realTime chart
    private lateinit var chart: LineChart
    private lateinit var data: LineData

    private lateinit var set: LineDataSet
    private lateinit var set2: LineDataSet
    private lateinit var set3: LineDataSet

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        BleDebugLog.i(logTag, "onCreateView-()")
        binding = DataBindingUtil.inflate(inflater, com.example.bledot.R.layout.fragment_realtime, container, false)
        with(binding) {
            viewModel = realtimeViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        chart = binding.lineChart
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BleDebugLog.i(logTag, "onViewCreated-()")
        // 차트 기본 세팅
        settingRealtimeChart()
        // temp btn
        binding.tempBtn.setOnClickListener {
            activity?.runOnUiThread {
                // 난수 생성
                val random = (-20..20).random()  // -20 <= n <= 20
                addEntry(random.toDouble(), (random-10).toDouble(), (random+10).toDouble())
            }
        }
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
        binding.icStopBtn.setOnClickListener {
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

    private fun settingRealtimeChart() {
        BleDebugLog.i(logTag, "settingRealtimeChart-()")

        set = createSet()
        set2 = createSet2()
        set3 = createSet3()

        val dataSets = arrayListOf<ILineDataSet>()
        dataSets.add(set)
        dataSets.add(set2)
        dataSets.add(set3)

        data = LineData(dataSets)
        chart.data = data

        chart.apply {
            setDrawGridBackground(true)
            setGridBackgroundColor(Color.WHITE) // chart 배경색
            // description text
            description.isEnabled = true
            // touch gestures (false-비활성화)
            setTouchEnabled(false)
            // scaling and dragging (false-비활성화)
            isDragEnabled = false
            setScaleEnabled(false)
            //auto scale
            isAutoScaleMinMaxEnabled = false
            // if disabled, scaling can be done on x- and y-axis separately
            setPinchZoom(false)
        }

        //X축
        chart.xAxis.setDrawGridLines(true)
        chart.xAxis.setDrawAxisLine(false)

        chart.xAxis.isEnabled = true
        chart.xAxis.setDrawGridLines(false)

        chart.xAxis.textColor = Color.WHITE

        //Legend
        val l = chart.legend
        l.isEnabled = true
        l.formSize = 10f // set the size of the legend forms/shapes

        l.textSize = 12f
        l.textColor = Color.BLACK // label color

        //Y축
        val leftAxis = chart.axisLeft
        leftAxis.isEnabled = true
        leftAxis.textColor = Color.BLACK
        leftAxis.setDrawGridLines(true)
        leftAxis.gridColor = Color.BLACK

        val rightAxis = chart.axisRight
        rightAxis.isEnabled = false

        addEntry(0.0, 0.0, 0.0)

        // don't forget to refresh the drawing
        chart.invalidate()
    }

    private fun addEntry(num: Double, num2: Double, num3: Double) {
        BleDebugLog.i(logTag, "addEntry-()")
        BleDebugLog.d(logTag, "num: $num")
        BleDebugLog.d(logTag, "num2: $num2")

        val entry = Entry(set.entryCount.toFloat(), num.toFloat())
        val entry2 = Entry(set2.entryCount.toFloat(), num2.toFloat())
        val entry3 = Entry(set3.entryCount.toFloat(), num3.toFloat())

        data.addEntry(entry, 0)
        data.addEntry(entry2, 1)
        data.addEntry(entry3, 2)

        data.notifyDataChanged()

        // let the chart know it's data has changed
        chart.notifyDataSetChanged()
        chart.setVisibleXRangeMaximum(10.0f)
        // this automatically refreshes the chart (calls invalidate())
        chart.moveViewTo(data.entryCount.toFloat(), 50f, YAxis.AxisDependency.LEFT)
    }

    private fun createSet(): LineDataSet {
        val set = LineDataSet(null, "Roll") // X
        set.lineWidth = 1f
        set.setDrawValues(false)
        set.valueTextColor = Color.rgb(243,101,75)
        set.color = Color.rgb(243,101,75)
        set.mode = LineDataSet.Mode.CUBIC_BEZIER
        set.setDrawCircles(false)
        //set.highLightColor = Color.rgb(190, 190, 190)
        return set
    }

    private fun createSet2(): LineDataSet {
        val set = LineDataSet(null, "Pitch") // Y
        set.lineWidth = 1f
        set.setDrawValues(false)
        set.valueTextColor = Color.rgb(91,209,178)
        set.color = Color.rgb(91,209,178)
        set.mode = LineDataSet.Mode.CUBIC_BEZIER
        set.setDrawCircles(false)
        //set.highLightColor = Color.rgb(190, 190, 190)
        return set
    }

    private fun createSet3(): LineDataSet {
        val set = LineDataSet(null, "Yaw") // Z
        set.lineWidth = 1f
        set.setDrawValues(false)
        set.valueTextColor = Color.rgb(23,145,253)
        set.color = Color.rgb(23,145,253)
        set.mode = LineDataSet.Mode.CUBIC_BEZIER
        set.setDrawCircles(false)
        //set.highLightColor = Color.rgb(255, 255, 0)
        return set
    }

    override fun onDestroy() {
        BleDebugLog.i(logTag, "resetTimer-()")
        resetTimer()
        super.onDestroy()
    }
}