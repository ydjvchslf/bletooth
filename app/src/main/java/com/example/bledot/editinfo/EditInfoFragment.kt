package com.example.bledot.editinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.bledot.R
import com.example.bledot.data.UserInfoEntity
import com.example.bledot.databinding.FragmentEditInfoBinding
import com.example.bledot.signup.SignupFragmentDirections
import com.example.bledot.util.*

class EditInfoFragment : Fragment(), AdapterView.OnItemSelectedListener {

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
        makeDropdownMenu()
        //settingCrtUserInfo(arg)
        getUserInfo()

        binding.backBtn.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(EditInfoFragmentDirections.actionEditInfoFragmentToConfigFragment())
        }
        binding.saveBtn.setOnClickListener {
            processEditInfo()
        }
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

    private fun getUserInfo() {
        BleDebugLog.i(logTag, "getUserInfo-()")
        editInfoViewModel.getUserFromServer { userInfo ->
            BleDebugLog.d(logTag, "정상적으로 userInfo 가져왔음")
            userInfo?.let { settingCrtUserInfo(it) }
        }
    }
    //TODO :: 기존 유저 정보 세팅, 일부만 완료 (spinner, 진단 날짜 빠짐, 디자인 나온 이후 할 것)
    private fun settingCrtUserInfo(userInfo: UserInfoEntity) {
        BleDebugLog.i(logTag, "settingCrtUserInfo-()")

        binding.name.infoInputEditText.setText(userInfo.name)
        binding.birth.infoInputEditText.setText(userInfo.birth)
        binding.weightSpinner.weightEditText.setText(userInfo.weight)
        binding.phone.infoInputEditText.setText(userInfo.phone)
        binding.address.address1.setText(userInfo.address1)
        binding.address.address2.setText(userInfo.address2)
        binding.address.address3.setText(userInfo.address3)
        binding.address.address4.setText(userInfo.zipCode)
    }

    private fun processEditInfo() {
        BleDebugLog.i(logTag, "processEditInfo-()")
        // 유저가 입력한 정보 모두 가져오기
        val name = binding.name.infoInputEditText.text.toString()
        val birth = binding.birth.infoInputEditText.text.toString()
        val gender = if (binding.gender.femaleBtn.isChecked) 0 else 1
        val weight = binding.weightSpinner.weightEditText.text.toString()
        val weightUnit = if (binding.weightSpinner.spinner.selectedItem.toString() == "kg") 0 else 1
        val race = getRace(binding.raceSpinner.spinner.selectedItem.toString()) // 0, 1, 2, 3, 4, 5, 6
        val pathology = getPathology(binding.pathSpinner.spinner.selectedItem.toString())
        val phone = binding.phone.infoInputEditText.text.toString()
        val address1 = binding.address.address1.text.toString()
        val address2 = binding.address.address2.text.toString()
        val address3 = binding.address.address3.text.toString()
        val address4 = binding.address.address4.text.toString()
        val country = getCountry(binding.countrySpinner.spinner.selectedItem.toString())
        // TODO:: UserInfoEntity 세팅
        editInfoViewModel.editUserInfo { isEdited ->
            if (isEdited) {
                // TODO:: 수정 완료 후 화면 처리
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        BleDebugLog.i(logTag, "onDestroy-()")
    }
}