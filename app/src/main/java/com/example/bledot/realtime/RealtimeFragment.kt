package com.example.bledot.realtime

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.bledot.App
import com.example.bledot.R
import com.example.bledot.ble.BleViewModel
import com.example.bledot.databinding.FragmentRealtimeBinding
import com.example.bledot.util.BleDebugLog

class RealtimeFragment : Fragment() {

    private val logTag = RealtimeFragment::class.simpleName
    private lateinit var binding: FragmentRealtimeBinding
    private val realtimeViewModel: RealtimeViewModel by activityViewModels()
    private val bleViewModel: BleViewModel by activityViewModels()

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
        // Logger btn // TODO:: 수정 필요
        binding.loggerBtn.setOnClickListener {
            realtimeViewModel.createLogger()
        }
        binding.fileSaveBtn.setOnClickListener {
            realtimeViewModel.createFile(bleViewModel.mConnectedXsDevice.value!!)
        }
    }
}