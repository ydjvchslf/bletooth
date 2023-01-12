package com.example.bledot.config

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.bledot.R
import com.example.bledot.databinding.FragmentConfigBinding
import com.example.bledot.util.BleDebugLog
import com.example.bledot.util.toolbarName

class ConfigFragment : Fragment() {

    private val logTag = ConfigFragment::class.simpleName
    private lateinit var binding: FragmentConfigBinding
    private val configViewModel: ConfigViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        BleDebugLog.i(logTag, "onCreateView-()")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_config, container, false)
        with(binding) {
            viewModel = configViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BleDebugLog.i(logTag, "onViewCreated-()")

        toolbarName.value = "Config"

        binding.getBtn.setOnClickListener {
            configViewModel.getProductList {
                if (it == 200) {
                    Toast.makeText(context, "200 OK", Toast.LENGTH_SHORT).show()
                }
            }
        }
        // edit info 버튼
        binding.editInfoBtn.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(ConfigFragmentDirections.actionConfigFragmentToEditInfoFragment())
        }
        // change pw 버튼
        binding.changePwBtn.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.changePwFragment)
        }
    }
}