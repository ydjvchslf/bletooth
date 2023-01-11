package com.example.bledot.changepw

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.bledot.R
import com.example.bledot.databinding.FragmentChangePwBinding
import com.example.bledot.util.BleDebugLog
import com.example.bledot.util.toolbarName

class ChangePwFragment : Fragment() {

    private val logTag = ChangePwFragment::class.simpleName
    private lateinit var binding: FragmentChangePwBinding
    private val changePwViewModel: ChangePwViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        BleDebugLog.i(logTag, "onCreateView-()")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_change_pw, container, false)
        with(binding) {
            viewModel = changePwViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BleDebugLog.i(logTag, "onViewCreated-()")

        toolbarName.value = "Change PW"
    }
}