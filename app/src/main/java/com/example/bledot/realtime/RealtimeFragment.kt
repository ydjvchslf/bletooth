package com.example.bledot.realtime

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView.setWebContentsDebuggingEnabled
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.bledot.App
import com.example.bledot.WebAppInterface
import com.example.bledot.ble.BleViewModel
import com.example.bledot.data.XYZData
import com.example.bledot.databinding.FragmentRealtimeBinding
import com.example.bledot.signup.SignupFragmentArgs
import com.example.bledot.util.BleDebugLog
import com.example.bledot.util.appIsWorking
import com.example.bledot.util.isWearingOption
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.gson.Gson
import java.io.File
import java.util.*
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
    private lateinit var chart2: LineChart
    private lateinit var chart3: LineChart

    private lateinit var data: LineData
    private lateinit var data2: LineData
    private lateinit var data3: LineData

    private lateinit var set: LineDataSet
    private lateinit var set2: LineDataSet
    private lateinit var set3: LineDataSet

    private var isZeroing = false
    private var momentData: XYZData? = null

    private var index = 1

    // MWM 데이터
    private val arg: RealtimeFragmentArgs by navArgs()

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
        chart2 = binding.lineChart2
        chart3 = binding.lineChart3
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BleDebugLog.i(logTag, "onViewCreated-()")
        // MWM 데이터 할당
        assignMWMData()
        // 웹뷰 스트롤 막기
        binding.realWebView.setOnTouchListener { _, _ -> true }
        // 차트 기본 세팅
        settingRealtimeChart()
        // 기기 연결 확인
        checkConnection()
        // 외장 메모리 용량 체크
        checkExternalStorage()
        // 작업 중 화면 터치 불가
        appIsWorking.observe(viewLifecycleOwner) { isWorking ->
            preventTouchEvent(isWorking)
        }
        // temp btn
        binding.tempBtn.setOnClickListener {
            activity?.runOnUiThread {
                // 난수 생성
                val random = (-20..20).random()  // -20 <= n <= 20
                addEntry(random.toDouble(), (random-10).toDouble(), (random+10).toDouble())
            }
        }
        // toggle 기본 세팅
        settingWearingOption()
        // toggle btn
        binding.toggleBtn.setOnClickListener {
            val isToggleValue = binding.toggleBtn.isChecked
            BleDebugLog.d(logTag, "isToggleValue: $isToggleValue")
            isWearingOption.value = isToggleValue

            if (isToggleValue) {
                Navigation.findNavController(binding.root).navigate(RealtimeFragmentDirections.actionRealtimeFragmentToListFragment())
            } else {
                isWearingOption.value = isToggleValue
            }
        }
        // recording start btn
        binding.start.setOnClickListener {
            BleDebugLog.i(logTag, "녹화 Start Clicked-()")

            if (bleViewModel.mConnectedXsDevice.value == null) {
                Toast.makeText(App.context(), "Please connect the device first.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            realtimeViewModel.isRecording.value = true
            BleDebugLog.i(logTag, "isRecording: ${realtimeViewModel.isRecording.value}")
            binding.realWebView.loadUrl("javascript:clearChart()")
            startTimer()
            bleViewModel.mConnectedXsDevice.value?.let {
                realtimeViewModel.createFile()
                bleViewModel.isRecording = true
            }
        }
        // recording stop btn
        binding.stop.setOnClickListener {
            BleDebugLog.i(logTag, "녹화 Stop Clicked-()")

            if (bleViewModel.mConnectedXsDevice.value == null) {
                Toast.makeText(App.context(), "Please connect the device first.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            realtimeViewModel.isRecording.value = false
            bleViewModel.isRecording = false
            BleDebugLog.i(logTag, "isRecording: ${realtimeViewModel.isRecording.value}")
            stopTimer()
            bleViewModel.mConnectedXsDevice.value?.let {
                bleViewModel.stopMeasure(it)
                index = 1
            }

            // 현재 저장한 파일명 확실히 있는지 체크 후 dialog 에 표출하기
            val isExist = checkCurrentData(realtimeViewModel.fileFullName)
            if (isExist) {
                showDialog("New Data", "${realtimeViewModel.filename}\nDo you want to upload to the server?")
            }
        }
        // zeroing
        binding.zeroing.setOnClickListener {
            when (bleViewModel.mConnectedXsDevice.value) {
                null -> {
                    Toast.makeText(App.context(), "Please connect the device first.", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    changeToZeroed()
                    momentData = bleViewModel.makeResetZero()
                    isZeroing = true
                }
            }
        }
//        binding.fileSaveBtn.setOnClickListener { // 임시
//            realtimeViewModel.createFile2()
//        }
//        binding.fileCloseBtn.setOnClickListener { // 임시
//            //realtimeViewModel.closeFiles()
//        }
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
            // zeroing 데이터 가공
            val afterXYZData = XYZData(
                xValue = xyzData.xValue - (momentData?.xValue ?: 0.0),
                yValue = xyzData.yValue - (momentData?.yValue ?: 0.0),
                zValue = xyzData.zValue - (momentData?.zValue ?: 0.0)
            )

            if (webViewList.size < 10) {
                if (isZeroing) {
                    webViewList.add(afterXYZData)
                } else {
                    webViewList.add(afterXYZData)
                }
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
            // zeroing 데이터 가공
            val afterXYZData = XYZData(
                xValue = xyzData.xValue - (momentData?.xValue ?: 0.0),
                yValue = xyzData.yValue - (momentData?.yValue ?: 0.0),
                zValue = xyzData.zValue - (momentData?.zValue ?: 0.0)
            )

            if (isZeroing) {
                activity?.runOnUiThread {
                    val xValue = afterXYZData.xValue
                    val yValue = afterXYZData.yValue
                    val zValue = afterXYZData.zValue
                    addEntry(xValue, yValue, zValue)
                }
            } else {
                activity?.runOnUiThread {
                    val xValue = xyzData.xValue
                    val yValue = xyzData.yValue
                    val zValue = xyzData.zValue
                    addEntry(xValue, yValue, zValue)
                }
            }

            // data 파일에 업데이트
            // 녹화 시작일 때만
            if (realtimeViewModel.isRecording.value == true) {
                realtimeViewModel.updateFiles(afterXYZData, index)
                index ++
            }
        }
//        binding.writeBtn.setOnClickListener { // 임시
//            val afterXYZData = XYZData(33.4444444, 22.3333, -44.693475374)
//            realtimeViewModel.updateFiles(afterXYZData, index)
//            index ++
//        }
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
        //binding.recordTextView.text = "When you're ready to record,\npress the record button."
    }

    private fun showDialog(title: String, subTitle: String) {
        val builder = AlertDialog.Builder(context).apply {
            setTitle(title)
            setMessage(subTitle)
            setCancelable(false)
            setPositiveButton("Action") { _, _ ->
                resetTimer()
                //showCompleteDialog("Complete", "The data uploaded to the server.")
                // TODO:: 서버 업로드
                realtimeViewModel.uploadToServer {
                    if (it) {
                        showCompleteDialog("Complete", "The data uploaded to the server.")
                    } else {
                        showCompleteDialog("Warning", "The data failed to upload to the server.")
                    }
                }
            }
            setNegativeButton("Cancel") { _, _ ->
                resetTimer()
                // 초기화
                binding.realWebView.loadUrl("javascript:clearChart()")
                checkConnection()
            }
        }
        builder.create().show()
    }

    private fun showCompleteDialog(title: String, subTitle: String) {
        val builder = AlertDialog.Builder(context).apply {
            setTitle(title)
            setMessage(subTitle)
            setCancelable(false)
            setPositiveButton("Action") { _, _ ->
                // TODO:: 초기화
                binding.realWebView.loadUrl("javascript:clearChart()")
                checkConnection()
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
        val dataSets2 = arrayListOf<ILineDataSet>()
        val dataSets3 = arrayListOf<ILineDataSet>()

        dataSets.add(set)
        dataSets2.add(set2)
        dataSets3.add(set3)

        data = LineData(dataSets)
        data2 = LineData(dataSets2)
        data3 = LineData(dataSets3)

        chart.data = data
        chart2.data = data2
        chart3.data = data3

        ///////////////////////////// chart1
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
        chart.xAxis.apply {
            setDrawGridLines(false)
            setDrawAxisLine(false)
            isEnabled = true
            textColor = Color.WHITE
        }

        chart.legend.apply {
            isEnabled = true
            formSize = 10f // set the size of the legend forms/shapes
            textSize = 12f
            textColor = Color.BLACK
        }

        //Y축
        chart.axisLeft.apply {
            isEnabled = true
            textColor = Color.BLACK
            setDrawGridLines(true)
            gridColor = Color.BLACK
        }

        chart.axisRight.apply {
            isEnabled = false
        }

        /////////////////////// chart2
        chart2.apply {
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
        chart2.xAxis.apply {
            setDrawGridLines(false)
            setDrawAxisLine(false)
            isEnabled = true
            textColor = Color.WHITE
        }

        chart2.legend.apply {
            isEnabled = true
            formSize = 10f // set the size of the legend forms/shapes
            textSize = 12f
            textColor = Color.BLACK
        }

        //Y축
        chart2.axisLeft.apply {
            isEnabled = true
            textColor = Color.BLACK
            setDrawGridLines(true)
            gridColor = Color.BLACK
        }

        chart2.axisRight.apply {
            isEnabled = false
        }

        /////////////////////////chart3
        chart3.apply {
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
        chart3.xAxis.apply {
            setDrawGridLines(false)
            setDrawAxisLine(false)
            isEnabled = true
            textColor = Color.WHITE
        }

        chart3.legend.apply {
            isEnabled = true
            formSize = 10f // set the size of the legend forms/shapes
            textSize = 12f
            textColor = Color.BLACK
        }

        //Y축
        chart3.axisLeft.apply {
            isEnabled = true
            textColor = Color.BLACK
            setDrawGridLines(true)
            gridColor = Color.BLACK
        }

        chart3.axisRight.apply {
            isEnabled = false
        }

        /////===================/////
        addEntry(0.0, 0.0, 0.0)
        // don't forget to refresh the drawing
        chart.invalidate()
        chart2.invalidate()
        chart3.invalidate()
        /////===================/////

    }

    private fun addEntry(num: Double, num2: Double, num3: Double) {
        BleDebugLog.i(logTag, "addEntry-()")

        val entry = Entry(set.entryCount.toFloat(), num.toFloat())
        val entry2 = Entry(set2.entryCount.toFloat(), num2.toFloat())
        val entry3 = Entry(set3.entryCount.toFloat(), num3.toFloat())

        data.addEntry(entry, 0)
        data2.addEntry(entry2, 0)
        data3.addEntry(entry3, 0)

        data.notifyDataChanged()
        data2.notifyDataChanged()
        data3.notifyDataChanged()

        // let the chart know it's data has changed
        chart.notifyDataSetChanged()
        chart2.notifyDataSetChanged()
        chart3.notifyDataSetChanged()

        chart.setVisibleXRangeMaximum(1000.0f)
        chart2.setVisibleXRangeMaximum(1000.0f)
        chart3.setVisibleXRangeMaximum(1000.0f)
        // this automatically refreshes the chart (calls invalidate())
        chart.moveViewTo(data.entryCount.toFloat(), 50f, YAxis.AxisDependency.LEFT)
        chart2.moveViewTo(data2.entryCount.toFloat(), 50f, YAxis.AxisDependency.LEFT)
        chart3.moveViewTo(data3.entryCount.toFloat(), 50f, YAxis.AxisDependency.LEFT)
    }

    private fun createSet(): LineDataSet {
        val set = LineDataSet(null, "X") // X Roll
        set.lineWidth = 1.2f
        set.setDrawValues(false)
        set.valueTextColor = Color.rgb(243,101,75)
        set.color = Color.rgb(243,101,75)
        set.mode = LineDataSet.Mode.CUBIC_BEZIER
        set.setDrawCircles(false)
        //set.highLightColor = Color.rgb(190, 190, 190)
        return set
    }

    private fun createSet2(): LineDataSet {
        val set = LineDataSet(null, "Y") // Y Pitch
        set.lineWidth = 1.2f
        set.setDrawValues(false)
        set.valueTextColor = Color.rgb(91,209,178)
        set.color = Color.rgb(91,209,178)
        set.mode = LineDataSet.Mode.CUBIC_BEZIER
        set.setDrawCircles(false)
        //set.highLightColor = Color.rgb(190, 190, 190)
        return set
    }

    private fun createSet3(): LineDataSet {
        val set = LineDataSet(null, "Z") // Z Yaw
        set.lineWidth = 1.2f
        set.setDrawValues(false)
        set.valueTextColor = Color.rgb(23,145,253)
        set.color = Color.rgb(23,145,253)
        set.mode = LineDataSet.Mode.CUBIC_BEZIER
        set.setDrawCircles(false)
        //set.highLightColor = Color.rgb(255, 255, 0)
        return set
    }

    private fun checkConnection() {
        BleDebugLog.i(logTag, "checkConnection-()")
        val device = bleViewModel.mConnectedXsDevice.value// 0 미연결, 2 연결
        BleDebugLog.d(logTag, "device: $device")

        device?.let {
            if (it.connectionState == 2) {
                BleDebugLog.i(logTag, "Bluetooth 연결 중")
                bleViewModel.startMeasure(it)
            }
        }
    }

    private fun checkCurrentData(pathAndName: String): Boolean {
        BleDebugLog.i(logTag, "checkCurrentData-()")
        val file = File(pathAndName)
        return file.exists()
    }

    private fun checkExternalStorage() {
        realtimeViewModel.getExternalMemory { isLimit ->
            if (isLimit == true) {
                showDialogComplete("Warning", "Not enough phone storage space.")
            }
        }
    }

    private fun preventTouchEvent(isWorking: Boolean) {
        BleDebugLog.i(logTag, "preventTouchEvent-()")
        if (isWorking) {
            binding.zeroing.isEnabled = false
            binding.toggleBtn.isEnabled = false
            binding.recordBtn.isEnabled = false
        } else {
            binding.zeroing.isEnabled = true
            binding.toggleBtn.isEnabled = true
            binding.recordBtn.isEnabled = true
        }
    }

    private fun showDialogComplete(title: String, subTitle: String) {
        val builder = AlertDialog.Builder(context).apply {
            setTitle(title)
            setMessage(subTitle)
            setCancelable(false)
            setPositiveButton("Action") { _, _ -> }
        }
        builder.create().show()
    }

    private fun toAfterDate(before: XYZData) {
        BleDebugLog.i(logTag, "toAfterDate-()")
        BleDebugLog.d(logTag, "before: $before")
        val beforeX = before.xValue
        val beforeY = before.yValue
        val beforeZ = before.zValue

    }

    private fun changeToZeroed() {
        BleDebugLog.i(logTag, "changeToZeroed-()")
        binding.zeroing.text = "zeroed"
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                // Do your actual work here
                binding.zeroing.text = "zero"
            }
        }, 2000L)
    }

    private fun settingWearingOption() {
        BleDebugLog.i(logTag, "settingWearingOption-()")
        if (isWearingOption.value == true) {
            binding.toggleBtn.isChecked = true
        }
    }

    private fun assignMWMData() {
        BleDebugLog.i(logTag, "assignMWMData-()")
        arg.webViewInfo?.let {
            realtimeViewModel.webViewData = it
            binding.toggleBtn.isChecked = true
        }
    }

    override fun onDestroy() {
        BleDebugLog.i(logTag, "onDestroy-()")
        stopTimer()
        resetTimer()
        super.onDestroy()
    }
}