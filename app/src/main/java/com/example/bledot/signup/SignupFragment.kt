package com.example.bledot.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.bledot.R
import com.example.bledot.databinding.FragmentSignupBinding
import com.example.bledot.util.BleDebugLog


class SignupFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private val logTag = SignupFragment::class.simpleName
    private lateinit var binding: FragmentSignupBinding
    private val signupViewModel: SignupViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup, container, false)
        with(binding) {
            viewModel = signupViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BleDebugLog.i(logTag, "onViewCreated-()")
        BleDebugLog.d(logTag, "name: ${binding.name.infoInputEditText.text}")
        makeDropdownMenu()
    }

    private fun makeDropdownMenu() {
        // drop down 항목
        val raceSize = resources.getStringArray(R.array.race_array)
        val adapter = activity?.let { ArrayAdapter(it, android.R.layout.simple_spinner_item, raceSize) }
        adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.raceSpinner.spinner.adapter = adapter

        val countrySize = resources.getStringArray(R.array.country_array)
        val ctryAdapter = activity?.let { ArrayAdapter(it, android.R.layout.simple_spinner_item, countrySize) }
        adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.countrySpinner.spinner.adapter = ctryAdapter

        val weightSize = resources.getStringArray(R.array.weight_array)
        val weightAdapter = activity?.let { ArrayAdapter(it, android.R.layout.simple_spinner_item, weightSize) }
        adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.weightSpinner.spinner.adapter = weightAdapter

        val path = resources.getStringArray(R.array.path_array)
        val pathAdapter = activity?.let { ArrayAdapter(it, android.R.layout.simple_spinner_item, path) }
        adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.pathSpinner.spinner.adapter = pathAdapter
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) { }

    override fun onNothingSelected(p0: AdapterView<*>?) { }

}