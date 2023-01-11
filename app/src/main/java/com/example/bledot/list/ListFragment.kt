package com.example.bledot.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.bledot.R
import com.example.bledot.databinding.FragmentListBinding
import com.example.bledot.util.BleDebugLog

class ListFragment : Fragment() {

    private val logTag = ListFragment::class.simpleName
    private lateinit var binding: FragmentListBinding
    private val listViewModel: ListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        BleDebugLog.i(logTag, "onCreateView-()")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false)
        with(binding) {
            viewModel = listViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }
}