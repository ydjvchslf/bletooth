package com.example.bledot.signup

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
import com.example.bledot.R
import com.example.bledot.databinding.FragmentSignupBinding
import com.example.bledot.util.BleDebugLog
import java.util.regex.Pattern


class SignupFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private val logTag = SignupFragment::class.simpleName
    private lateinit var binding: FragmentSignupBinding
    private val signupViewModel: SignupViewModel by activityViewModels()
    // 회원가입 처리시 필요한 flag
    private var nameFlag = false
    private var dateFlag = false
    private var weightFlag = false
    private var emailFlag = false
    private var pwFlag = false
    private var phoneFlag = false
    private var addressFlag = false
    private var agreeFlag = false

    val REG = "\\d{4}/(0[1-9]|1[012])/(0[1-9]|[12][0-9]|3[01])"

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
        // 필드 형식 안맞을 시 빨간 테두리 & 문구
        checkNameGuide()
        checkDateGuide()
        checkWeightGuide()
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

    private fun checkNameGuide() {
        binding.name.infoInputEditText.addTextChangedListener(object: TextWatcher{
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

                        dateFlag = false
                    } else if (textlength == 7 && before != 1) {
                        binding.birth.infoInputEditText.setText(binding.birth.infoInputEditText.text.toString() + "/")
                        binding.birth.infoInputEditText.setSelection(binding.birth.infoInputEditText.text.length)

                        dateFlag = false
                    } else if (textlength == 5 && !binding.birth.infoInputEditText.text.toString().contains("/")) {
                        binding.birth.infoInputEditText.setText(binding.birth.infoInputEditText.text.toString()
                                .substring(0, 4) + "/" + binding.birth.infoInputEditText.text.toString()
                                .substring(4))
                        binding.birth.infoInputEditText.setSelection(binding.birth.infoInputEditText.text.length)

                        dateFlag = false
                    } else if (textlength == 8 && binding.birth.infoInputEditText.text.toString().substring(7, 8) != "/") {
                        binding.birth.infoInputEditText.setText(
                        binding.birth.infoInputEditText.text.toString()
                                .substring(0, 7) + "/" + binding.birth.infoInputEditText.text.toString()
                                .substring(7))
                        binding.birth.infoInputEditText.setSelection(binding.birth.infoInputEditText.text.length)

                        dateFlag = false
                        Toast.makeText(context, "dateFlag => $dateFlag", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun afterTextChanged(p0: Editable?) {
                val inputText = binding.birth.infoInputEditText.text.toString()
                //val pattern = Pattern.compile(REG)
                val result = Pattern.matches(REG, inputText)
                Toast.makeText(context, "result => $result", Toast.LENGTH_SHORT).show()
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
                    nameFlag = true
                }
            }
        })
    }
}