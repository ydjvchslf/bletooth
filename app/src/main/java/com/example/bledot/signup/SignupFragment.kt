package com.example.bledot.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.bledot.R
import com.example.bledot.databinding.FragmentSignupBinding

class SignupFragment : Fragment() {

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

}