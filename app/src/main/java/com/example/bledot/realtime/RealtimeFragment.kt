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
        // 기록 stop
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
    }
}