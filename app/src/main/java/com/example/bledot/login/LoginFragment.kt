package com.example.bledot.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.bledot.R
import com.example.bledot.activity.main.MainActivity
import com.example.bledot.databinding.FragmentLoginBinding
import com.example.bledot.util.BleDebugLog
import kotlin.system.exitProcess


class LoginFragment : Fragment() {

    private val logTag = LoginFragment::class.simpleName
    private var mBinding: FragmentLoginBinding? = null
    private val binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BleDebugLog.i(logTag, "onViewCreated-()")
        // 로그인 버튼
        binding.signInBtn.setOnClickListener {
            // mainActivity 띄우기
            activity?.startActivity(Intent(activity, MainActivity::class.java))
            // TODO:: 정상 로그인 후, 기존 액티비티 제거할 것
            //activity?.finish()
        }
        // 비밀번호 찾기 버튼
        binding.findPwBtn.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_loginFragment_to_findFragment)
    }
        // 회원가입 버튼
        binding.signUpBtn.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_loginFragment_to_signUpFragment)
        }
    }
}