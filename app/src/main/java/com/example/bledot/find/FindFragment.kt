package com.example.bledot.find

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
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
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.example.bledot.R
import com.example.bledot.databinding.FragmentFindBinding
import com.example.bledot.util.BleDebugLog
import java.util.regex.Pattern


class FindFragment : Fragment() {

    private val logTag = FindFragment::class.simpleName
    private lateinit var binding: FragmentFindBinding
    private val findViewModel: FindViewModel by activityViewModels()

    private var emailFlag = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_find, container, false)
        with(binding) {
            viewModel = findViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BleDebugLog.i(logTag, "onViewCreated-()")
        checkEmailGuide()
        binding.findPwBtn.setOnClickListener {
            if(emailFlag) {
                val userEmail = binding.editTextEmail.text.toString()
                // TODO:: db에 존재하는 email 인지 확인
                findViewModel.isValidEmail(userEmail) { isExist ->
                    if (isExist) {
                        showDialog("Complete", "[$userEmail]\nPassword reset mail has been sent.")
                    } else {
                        Toast.makeText(activity, "존재하지 않는 이메일입니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        binding.signInBtn.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(FindFragmentDirections.actionFindFragmentToLoginFragment())
        }
        binding.backBtn.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(FindFragmentDirections.actionFindFragmentToLoginFragment())
        }
    }

    private fun checkEmailGuide() {
        binding.editTextEmail.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.editTextEmail.text.isEmpty()) {
                    binding.emailStateTextView.visibility = View.VISIBLE
                    binding.editTextEmail.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.red_edittext_rounded_corner_rectangle, null)
                    emailFlag = false
                } else {
                    val emailValidation = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
                    var userEmail = binding.editTextEmail.text.toString().trim()
                    val emailGuide = Pattern.matches(emailValidation, userEmail)
                    if (emailGuide) {
                        //이메일 형태가 정상일 경우
                        binding.emailStateTextView.visibility = View.GONE
                        binding.editTextEmail.background =
                            ResourcesCompat.getDrawable(resources, R.drawable.edittext_rounded_corner_rectangle, null)
                        emailFlag = true
                    } else {
                        binding.emailStateTextView.visibility = View.VISIBLE
                        binding.editTextEmail.background =
                            ResourcesCompat.getDrawable(resources, R.drawable.red_edittext_rounded_corner_rectangle, null)
                        emailFlag = false
                    }
                }
            }
        })
    }

    private fun showDialog(title: String, subTitle: String) {
        val builder = AlertDialog.Builder(context).apply {
            setTitle(title)
            setMessage(subTitle)
            setCancelable(false)
            setPositiveButton("YES") { _, _ -> }
            //setNegativeButton("NO") { _, _ -> }
        }
        builder.create().show()
    }
}