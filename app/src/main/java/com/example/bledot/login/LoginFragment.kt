package com.example.bledot.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.example.bledot.R
import com.example.bledot.activity.main.MainActivity
import com.example.bledot.ble.BleViewModel
import com.example.bledot.databinding.FragmentBleBinding
import com.example.bledot.databinding.FragmentLoginBinding
import com.example.bledot.util.BleDebugLog
import kotlin.system.exitProcess


class LoginFragment : Fragment() {

    private val logTag = LoginFragment::class.simpleName
    private lateinit var binding: FragmentLoginBinding
    private val loginViewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        with(binding) {
            viewModel = loginViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BleDebugLog.i(logTag, "onViewCreated-()")
        // 로그인 버튼
        binding.signInBtn.setOnClickListener {
            if (isCheckEmailAndPw()) {
                activity?.startActivity(Intent(activity, MainActivity::class.java))
                /*
                loginViewModel.login { retCode, userInfo ->
                    if (retCode == 200) {
                        Toast.makeText(context, "로그인 성공!", Toast.LENGTH_SHORT).show()
                        BleDebugLog.d(logTag, "userInfo: ${userInfo.toString()}")
                        // mainActivity 띄우기
                        activity?.startActivity(Intent(activity, MainActivity::class.java))
                        // TODO:: 정상 로그인 후, 기존 액티비티 제거할 것
                        //activity?.finish()
                    }
                    if (retCode == 5555) {
                        Toast.makeText(context, "계정 틀림, 로그인 에러!!", Toast.LENGTH_SHORT).show()
                    }
                }
                 */
            }
        }
        // 비밀번호 찾기 버튼
        binding.findPwTextView.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.findFragment)
        }
        // 회원가입 버튼
        binding.signUpTextView.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.signupFragment)
        }
    }

    private fun isCheckEmailAndPw(): Boolean {
        if (binding.editTextEmail.text.isEmpty() || binding.editTextPw.text.isEmpty()) {
            Toast.makeText(context, "Email 과 Password 입력해주세요", Toast.LENGTH_SHORT).show()
            return false
        } else {
            return true
        }
    }
}