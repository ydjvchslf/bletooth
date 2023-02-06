package com.example.bledot.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.example.bledot.R
import com.example.bledot.changepw.ChangePwFragmentDirections
import com.example.bledot.config.ConfigFragment
import com.example.bledot.config.ConfigViewModel
import com.example.bledot.databinding.FragmentConfigBinding
import com.example.bledot.databinding.FragmentHomeBinding
import com.example.bledot.util.BleDebugLog

class HomeFragment : Fragment() {

    private val logTag = HomeFragment::class.simpleName
    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        BleDebugLog.i(logTag, "onCreateView-()")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        with(binding) {
            viewModel = homeViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BleDebugLog.i(logTag, "onViewCreated-()")
        // 버튼 > 네비게이션
        binding.listBtn.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(HomeFragmentDirections.actionHomeFragmentToListFragment())
        }
        binding.deviceBtn.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(HomeFragmentDirections.actionHomeFragmentToBleFragment())
        }
        binding.recordBtn.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(HomeFragmentDirections.actionHomeFragmentToRealtimeFragment())
        }
        binding.settingBtn.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(HomeFragmentDirections.actionHomeFragmentToConfigFragment())
        }
    }
}