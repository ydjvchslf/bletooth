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
import com.example.bledot.util.BleDebugLog
import com.example.bledot.util.toolbarName

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BleDebugLog.i(logTag, "onViewCreated-()")

        toolbarName.value = "Config"

        binding.getBtn.setOnClickListener {
            configViewModel.getProductList {
                if (it == 200) {
                    Toast.makeText(context, "200 OK", Toast.LENGTH_SHORT).show()
                }
            }
        }
        // edit info 버튼
        binding.editInfoBtn.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(ConfigFragmentDirections.actionConfigFragmentToEditInfoFragment())
        }
        // change pw 버튼
        binding.changePwBtn.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.changePwFragment)
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
        // 일반로그인 경우, pref 초기화
        val preEmail = App.prefs.getString("email", "no history")
        BleDebugLog.d(logTag, "preEmail: $preEmail")
        if (preEmail != "no history") { // 일반로그인 상태
            BleDebugLog.d(logTag, "==일반로그인 로그아웃==")
            val prefs : SharedPreferences? = context?.getSharedPreferences("prefs_name", Context.MODE_PRIVATE)
            val editor = prefs?.edit()
            editor?.remove("email")
            editor?.clear()
            editor?.commit()
            activity?.startActivity(Intent(activity, BeforeActivity::class.java))
            activity?.finish()
        }
    }

    private fun googleLogout() {
        // TODO :: Firebase google sign out
    }
}