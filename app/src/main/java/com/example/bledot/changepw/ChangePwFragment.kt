package com.example.bledot.changepw

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.example.bledot.R
import com.example.bledot.databinding.FragmentChangePwBinding
import com.example.bledot.editinfo.EditInfoFragmentDirections
import com.example.bledot.login.LoginFragmentDirections
import com.example.bledot.util.BleDebugLog
import com.example.bledot.util.userId
import java.util.*


class ChangePwFragment : Fragment() {

    private val logTag = ChangePwFragment::class.simpleName
    private lateinit var binding: FragmentChangePwBinding
    private val changePwViewModel: ChangePwViewModel by activityViewModels()
    // 비번 변경 필요한 flag
    private var pwFlag1 = false
    private var pwFlag2 = false
    private var timer: Timer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        BleDebugLog.i(logTag, "onCreateView-()")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_change_pw, container, false)
        with(binding) {
            viewModel = changePwViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BleDebugLog.i(logTag, "onViewCreated-()")
        binding.pw.pw1EditText.hint = "New Password"
        binding.pw.pw2EditText.hint = "Confirm Password"
        binding.currentPw.infoInputEditText.inputType = (InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD)

        checkCrtPw() // 현재 비밀번호 맞는지 확인
        checkPwGuide() // 새로운 비밀번호 맞는지 확인
        // 비밀번호 변경 버튼
        binding.changeBtn.setOnClickListener {
            changePassword()
        }
        // 뒤로가기 버튼
        binding.backBtn.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(ChangePwFragmentDirections.actionChangePwFragmentToConfigFragment())
        }
    }

    private fun checkCrtPw() {
        binding.currentPw.infoInputEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                // The user typed: start the timer
                timer = Timer()
                timer?.schedule(object : TimerTask() {
                    override fun run() {
                        // Do your actual work here
                        if (binding.currentPw.infoInputEditText.text.isEmpty()){
                            BleDebugLog.d(logTag, "isEmpty() 입니다")
                            return
                        }
                        val crtPw = binding.currentPw.infoInputEditText.text.toString().toInt()
                        BleDebugLog.d(logTag, "유저 입력한 현재 비번: $crtPw")
                        activity?.runOnUiThread {
                            changePwViewModel.isCheckedPw(crtPw) { isRight ->
                                if (isRight) {
                                    pwFlag1 = true
                                    binding.crtPwTextView.visibility = View.VISIBLE
                                    binding.crtNotTextView.visibility = View.GONE
                                } else {
                                    pwFlag1 = false
                                    binding.crtNotTextView.visibility = View.VISIBLE
                                    binding.crtPwTextView.visibility = View.GONE
                                }
                            }
                        }
                    }
                }, 1000L)
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // The user is typing: reset already started timer (if existing)
                if (timer != null) {
                    timer?.cancel()
                }
            }
        })
    }

    private fun checkPwGuide() {
        binding.pw.pw1EditText.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val length = binding.pw.pw1EditText.text.toString().length
                if (length == 0) {
                    pwFlag2 = false
                }
                if (length in 1..7) {
                    binding.pw.pwLimitTextView.visibility = View.VISIBLE
                    binding.pw.pw1EditText.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.red_edittext_rounded_corner_rectangle, null)
                    pwFlag2 = false
                } else if (7 < length) {
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
                    pwFlag2 = false
                } else {
                    binding.pwNotTextView.visibility = View.INVISIBLE
                    binding.pw.pw1EditText.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.edittext_rounded_corner_rectangle, null)
                    binding.pw.pw2EditText.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.edittext_rounded_corner_rectangle, null)
                    pwFlag2 = true
                }
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
        })
    }

    private fun changePassword() {
        BleDebugLog.i(logTag, "changePassword-()")
        if (pwFlag1 && pwFlag2) {
            val email = userId.value.toString()
            val inputPw = binding.pw.pw2EditText.text.toString()

            changePwViewModel.changePw(email, inputPw) {
                if (it) {
                    showDialog("Complete", "Your password has been changed.")
                }
            }
        } else {
            Toast.makeText(context, "pwFlag1: $pwFlag1, pwFlag2: $pwFlag2", Toast.LENGTH_SHORT).show()
            return
        }
    }

    private fun showDialog(title: String, subTitle: String) {
        val builder = AlertDialog.Builder(context).apply {
            setTitle(title)
            setMessage(subTitle)
            setCancelable(false)
            setPositiveButton("YES") { _, _ ->
                Navigation.findNavController(binding.root).navigate(ChangePwFragmentDirections.actionChangePwFragmentToConfigFragment())
            }
            //setNegativeButton("NO") { _, _ -> }
        }
        builder.create().show()
    }
}