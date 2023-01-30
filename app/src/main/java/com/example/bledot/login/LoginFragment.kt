package com.example.bledot.login

import android.app.Activity
import android.app.Activity.RESULT_OK
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
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*


class LoginFragment : Fragment() {

    private val logTag = LoginFragment::class.simpleName
    private lateinit var binding: FragmentLoginBinding
    private val loginViewModel: LoginViewModel by activityViewModels()

    private var mGoogleSignInClient : GoogleSignInClient? = null
    private var auth : FirebaseAuth? = null

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
        auth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BleDebugLog.i(logTag, "onViewCreated-()")
        // 로그인 버튼
        binding.signInBtn.setOnClickListener {
            if (isCheckedEmailAndPw()) {

                val receivedEmail = binding.editTextEmail.text.toString()
                val receivedPw = binding.editTextPw.text.toString()
                // 일반 로그인 성공 후 Preference 저장
                App.prefs.setString("email", receivedEmail)
                userId.value = receivedEmail // 추후 Api 에서 필요한 {userId} 저장
                activity?.startActivity(Intent(activity, MainActivity::class.java))
                activity?.finish()
                /*
                loginViewModel.login { retCode, userInfo ->
                    if (retCode == 200) {
                        Toast.makeText(context, "로그인 성공!", Toast.LENGTH_SHORT).show()
                        BleDebugLog.d(logTag, "userInfo: ${userInfo.toString()}")
                        // mainActivity 띄우기
                        activity?.startActivity(Intent(activity, MainActivity::class.java))
                        // 정상 로그인 후, Before 액티비티 제거
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
        // 구글로그인 버튼
        binding.googleBtn.setOnClickListener {
            googleLogin()
        }
        // 구글 자동로그인
        checkAutoGoogle()

        // 일반 자동로그인
        checkAutoLogin()
    }

    private fun isCheckedEmailAndPw(): Boolean {
        return if (binding.editTextEmail.text.isEmpty() || binding.editTextPw.text.isEmpty()) {
            Toast.makeText(context, "Email 과 Password 입력해주세요", Toast.LENGTH_SHORT).show()
            false
        } else {
            true
        }
    }

    private fun googleLogin() {
        BleDebugLog.i(logTag, "googleLogin-()")
        val signInIntent = mGoogleSignInClient?.signInIntent
        resultLauncher.launch(signInIntent)
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            BleDebugLog.d(logTag, "${result.data}")
            val data: Intent? = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account.idToken)
        }
    }

    private fun checkAutoGoogle() {
        auth?.currentUser?.getIdToken(true)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                BleDebugLog.d(logTag, "autoGoogle-()")
                val idToken = task.result.token
                val email = auth?.currentUser?.email
                BleDebugLog.d(logTag, "email: $email")
                userId.value = email // 추후 Api 에서 필요한 {userId} 저장
                activity?.startActivity(Intent(activity, MainActivity::class.java))
                activity?.finish()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth?.signInWithCredential(credential)?.addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                // 인증에 성공한 후, 현재 로그인된 유저의 정보를 가져올 수 있습니다.
                val email = auth?.currentUser?.email
                BleDebugLog.d(logTag, "idToken: $idToken, \n email: $email")
                // 구글로그인 후, 유저정보 있는지 없는지 체크
                email?.let {
                    userId.value = email // 추후 Api 에서 필요한 {userId} 저장
                    isCheckedUserInfo(it)
                }
            }
        }
    }

    private fun isCheckedUserInfo(email: String) {
        loginViewModel.checkUserInfo(email) { isExist ->
            if (isExist) {
                activity?.startActivity(Intent(activity, MainActivity::class.java))
                activity?.finish()
            } else {
                val navAction = LoginFragmentDirections.actionLoginFragmentToSignUpFragment(email)
                Navigation.findNavController(binding.root).navigate(navAction)
            }
        }
    }

    private fun checkAutoLogin() {
        // 일반 로그인 > 자동 로그인 체크
        val preEmail = App.prefs.getString("email", "no history")
        BleDebugLog.d(logTag, "preEmail: $preEmail")
        if(preEmail != "no history") {
            userId.value = preEmail // 추후 Api 에서 필요한 {userId} 저장
            activity?.startActivity(Intent(activity, MainActivity::class.java))
            activity?.finish()
        }
    }
}