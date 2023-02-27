package com.example.bledot.notupload

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.bledot.R
import com.example.bledot.databinding.FragmentNotUploadedBinding
import com.example.bledot.util.BleDebugLog

class NotuploadFragment: Fragment() {

    private val logTag = NotuploadFragment::class.simpleName
    private lateinit var binding: FragmentNotUploadedBinding
    private val notuploadViewModel: NotuploadViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        BleDebugLog.i(logTag, "onCreateView-()")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_not_uploaded, container, false)
        with(binding) {
            viewModel = notuploadViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BleDebugLog.i(logTag, "onViewCreated-()")
    }
}