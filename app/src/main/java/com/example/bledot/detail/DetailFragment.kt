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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BleDebugLog.i(logTag, "onViewCreated-()")

        binding.getBtn.setOnClickListener {
            detailViewModel.getProductList()
        }
    }
}