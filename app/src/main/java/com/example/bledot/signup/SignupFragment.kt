package com.example.bledot.signup

import android.annotation.SuppressLint
import android.content.Intent
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
import androidx.navigation.fragment.navArgs
import com.example.bledot.R
import com.example.bledot.activity.main.MainActivity
import com.example.bledot.data.UserInfoEntity
import com.example.bledot.databinding.FragmentSignupBinding
import com.example.bledot.util.*
import java.util.regex.Pattern


class SignupFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private val logTag = SignupFragment::class.simpleName
    private lateinit var binding: FragmentSignupBinding
    private val signupViewModel: SignupViewModel by activityViewModels()
    // 회원가입 처리시 필요한 flag
    private var nameFlag = false
    private var dateFlag = false
    private var weightFlag = false
    private var diagnosisFlag = false
    private var emailFlag = false
    private var emailDuplicationFlag = false
    private var pwFlag = false
    private var phoneFlag = false
    private var addressFlag = false
    private var agreeFlag = false
    //구글 로그인으로 넘어온 유저 email
    private val arg: SignupFragmentArgs by navArgs()

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
        // 구글로 넘어온 유저
        checkGoogleUser()

        makeDropdownMenu()
        // 필드 형식 안맞을 시 빨간 테두리 & 문구
        checkNameGuide()
        checkDateGuide()
        checkWeightGuide()
        checkEmailGuide()
        binding.emailCheckBtn.setOnClickListener {
            val inputEmail = binding.email.infoInputEditText.text.toString()
            if (inputEmail.isNotEmpty() && emailFlag) {
                signupViewModel.checkEmail(inputEmail) { isDuplicate ->
                    BleDebugLog.d(logTag, "isDuplicate: $isDuplicate")
                    if (isDuplicate) { // 중복됨
                        binding.emailOkTextView.visibility = View.GONE
                        binding.emailNotTextView.visibility = View.GONE
                        binding.emailDuplicationTextView.visibility = View.VISIBLE
                        emailDuplicationFlag = false
                        //Toast.makeText(context, "emailDuplicationFlag: $emailDuplicationFlag", Toast.LENGTH_SHORT).show()
                    } else { // 사용 가능
                        binding.emailOkTextView.visibility = View.VISIBLE
                        binding.emailNotTextView.visibility = View.GONE
                        binding.emailDuplicationTextView.visibility = View.GONE
                        emailDuplicationFlag = true
                        //Toast.makeText(context, "emailDuplicationFlag: $emailDuplicationFlag", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        checkPwGuide()
        checkPhoneGuide()
        checkAddressGuide()
        checkAgreeGuide()
        checkDiagnosisGuide()

        binding.signUpBtn.setOnClickListener {
            checkAllFlags()
        }

        binding.backBtn.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(SignupFragmentDirections.actionSignupFragmentToLoginFragment())
        }

        binding.signInBtn.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(SignupFragmentDirections.actionSignupFragmentToLoginFragment())
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

    private fun checkEmailGuide() {
        binding.email.infoInputEditText.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.email.infoInputEditText.text.isEmpty()) {
                    binding.emailNotTextView.visibility = View.VISIBLE
                    binding.email.infoInputEditText.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.red_edittext_rounded_corner_rectangle, null)
                    emailFlag = false
                } else {
                    val emailValidation = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
                    var userEmail = binding.email.infoInputEditText.text.toString().trim()
                    val emailGuide = Pattern.matches(emailValidation, userEmail)
                    if (emailGuide) {
                        //이메일 형태가 정상일 경우
                        binding.emailNotTextView.visibility = View.GONE
                        binding.email.infoInputEditText.background =
                            ResourcesCompat.getDrawable(resources, R.drawable.edittext_rounded_corner_rectangle, null)
                        emailFlag = true
                    } else {
                        binding.emailNotTextView.visibility = View.VISIBLE
                        binding.emailOkTextView.visibility = View.GONE
                        binding.email.infoInputEditText.background =
                            ResourcesCompat.getDrawable(resources, R.drawable.red_edittext_rounded_corner_rectangle, null)
                        emailFlag = false
                    }
                }
            }
        })
    }

    private fun checkPwGuide() {
        binding.pw.pw1EditText.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.pw.pw1EditText.text.isEmpty() || binding.pw.pw1EditText.text.toString().length < 8) {
                    binding.pw.pwLimitTextView.visibility = View.VISIBLE
                    binding.pw.pw1EditText.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.red_edittext_rounded_corner_rectangle, null)
                    pwFlag = false
                } else {
                    binding.pw.pwLimitTextView.visibility = View.INVISIBLE
                    binding.pw.pw1EditText.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.edittext_rounded_corner_rectangle, null)
                }
            }
        })

        binding.pw.pw2EditText.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                if (binding.pw.pw1EditText.text.toString() != binding.pw.pw2EditText.text.toString()) {
                    binding.pwNotTextView.visibility = View.VISIBLE
                    binding.pw.pw1EditText.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.red_edittext_rounded_corner_rectangle, null)
                    binding.pw.pw2EditText.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.red_edittext_rounded_corner_rectangle, null)
                    pwFlag = false
                } else {
                    binding.pwNotTextView.visibility = View.INVISIBLE
                    binding.pw.pw1EditText.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.edittext_rounded_corner_rectangle, null)
                    binding.pw.pw2EditText.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.edittext_rounded_corner_rectangle, null)
                    pwFlag = true
                }
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
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

    private fun checkAgreeGuide() {
        binding.checkbox.setOnClickListener {
            agreeFlag = binding.checkbox.isChecked
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

    private fun checkAllFlags() {
        BleDebugLog.i(logTag, "checkAllFlags-()")
        BleDebugLog.d(logTag, "arg.email: ${arg.email}")
        arg.email?.let { // 구글 로그인으로 넘어온 유저는 이메일 중복체크 넘어감
            emailDuplicationFlag = true
        }

        BleDebugLog.d(logTag, "nameFlag: $nameFlag , dateFlag : $dateFlag ,&& weightFlag: $weightFlag , && emailFlag : $emailFlag ," +
                "&& pwFlag : $pwFlag ,&& phoneFlag : $phoneFlag ,&& addressFlag : $addressFlag ,&& agreeFlag: $agreeFlag, && emailDuplicationFlag: $emailDuplicationFlag, && diagnosisFlag: $diagnosisFlag")

        if ( nameFlag && dateFlag && weightFlag && emailFlag && pwFlag && phoneFlag && addressFlag && agreeFlag && emailDuplicationFlag && diagnosisFlag) {
            BleDebugLog.d(logTag, "모든조건 완성, Sign up 성공적")
            processSignUp()
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
            } else if (!emailFlag) {
                if(binding.email.infoInputEditText.text.isEmpty()) {
                    Toast.makeText(context, "Please fill out all items", Toast.LENGTH_SHORT).show()
                    binding.emailNotTextView.visibility = View.VISIBLE
                    binding.email.infoInputEditText.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.red_edittext_rounded_corner_rectangle, null)
                } else {
                    Toast.makeText(context, "Please check for email duplication", Toast.LENGTH_SHORT).show()
                }
            } else if (!pwFlag) {
                Toast.makeText(context, "Please fill out all items", Toast.LENGTH_SHORT).show()
                binding.pw.pwLimitTextView.visibility = View.VISIBLE
                binding.pw.pw1EditText.background =
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
            } else if (!agreeFlag) {
                Toast.makeText(context, "Please check the consent.", Toast.LENGTH_SHORT).show()
            } else if (!diagnosisFlag) {
                Toast.makeText(context, "Please fill out all items", Toast.LENGTH_SHORT).show()
                showDiagnosisError()
            }
        }
    }

    private fun checkGoogleUser() {
        if (arg.email != null) {
            Toast.makeText(context, "arg.email: ${arg.email}", Toast.LENGTH_SHORT).show()
            // Sign up 이메일 채우기, pw 삭제
            binding.email.infoInputEditText.apply {
                setText(arg.email)
                isEnabled = false
            }
            emailFlag = true
            binding.emailCheckBtn.visibility = View.GONE

            binding.pw.root.visibility = View.GONE
            pwFlag = true
        }
    }

    private fun processSignUp() {
        BleDebugLog.i(logTag, "processSignUp-()")
        // 유저가 입력한 정보 모두 가져오기
        val name = binding.name.infoInputEditText.text.toString()
        val birth = binding.birth.infoInputEditText.text.toString()
        val gender = if (binding.gender.femaleBtn.isChecked) "female" else "male"
        val weight = binding.weightSpinner.weightEditText.text.toString()
        val weightUnit = if (binding.weightSpinner.spinner.selectedItem.toString() == "kg") "kg" else "lbs"
        val race = binding.raceSpinner.spinner.selectedItem.toString()
        val pathology = binding.pathSpinner.spinner.selectedItem.toString()
        val diagDate = binding.diagDate.infoInputEditText.text.toString()
        val inputEmail = if (arg.email == null) { binding.email.infoInputEditText.text.toString() } else { arg.email }
        val vender = if (arg.email == null) { "email" } else { "google" }
        val pw = if (arg.email == null) { binding.pw.pw2EditText.text.toString() } else { null }
        val phone = binding.phone.infoInputEditText.text.toString()
        val address1 = binding.address.address1.text.toString()
        val address2 = binding.address.address2.text.toString()
        val address3 = binding.address.address3.text.toString()
        val address4 = binding.address.address4.text.toString()
        val country = binding.countryPicker.picker.selectedCountryName
        // UserInfoEntity 세팅
        val userInfoEntity = UserInfoEntity(
            email = inputEmail.toString(),
            vender = vender,
            pwd = pw,
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

        signupViewModel.signUp(userInfoEntity) { isRegistered ->
            if (isRegistered) {
                // TODO:: 로그인 후 홈 화면으로 전환
                BleDebugLog.d(logTag, "회원 가입, db 저장 성공!")
                activity?.startActivity(Intent(activity, MainActivity::class.java))
                activity?.finish()
            }
        }
    }
}