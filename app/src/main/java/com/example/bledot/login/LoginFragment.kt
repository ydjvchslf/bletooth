package com.example.bledot.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bledot.databinding.FragmentLoginBinding

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
}