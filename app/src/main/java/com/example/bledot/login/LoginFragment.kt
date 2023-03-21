package com.example.bledot.login

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.example.bledot.App
import com.example.bledot.R
import com.example.bledot.activity.main.MainActivity
import com.example.bledot.databinding.FragmentLoginBinding
import com.example.bledot.util.BleDebugLog
import com.example.bledot.util.userId
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException


class LoginFragment : Fragment() {

    private val logTag = LoginFragment::class.simpleName
    private lateinit var binding: FragmentLoginBinding
    private val loginViewModel: LoginViewModel by activityViewModels()

    private var mGoogleSignInClient : GoogleSignInClient? = null

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
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BleDebugLog.i(logTag, "onViewCreated-()")
        // 로그인 버튼
        binding.signInBtn.setOnClickListener {
            processLogin()
        }
        // 비밀번호 찾기 버튼
        binding.findPwTextView.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.findFragment)
        }
        // 회원가입 버튼
        binding.signUpTextView.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.signupFragment)
        }
        // 구글로그인 버튼
        binding.googleBtn.setOnClickListener {
            googleLogin()
        }
        // 구글 자동로그인
        //checkAutoGoogle()

        // 일반 자동로그인
        checkAutoLogin()
    }

    private fun processLogin() {
        BleDebugLog.i(logTag, "processLogin-()")

        val inputEmail = binding.editTextEmail.text.toString()
        val inputPw = binding.editTextPw.text.toString()
        BleDebugLog.d(logTag, "inputEmail: $inputEmail, inputPw: $inputPw")

        if (inputEmail.isNotEmpty() && inputPw.isNotEmpty()) {
            loginViewModel.normalLogin(inputEmail, inputPw) {
                if (it) {
                    // 일반 로그인 성공 후 Preference 저장
                    App.prefs.setString("email", inputEmail)
                    userId.value = inputEmail // 추후 Api 에서 필요한 {userId} 저장
                    activity?.startActivity(Intent(activity, MainActivity::class.java))
                    activity?.finish()
                } else {
                    showDialogComplete("Notice", "Please check your email and password.")
                }
            }
        }
    }

    private fun googleLogin() {
        BleDebugLog.i(logTag, "googleLogin-()")
        val signInIntent = mGoogleSignInClient?.signInIntent
        resultLauncher.launch(signInIntent)
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val email = account.email
                BleDebugLog.d(logTag, "googleEmail: $email")
                // Signed in successfully, handle the user's account here
                // 구글로그인 후, 유저정보 있는지 없는지 체크
                email?.let {
                    userId.value = email // 추후 Api 에서 필요한 {userId} 저장
                    isCheckedUserInfo(it)
                }
            } catch (e: ApiException) {
                // The Google Sign In failed, handle this here
            }
        }
    }

    private fun checkAutoGoogle() {
        BleDebugLog.i(logTag, "checkAutoGoogle-()")
        val gsa: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(requireActivity())
        gsa?.let {
            BleDebugLog.d(logTag, "구글 로그인 이력 있음")
            BleDebugLog.d(logTag, "email: ${gsa.email}")
            userId.value = gsa.email // 추후 Api 에서 필요한 {userId} 저장
            activity?.startActivity(Intent(activity, MainActivity::class.java))
            activity?.finish()
        }
    }

    private fun isCheckedUserInfo(email: String) {
        loginViewModel.checkUserInfo(email) { isExist ->
            if (isExist) { // 유저 정보 O -> 로그인 api
                // 구글(소셜) 로그인 api
                loginViewModel.googleLogin(email) { result ->
                    if (result) {
                        activity?.startActivity(Intent(activity, MainActivity::class.java))
                        activity?.finish()
                    } else {
                        // TODO:: 계정은 있는데, 구글 로그인 실패. 다시 시도 요청?
                    }
                }
            } else { // 유저 정보 X -> 회원가입 화면으로 이동
                val navAction = LoginFragmentDirections.actionLoginFragmentToSignUpFragment(email)
                Navigation.findNavController(binding.root).navigate(navAction)
            }
        }
    }

    private fun checkAutoLogin() {
        BleDebugLog.i(logTag, "checkAutoLogin-()")
        // 일반 로그인 > 자동 로그인 체크
        val isToken = App.prefs.getString("token", "no token")
        BleDebugLog.d(logTag, "isToken: $isToken")
        if(isToken != "no token") {
            //userId.value = preEmail // 추후 Api 에서 필요한 {userId} 저장
            activity?.startActivity(Intent(activity, MainActivity::class.java))
            activity?.finish()
        }
    }

    private fun showDialogComplete(title: String, subTitle: String) {
        val builder = AlertDialog.Builder(context).apply {
            setTitle(title)
            setMessage(subTitle)
            setCancelable(false)
            setPositiveButton("Action") { _, _ -> }
        }
        builder.create().show()
    }
}