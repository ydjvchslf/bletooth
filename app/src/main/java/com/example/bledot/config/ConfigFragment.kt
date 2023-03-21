package com.example.bledot.config

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.bledot.App
import com.example.bledot.App.Companion.prefs
import com.example.bledot.PreferenceUtil
import com.example.bledot.R
import com.example.bledot.activity.before.BeforeActivity
import com.example.bledot.activity.main.MainActivity
import com.example.bledot.databinding.FragmentConfigBinding
import com.example.bledot.membership.MembershipFragmentDirections
import com.example.bledot.util.BleDebugLog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class ConfigFragment : Fragment() {

    private val logTag = ConfigFragment::class.simpleName
    private lateinit var binding: FragmentConfigBinding
    private val configViewModel: ConfigViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        BleDebugLog.i(logTag, "onCreateView-()")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_config, container, false)
        with(binding) {
            viewModel = configViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BleDebugLog.i(logTag, "onViewCreated-()")
        // 내 정보 보기
        configViewModel.getMyInfo {
            BleDebugLog.d(logTag, "userInfo: $it")
            binding.infoNameTextView.text = it.name
            binding.infoEmailTextView.text = it.email
        }
        // edit info 버튼
        binding.editInfoBtn.setOnClickListener {
            val crtUserInfo = configViewModel.crnUserInfo
            Navigation.findNavController(binding.root).navigate(ConfigFragmentDirections.actionConfigFragmentToEditInfoFragment(crtUserInfo))
        }
        // change pw 버튼
        binding.changePwBtn.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.changePwFragment)
        }
        // 탈퇴 버튼
        binding.leaveBtn.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.withdrawalFragment)
        }
        // membership 버튼
        binding.membershipBtn.setOnClickListener {
            val crtUserInfo = configViewModel.crnUserInfo
            Navigation.findNavController(binding.root).navigate(ConfigFragmentDirections.actionConfigFragmentToMembershipFragment(crtUserInfo))
        }
        // Logout 버튼
        binding.logoutBtn.setOnClickListener {
            showDialog("Logout", "Are you sure to logout?")
        }
    }

    private fun showDialog(title: String, subTitle: String) {
        val builder = AlertDialog.Builder(context).apply {
            setTitle(title)
            setMessage(subTitle)
            setCancelable(false)
            setPositiveButton("YES") { _, _ ->
                normalLogout()
                googleLogout()
            }
            setNegativeButton("NO") { _, _ -> }
        }
        builder.create().show()
    }

    @SuppressLint("CommitPrefEdits", "ApplySharedPref")
    private fun normalLogout() {
        BleDebugLog.i(logTag, "normalLogout-()")
        // Pref 초기화
        val token = App.prefs.getString("token", "no token")
        BleDebugLog.d(logTag, "초기화 전 token: $token")
        if (token != "no token") {
            val prefs : SharedPreferences? = context?.getSharedPreferences("prefs_name", Context.MODE_PRIVATE)
            val editor = prefs?.edit()
            editor?.remove("token")
            editor?.remove("email")
            editor?.clear()
            editor?.commit()
            activity?.startActivity(Intent(activity, BeforeActivity::class.java))
            activity?.finish()
        }
    }

    @SuppressLint("CommitPrefEdits", "ApplySharedPref")
    private fun googleLogout() {
        BleDebugLog.i(logTag, "googleLogout-()")
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        mGoogleSignInClient.signOut()

        activity?.startActivity(Intent(activity, BeforeActivity::class.java))
        activity?.finish()
    }
}