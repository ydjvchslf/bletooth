package com.example.bledot.editinfo

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.example.bledot.App
import com.example.bledot.R
import com.example.bledot.data.UserInfoEntity
import com.example.bledot.databinding.FragmentEditInfoBinding
import com.example.bledot.util.BleDebugLog
import com.example.bledot.util.REG
import com.example.bledot.util.getPathology
import com.example.bledot.util.getRace
import java.util.regex.Pattern


class EditInfoFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private val logTag = EditInfoFragment::class.simpleName
    private lateinit var binding: FragmentEditInfoBinding
    private val editInfoViewModel: EditInfoViewModel by activityViewModels()
    // 회원정보 수정시 필요한 flag
    private var nameFlag = false
    private var dateFlag = false
    private var weightFlag = false
    private var diagnosisFlag = false
    private var phoneFlag = false
    private var addressFlag = false

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
        getUserInfo()

        // 필드 형식 안맞을 시 빨간 테두리 & 문구
        checkNameGuide()
        checkDateGuide()
        checkWeightGuide()
        checkPhoneGuide()
        checkAddressGuide()
        checkDiagnosisGuide()

        binding.backBtn.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(EditInfoFragmentDirections.actionEditInfoFragmentToConfigFragment())
        }
        binding.saveBtn.setOnClickListener {
            checkAllFlags()
            //processEditInfo()
        }
    }

    private fun makeDropdownMenu() {
        // drop down 항목
        val raceSize = resources.getStringArray(R.array.race_array)
        val adapter = activity?.let { ArrayAdapter(it, android.R.layout.simple_spinner_item, raceSize) }
        adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.raceSpinner.spinner.adapter = adapter

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

    private fun checkNameGuide() {
        binding.name.infoInputEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.name.infoInputEditText.text.length > 30) {
                    binding.nameStateTextView.visibility = View.VISIBLE
                    binding.name.infoInputEditText.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.red_edittext_rounded_corner_rectangle, null)
                    nameFlag = false
                }
                else if (binding.name.infoInputEditText.text.isEmpty()) {
                    binding.nameStateTextView.visibility = View.VISIBLE
                    binding.name.infoInputEditText.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.red_edittext_rounded_corner_rectangle, null)
                    nameFlag = false
                }
                else if (binding.name.infoInputEditText.text.length < 31){
                    binding.nameStateTextView.visibility = View.INVISIBLE
                    binding.name.infoInputEditText.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.edittext_rounded_corner_rectangle, null)
                    nameFlag = true
                }
            }
            override fun afterTextChanged(p0: Editable?) { }
        })
    }

    private fun checkDateGuide() {
        binding.birth.infoInputEditText.inputType = InputType.TYPE_CLASS_NUMBER
        binding.birth.infoInputEditText.filters = arrayOf(InputFilter.LengthFilter(10))

        binding.birth.infoInputEditText.addTextChangedListener(object: TextWatcher{
            @SuppressLint("SetTextI18n")
            override fun onTextChanged(p0: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.birth.infoInputEditText.isFocusable && p0.toString() != "") {
                    val textlength = binding.birth.infoInputEditText.text.toString().length
                    if (textlength == 4 && before != 1) {
                        binding.birth.infoInputEditText.setText(binding.birth.infoInputEditText.text.toString() + "/")
                        binding.birth.infoInputEditText.setSelection(binding.birth.infoInputEditText.text.length)
                        showBirthError()
                    } else if (textlength == 7 && before != 1) {
                        binding.birth.infoInputEditText.setText(binding.birth.infoInputEditText.text.toString() + "/")
                        binding.birth.infoInputEditText.setSelection(binding.birth.infoInputEditText.text.length)
                        showBirthError()
                    } else if (textlength == 5 && !binding.birth.infoInputEditText.text.toString().contains("/")) {
                        binding.birth.infoInputEditText.setText(binding.birth.infoInputEditText.text.toString()
                            .substring(0, 4) + "/" + binding.birth.infoInputEditText.text.toString()
                            .substring(4))
                        binding.birth.infoInputEditText.setSelection(binding.birth.infoInputEditText.text.length)
                        showBirthError()
                    } else if (textlength == 8 && binding.birth.infoInputEditText.text.toString().substring(7, 8) != "/") {
                        binding.birth.infoInputEditText.setText(
                            binding.birth.infoInputEditText.text.toString()
                                .substring(0, 7) + "/" + binding.birth.infoInputEditText.text.toString()
                                .substring(7))
                        binding.birth.infoInputEditText.setSelection(binding.birth.infoInputEditText.text.length)
                        showBirthError()
                    } else if (textlength != 10) {
                        showBirthError()
                    }
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun afterTextChanged(p0: Editable?) {
                val inputText = binding.birth.infoInputEditText.text.toString()
                val result = Pattern.matches(REG, inputText)
                if(result) {
                    binding.birthStateTextView.visibility = View.INVISIBLE
                    binding.birth.infoInputEditText.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.edittext_rounded_corner_rectangle, null)
                    dateFlag = true
                }
            }
        })
    }

    private fun checkDiagnosisGuide() {
        binding.diagDate.infoInputEditText.inputType = InputType.TYPE_CLASS_NUMBER
        binding.diagDate.infoInputEditText.filters = arrayOf(InputFilter.LengthFilter(10))

        binding.diagDate.infoInputEditText.addTextChangedListener(object: TextWatcher{
            @SuppressLint("SetTextI18n")
            override fun onTextChanged(p0: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.diagDate.infoInputEditText.isFocusable && p0.toString() != "") {
                    val textlength = binding.diagDate.infoInputEditText.text.toString().length
                    if (textlength == 4 && before != 1) {
                        binding.diagDate.infoInputEditText.setText(binding.diagDate.infoInputEditText.text.toString() + "/")
                        binding.diagDate.infoInputEditText.setSelection(binding.diagDate.infoInputEditText.text.length)
                        showDiagnosisError()
                    } else if (textlength == 7 && before != 1) {
                        binding.diagDate.infoInputEditText.setText(binding.diagDate.infoInputEditText.text.toString() + "/")
                        binding.diagDate.infoInputEditText.setSelection(binding.diagDate.infoInputEditText.text.length)
                        showDiagnosisError()
                    } else if (textlength == 5 && !binding.diagDate.infoInputEditText.text.toString().contains("/")) {
                        binding.diagDate.infoInputEditText.setText(binding.diagDate.infoInputEditText.text.toString()
                            .substring(0, 4) + "/" + binding.diagDate.infoInputEditText.text.toString()
                            .substring(4))
                        binding.diagDate.infoInputEditText.setSelection(binding.diagDate.infoInputEditText.text.length)
                        showDiagnosisError()
                    } else if (textlength == 8 && binding.diagDate.infoInputEditText.text.toString().substring(7, 8) != "/") {
                        binding.diagDate.infoInputEditText.setText(
                            binding.diagDate.infoInputEditText.text.toString()
                                .substring(0, 7) + "/" + binding.diagDate.infoInputEditText.text.toString()
                                .substring(7))
                        binding.diagDate.infoInputEditText.setSelection(binding.diagDate.infoInputEditText.text.length)
                        showDiagnosisError()
                    } else if (textlength != 10) {
                        showDiagnosisError()
                    }
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun afterTextChanged(p0: Editable?) {
                val inputText = binding.diagDate.infoInputEditText.text.toString()
                val result = Pattern.matches(REG, inputText)
                if(result) {
                    binding.diagStateTextView.visibility = View.INVISIBLE
                    binding.diagDate.infoInputEditText.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.edittext_rounded_corner_rectangle, null)
                    diagnosisFlag = true
                }
            }
        })
    }

    private fun checkWeightGuide() {
        binding.weightSpinner.weightEditText.inputType = InputType.TYPE_CLASS_NUMBER
        binding.weightSpinner.weightEditText.filters = arrayOf(InputFilter.LengthFilter(3))
        binding.weightSpinner.weightEditText.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.weightSpinner.weightEditText.text.isEmpty()) {
                    binding.weightStateTextView.visibility = View.VISIBLE
                    binding.weightSpinner.weightEditText.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.red_edittext_rounded_corner_rectangle, null)
                    weightFlag = false
                } else {
                    binding.weightStateTextView.visibility = View.INVISIBLE
                    binding.weightSpinner.weightEditText.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.edittext_rounded_corner_rectangle, null)
                    weightFlag = true
                }
            }
        })
    }

    private fun checkPhoneGuide() {
        binding.phone.infoInputEditText.inputType = InputType.TYPE_CLASS_NUMBER
        binding.phone.infoInputEditText.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.phone.infoInputEditText.text.isEmpty()) {
                    binding.phoneNotTextView.visibility = View.VISIBLE
                    binding.phone.infoInputEditText.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.red_edittext_rounded_corner_rectangle, null)
                    phoneFlag = false
                } else {
                    binding.phoneNotTextView.visibility = View.INVISIBLE
                    binding.phone.infoInputEditText.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.edittext_rounded_corner_rectangle, null)
                    phoneFlag = true
                }
            }
        })
    }

    private fun checkAddressGuide() {
        binding.address.address1.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.address.address1.text.isEmpty()) {
                    binding.address.addressNotTextView.visibility = View.VISIBLE
                    binding.address.address1.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.red_edittext_rounded_corner_rectangle, null)
                    addressFlag = false
                } else {
                    binding.address.addressNotTextView.visibility = View.INVISIBLE
                    binding.address.address1.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.edittext_rounded_corner_rectangle, null)
                }
            }
        })

        binding.address.address2.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.address.address2.text.isEmpty()) {
                    binding.address.addressNotTextView.visibility = View.VISIBLE
                    binding.address.address2.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.red_edittext_rounded_corner_rectangle, null)
                    addressFlag = false
                } else {
                    binding.address.addressNotTextView.visibility = View.INVISIBLE
                    binding.address.address2.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.edittext_rounded_corner_rectangle, null)
                }
            }
        })

        binding.address.address3.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.address.address3.text.isEmpty()) {
                    binding.address.addressNotTextView.visibility = View.VISIBLE
                    binding.address.address3.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.red_edittext_rounded_corner_rectangle, null)
                    addressFlag = false
                } else {
                    binding.address.addressNotTextView.visibility = View.INVISIBLE
                    binding.address.address3.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.edittext_rounded_corner_rectangle, null)
                }
            }
        })

        binding.address.address4.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.address.address4.text.isEmpty()) {
                    binding.address.addressNotTextView.visibility = View.VISIBLE
                    binding.address.address4.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.red_edittext_rounded_corner_rectangle, null)
                    addressFlag = false
                } else {
                    binding.address.addressNotTextView.visibility = View.INVISIBLE
                    binding.address.address4.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.edittext_rounded_corner_rectangle, null)
                    addressFlag = true
                }
            }
        })
    }

    private fun checkAllFlags() {
        BleDebugLog.i(logTag, "checkAllFlags-()")

        BleDebugLog.d(logTag, "nameFlag: $nameFlag , dateFlag : $dateFlag ,&& weightFlag: $weightFlag, && phoneFlag : $phoneFlag ,&& addressFlag : $addressFlag , && diagnosisFlag: $diagnosisFlag")

        if ( nameFlag && dateFlag && weightFlag && phoneFlag && addressFlag && diagnosisFlag) {
            BleDebugLog.d(logTag, "모든조건 완성, 유저 정보 수정합시다!")
            processEditInfo()
        } else {
            if (!nameFlag) {
                Toast.makeText(context, "Please fill out all items", Toast.LENGTH_SHORT).show()
                binding.nameStateTextView.visibility = View.VISIBLE
                binding.name.infoInputEditText.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.red_edittext_rounded_corner_rectangle, null)
            } else if (!dateFlag) {
                Toast.makeText(context, "Please fill out all items", Toast.LENGTH_SHORT).show()
                showBirthError()
            } else if (!weightFlag) {
                Toast.makeText(context, "Please fill out all items", Toast.LENGTH_SHORT).show()
                binding.weightStateTextView.visibility = View.VISIBLE
                binding.weightSpinner.weightEditText.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.red_edittext_rounded_corner_rectangle, null)
            } else if (!phoneFlag) {
                Toast.makeText(context, "Please fill out all items", Toast.LENGTH_SHORT).show()
                binding.phoneNotTextView.visibility = View.VISIBLE
                binding.phone.infoInputEditText.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.red_edittext_rounded_corner_rectangle, null)
            } else if (!addressFlag) {
                Toast.makeText(context, "Please fill out all items", Toast.LENGTH_SHORT).show()
                binding.address.addressNotTextView.visibility = View.VISIBLE
                if(binding.address.address1.text.isEmpty()) {
                    binding.address.address1.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.red_edittext_rounded_corner_rectangle, null)
                } else if(binding.address.address2.text.isEmpty()) {
                    binding.address.address2.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.red_edittext_rounded_corner_rectangle, null)
                } else if(binding.address.address3.text.isEmpty()) {
                    binding.address.address3.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.red_edittext_rounded_corner_rectangle, null)
                } else if(binding.address.address4.text.isEmpty()) {
                    binding.address.address4.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.red_edittext_rounded_corner_rectangle, null)
                }
            } else if (!diagnosisFlag) {
                Toast.makeText(context, "Please fill out all items", Toast.LENGTH_SHORT).show()
                showDiagnosisError()
            }
        }
    }

    private fun showBirthError() {
        binding.birthStateTextView.visibility = View.VISIBLE
        binding.birth.infoInputEditText.background =
            ResourcesCompat.getDrawable(resources, R.drawable.red_edittext_rounded_corner_rectangle, null)
        dateFlag = false
    }

    private fun showDiagnosisError() {
        binding.diagStateTextView.visibility = View.VISIBLE
        binding.diagDate.infoInputEditText.background =
            ResourcesCompat.getDrawable(resources, R.drawable.red_edittext_rounded_corner_rectangle, null)
        diagnosisFlag = false
    }

    private fun getUserInfo() {
        BleDebugLog.i(logTag, "getUserInfo-()")
        editInfoViewModel.getUserFromServer { userInfo ->
            BleDebugLog.d(logTag, "정상적으로 userInfo 가져왔음")
            userInfo?.let { settingCrtUserInfo(it) }
        }
    }
    //TODO :: 기존 유저 정보 세팅, 국가 빼고 완료
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
        binding.diagDate.infoInputEditText.setText(userInfo.diagDate)
        // spinner: race, pathology, spinner: country
        if (userInfo.gender == "female") { binding.gender.femaleBtn.isChecked = true } else { binding.gender.maleBtn.isChecked = true }
        if (userInfo.weightUnit == "kg") { binding.weightSpinner.spinner.setSelection(0) } else { binding.weightSpinner.spinner.setSelection(1) }
        getRace(userInfo.race)?.let {
            binding.raceSpinner.spinner.setSelection(it)
        }
        getPathology(userInfo.pathology)?.let {
            binding.pathSpinner.spinner.setSelection(it)
        }
        binding.countryTextView.text = userInfo.country
    }

    private fun processEditInfo() {
        BleDebugLog.i(logTag, "processEditInfo-()")
        // 유저가 입력한 정보 모두 가져오기
        val name = binding.name.infoInputEditText.text.toString()
        val birth = binding.birth.infoInputEditText.text.toString()
        val gender = if (binding.gender.femaleBtn.isChecked) "female" else "male"
        val weight = binding.weightSpinner.weightEditText.text.toString()
        val weightUnit = if (binding.weightSpinner.spinner.selectedItem.toString() == "kg") "kg" else "lbs"
        val race = binding.raceSpinner.spinner.selectedItem.toString()
        val pathology = binding.pathSpinner.spinner.selectedItem.toString()
        val diagDate = binding.diagDate.infoInputEditText.text.toString()
        val phone = binding.phone.infoInputEditText.text.toString()
        val address1 = binding.address.address1.text.toString()
        val address2 = binding.address.address2.text.toString()
        val address3 = binding.address.address3.text.toString()
        val address4 = binding.address.address4.text.toString()
        val country = binding.countryTextView.text.toString()

        // TODO:: UserInfoEntity 세팅
        val editedUserInfo = UserInfoEntity(
            email = App.prefs.getString("email", "no email"),
            vender = null,
            pwd = null,
            name = name,
            birth = birth,
            gender = gender,
            weight = weight,
            weightUnit = weightUnit,
            race = race,
            pathology = pathology,
            diagDate = diagDate,
            phone = phone,
            address1 = address1,
            address2 = address2,
            address3 = address3,
            zipCode = address4,
            country = country,
            membership = null
        )

        editInfoViewModel.editUserInfo(editedUserInfo) { isEdited ->
            if (isEdited) {
                // TODO:: 수정 완료 후 화면 처리
                BleDebugLog.d(logTag, "최종 수정 완료!!!!!!!!")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        BleDebugLog.i(logTag, "onDestroy-()")
    }
}