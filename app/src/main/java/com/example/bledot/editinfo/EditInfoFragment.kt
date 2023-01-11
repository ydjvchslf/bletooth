package com.example.bledot.editinfo

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
import com.example.bledot.databinding.FragmentEditInfoBinding
import com.example.bledot.util.BleDebugLog
import com.example.bledot.util.toolbarName

class EditInfoFragment : Fragment() {

    private val logTag = EditInfoFragment::class.simpleName
    private lateinit var binding: FragmentEditInfoBinding
    private val editInfoViewModel: EditInfoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        BleDebugLog.i(logTag, "onCreateView-()")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_info, container, false)
        with(binding) {
            viewModel = editInfoViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BleDebugLog.i(logTag, "onViewCreated-()")

        toolbarName.value = "User Info"
    }
}