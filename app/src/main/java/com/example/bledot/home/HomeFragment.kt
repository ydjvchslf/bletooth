package com.example.bledot.home

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
import androidx.navigation.Navigation
import com.example.bledot.App
import com.example.bledot.R
import com.example.bledot.activity.before.BeforeActivity
import com.example.bledot.databinding.FragmentHomeBinding
import com.example.bledot.util.BleDebugLog
import com.example.bledot.util.appIsWorking
import com.example.bledot.util.userId


class HomeFragment : Fragment() {

    private val logTag = HomeFragment::class.simpleName
    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        BleDebugLog.i(logTag, "onCreateView-()")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        with(binding) {
            viewModel = homeViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BleDebugLog.i(logTag, "onViewCreated-()")
        // token 값 확인
        checkToken()
        // 버튼 > 네비게이션
        binding.listBtn.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(HomeFragmentDirections.actionHomeFragmentToListFragment())
        }
        binding.deviceBtn.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(HomeFragmentDirections.actionHomeFragmentToBleFragment())
        }
        binding.recordBtn.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(HomeFragmentDirections.actionHomeFragmentToRealtimeFragment())
        }
        binding.settingBtn.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(HomeFragmentDirections.actionHomeFragmentToConfigFragment())
        }

        // 미전송 데이터 확인
        //checkData()

        // 작업 중 화면 터치 불가
        appIsWorking.observe(viewLifecycleOwner) { isWorking ->
            preventTouchEvent(isWorking)
        }
    }

    private fun checkData() {
        homeViewModel.isExistData {
            if (0 < it) {
                // 파일이 1개 이상 존재 시, 다이얼로그 띄워줌
                showDialog("Data not uploaded",
                    "$it data found\n" +
                            "Do you want to re-upload?\n" +
                            "If you cancel, all data will be cleared.")
            }
        }
    }

    private fun showDialog(title: String, subTitle: String) {
        val builder = AlertDialog.Builder(context).apply {
            setTitle(title)
            setMessage(subTitle)
            setCancelable(false)
            setPositiveButton("Upload") { _, _ ->
                // 데이터 하나씩 업로드
                homeViewModel.uploadData { isUploaded ->
                    if (isUploaded) {
                        // 업로드 후 Re-Check
                        homeViewModel.isExistData { dataNum ->
                            if (dataNum == 0) {
                                showDialogComplete("Complete", "Data has been uploaded.")
                            }
                        }
                    } else {
                        checkData()
                    }
                }
            }
            setNegativeButton("Cancel") { _, _ ->
                homeViewModel.deleteAllData {
                    if (it) {
                        // 모두 삭제 후 Re-Check
                        homeViewModel.isExistData { dataNum ->
                            if (dataNum == 0) {
                                showDialogComplete("Cleared", "All data has been cleared.")
                            }
                        }
                    }
                }
            }
        }
        builder.create().show()
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

    private fun preventTouchEvent(isWorking: Boolean) {
        BleDebugLog.i(logTag, "preventTouchEvent-()")
        if (isWorking) {
            binding.listBtn.isEnabled = false
            binding.deviceBtn.isEnabled = false
            binding.recordBtn.isEnabled = false
            binding.settingBtn.isEnabled = false
        } else {
            binding.listBtn.isEnabled = true
            binding.deviceBtn.isEnabled = true
            binding.recordBtn.isEnabled = true
            binding.settingBtn.isEnabled = true
        }
    }

    private fun checkToken() {
        BleDebugLog.i(logTag, "checkToken-()")
        val token = App.prefs.getString("token", "no token")
        BleDebugLog.d(logTag, "홈화면 token: $token")
        // if (!isGoogleUser()) { App.prefs.setString("email", "${userId.value}") } // 일반 로그인만 pref 에 email 저장
    }
}