package com.example.bledot.config

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.bledot.R
import com.example.bledot.databinding.FragmentConfigBinding
import com.example.bledot.util.BleDebugLog

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

        binding.getBtn.setOnClickListener {
            configViewModel.getProductList {
                if (it == 200) {
                    Toast.makeText(context, "200 OK", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.loginBtn.setOnClickListener {
            configViewModel.login { retCode, userInfo ->
                if (retCode == 200) {
                    Toast.makeText(context, "로그인 성공!", Toast.LENGTH_SHORT).show()
                    binding.textView.text = userInfo.toString()
                }
                if (retCode == 5555) {
                    Toast.makeText(context, "계정 틀림, 로그인 에러!!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}